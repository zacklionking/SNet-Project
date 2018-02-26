package jcoolj.com.dribbble.view;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import jcoolj.com.base.anim.PulseAnimator;
import jcoolj.com.base.view.ExpandableLayout;
import jcoolj.com.base.view.scrollable.ExtendRecyclerView;
import jcoolj.com.base.view.scrollable.ScrollBehavior;
import jcoolj.com.base.view.scrollable.ScrollState;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.dribbble.R;
import jcoolj.com.dribbble.data.FavoritesManager;
import jcoolj.com.dribbble.data.FavoritesProvider;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.adapter.AttachmentsAdapter;
import jcoolj.com.dribbble.adapter.ShotCardAdapter;
import jcoolj.com.dribbble.bean.Attachment;
import jcoolj.com.dribbble.bean.Comment;
import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.data.ShotsManager;
import jcoolj.com.dribbble.utils.Navigator;

public class ShotCard extends FrameLayout implements Subscriber<List<Comment>>, LoaderManager.LoaderCallbacks<Cursor>, ScrollBehavior, View.OnClickListener {

    private static final int DEFAULT_COMMENTS_PER_PAGE = 10;

    private ShotsManager shotsManager;
    private Subscriber<List<Attachment>> attachmentSubscriber;

    private Shot shot;
    private User user;

    private View view_user_container;
    private ExtendRecyclerView view_commentList;
    private ImageView view_avatar;
    private TextView view_username;
    private TextView view_teamName;
    private TextView view_shotTitle;
    private TextView view_views;
    private TextView view_likes;
    private TextView view_comments;
    private ImageView view_shotImg;
    private TextView view_description;
    private DribbbleView view_dribbble;
    private TextView view_shot_loading;
    private TextView view_comments_loading;

    private View btn_like;
    private boolean isLikeClick;

    private ExpandableLayout view_attachmentContainer;
    private RecyclerView view_attachments;
    private List<Attachment> attachmentsList;

    private ShotCardAdapter shotCardAdapter;
    private List<Comment> commentsList;

    private boolean isCommentsLoading = true;
    private boolean isCommentsLoadingError;

    private Animation anim_fadeIn;

    public ShotCard(Context context) {
        this(context, null);
    }

    public ShotCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        attachmentsList = new ArrayList<>();
        commentsList = new ArrayList<>();
        shotsManager = new ShotsManager();
        init(context);
    }

    private void init(Context context){
        inflate(context, R.layout.view_shot_card, this);

        view_commentList = (ExtendRecyclerView) findViewById(R.id.shot_comments);
        view_commentList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        shotCardAdapter = new ShotCardAdapter(context, commentsList);
        view_commentList.setAdapter(shotCardAdapter);
        view_commentList.addScrollViewCallbacks(this);
        view_commentList.setItemAnimator(null);

        // 处理圆角背景在某些机型上莫名消失的兼容性问题
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            view_commentList.setLayerType(LAYER_TYPE_SOFTWARE, null);

        View header = LayoutInflater.from(context).inflate(R.layout.view_shot_card_header, this, false);
        view_user_container = header.findViewById(R.id.user_container);
        view_shotTitle = (TextView) header.findViewById(R.id.shot_title);
        view_avatar = (ImageView) header.findViewById(R.id.user_avatar);
        view_username = (TextView) header.findViewById(R.id.user_name);
        view_teamName = (TextView) header.findViewById(R.id.team_name);
        view_views = (TextView) header.findViewById(R.id.views);
        view_likes = (TextView) header.findViewById(R.id.likes);
        view_comments = (TextView) header.findViewById(R.id.comments);
        view_attachmentContainer = (ExpandableLayout) header.findViewById(R.id.shot_attachments_container);
        view_teamName.setOnClickListener(this);
        btn_like = header.findViewById(R.id.btn_like);
        btn_like.setOnClickListener(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.title_bar_height);
        header.setLayoutParams(params);
        view_commentList.setHeaderView(header);

        View footer = LayoutInflater.from(context).inflate(R.layout.view_loading_footer, this, false);
        view_comments_loading = (TextView) footer.findViewById(R.id.loading_text);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCommentsLoadingError) {
                    isCommentsLoadingError = false;
                    shotsManager.getComments(ShotCard.this, shot.getId(), DEFAULT_COMMENTS_PER_PAGE);
                }
            }
        });
        view_commentList.setFooterView(footer);

        view_shotImg = (ImageView) header.findViewById(R.id.shot_detail_img);
        view_shotImg.setOnClickListener(this);
        view_dribbble = (DribbbleView) header.findViewById(R.id.dribbble);
        view_shot_loading = (TextView) header.findViewById(R.id.loading_tips);
        anim_fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.anim_fade_in);

        view_attachments = (RecyclerView) header.findViewById(R.id.shot_attachments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        view_attachments.setLayoutManager(linearLayoutManager);
        AttachmentsAdapter adapter = new AttachmentsAdapter(context, attachmentsList);
        adapter.setOnItemClickListener(new AttachmentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Attachment attachment) {
                Navigator.navigateToImageViewer(getContext(), attachment.getUrl());
            }
        });
        view_attachments.setAdapter(adapter);

        view_description = (TextView) header.findViewById(R.id.shot_description);
        view_description.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setOnCommentClickListener(ShotCardAdapter.OnCommentClickListener listener){
        shotCardAdapter.setOnCommentClickListener(listener);
    }

    public void setShot(Shot shot){
        if(this.shot == null || !this.shot.equals(shot)) {
            this.shot = shot;
            User user = shot.getUser();
            if(user != null) {
                if(this.user == null || !this.user.equals(user)) {
                    this.user = user;
                    if(shot.getTeam() != null){
                        User team = shot.getTeam();
                        view_username.setText(user.getName());
                        Glide.with(getContext()).load(user.getAvatarUrl()).into(view_avatar);
                        view_username.append(" ");
                        view_teamName.setText(Html.fromHtml("<font color=\"#7f7f7f\">for </font>" + team.getName()));
                        view_teamName.setVisibility(VISIBLE);
                    } else {
                        Glide.with(getContext()).load(user.getAvatarUrl()).into(view_avatar);
                        view_username.setText(user.getName());
                    }
                }
            }
            attachmentsList.clear();
            view_attachments.getAdapter().notifyDataSetChanged();
            view_attachmentContainer.hide();
            commentsList.clear();
            shotCardAdapter.notifyDataSetChanged();
            isCommentsLoadingError = false;

            if(attachmentSubscriber == null)
                attachmentSubscriber = new Subscriber<List<Attachment>>() {
                    @Override
                    public void onSubscribe() {

                    }

                    @Override
                    public void onRefuse(Throwable e) {

                    }

                    @Override
                    public void onComplete(List<Attachment> attachments) {
                        if(attachments.size() > 0){
                            view_attachmentContainer.show();
                            attachmentsList.clear();
                            attachmentsList.addAll(attachments);
                            view_attachments.getAdapter().notifyDataSetChanged();
                        }
                    }
                };
            if(shot.getAttachments_count() > 0)
                shotsManager.getAttachments(attachmentSubscriber, shot.getId());
            if(shot.getComments_count() > 0) {
                isCommentsLoading = true;
                shotsManager.getComments(this, shot.getId(), DEFAULT_COMMENTS_PER_PAGE);
            } else
                view_comments_loading.setText(R.string.nothing_more);

            view_shotTitle.setText(shot.getTitle());

            Resources res = getResources();
            view_views.setText(Html.fromHtml("<b>" + shot.getViews_count() + "</b> " + res.getString(R.string.views)));
            view_likes.setText(Html.fromHtml("<b>" + shot.getLikes_count() + "</b> " + res.getString(R.string.likes)));
            view_comments.setText(Html.fromHtml("<b>" + shot.getComments_count() + "</b> " + res.getString(R.string.comments)));
            String description = shot.getDescription();
            view_description.setText((TextUtils.isEmpty(description) || description.equals("null")) ?
                    Html.fromHtml("<p>" + getContext().getString(R.string.no_description) + "</p>") : Html.fromHtml(description));
            loadShot();

            ((Activity) getContext()).getLoaderManager().initLoader(FavoritesManager.TYPE_SHOT, null, this);
        }
    }

    public void startShotDisplay() {
        // 重播GIF
        Drawable drawable = view_shotImg.getDrawable();
        if(drawable != null && drawable instanceof Animatable) {
            Animatable gif = (Animatable)drawable;
            gif.start();
        }
    }

    public void stopShotDisplay() {
        // 暂停GIF播放，减少开销
        Drawable drawable = view_shotImg.getDrawable();
        if(drawable != null && drawable instanceof Animatable) {
            Animatable gif = (Animatable)drawable;
            gif.stop();
        }
    }

    private void loadShot(){
        // 按原图尺寸加载图片
        Glide.with(getContext()).load(shot.getImgUrl()).placeholder(R.drawable.bg_shot_placeholder).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new GlideDrawableImageViewTarget(view_shotImg) {
            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
                view_dribbble.bounce();
                if(view_shot_loading != null)
                    view_shot_loading.setVisibility(GONE);
            }

            @Override
            protected void setResource(GlideDrawable resource) {
                super.setResource(resource);
                view_dribbble.stop();
            }

            @Override
            public void setDrawable(Drawable drawable) {
                super.setDrawable(drawable);
                view_dribbble.stop();
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                view_dribbble.stop();
                if(view_shot_loading != null) {
                    if(e != null)
                        view_shot_loading.setText(e.toString());
                    else
                        view_shot_loading.setText(getResources().getString(R.string.loading_fail));
                    view_shot_loading.startAnimation(anim_fadeIn);
                    view_shot_loading.setVisibility(VISIBLE);
                }
            }
        });
    }

    public void cancelShotLoading(){
        Glide.clear(view_shotImg);
    }

    public Shot getShot(){
        return shot;
    }

    public void setOnUserClickListener(OnClickListener listener){
        view_user_container.setOnClickListener(listener);
    }

    @Override
    public void onScrollChanged(ScrollState direction, int position) {

    }

    @Override
    public void onReachEnd() {
        if(isCommentsLoading)
            return;
        shotsManager.getComments(this, shot.getId(), DEFAULT_COMMENTS_PER_PAGE);
        isCommentsLoading = true;
    }

    @Override
    public void onScrollDirectionChanged(ScrollState direction) {

    }

    @Override
    public void onClick(View v) {
        if(v == btn_like){
            isLikeClick = true;
            if((boolean) btn_like.getTag())
                FavoritesManager.remove(getContext(), shot);
            else {
                FavoritesManager.add(getContext(), shot);
                PulseAnimator.pulse(btn_like);
            }
        } else if(v == view_shotImg) {
            if(view_shot_loading.getVisibility() == VISIBLE) {
                view_shot_loading.setVisibility(GONE);
                loadShot();
            } else if(shot != null) {
                Navigator.navigateToImageViewer(getContext(), shot.getImgUrl());
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        btn_like.setEnabled(false);
//        Logger.d("favorite start load");
        return new CursorLoader(getContext(), ContentUris.withAppendedId(FavoritesProvider.CONTENT_SHOT_URI, shot.getId()),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean isFind = data != null && data.moveToFirst();
        if(isLikeClick && isFind)
            Toast.makeText(getContext(), "Favorite shot added", Toast.LENGTH_SHORT).show();
        isLikeClick = false;
//        Logger.d("favorite load finished "+isFind);
        btn_like.setBackgroundResource(isFind ?
                R.drawable.ic_like_pressed : R.drawable.ic_like_normal);
        btn_like.setTag(isFind);
        btn_like.setEnabled(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onSubscribe() {
        view_comments_loading.setText(R.string.loading_comments);
    }

    @Override
    public void onRefuse(Throwable e) {
        isCommentsLoading = false;
        isCommentsLoadingError = true;
        view_comments_loading.setText(R.string.loading_fail);
    }

    @Override
    public void onComplete(List<Comment> comments) {
        if(comments.size() > 0){
            int offset = commentsList.size() + 1;
            commentsList.addAll(comments);
            // Header不需要更新
            shotCardAdapter.notifyItemRangeInserted(offset, comments.size());
            if(comments.size() < DEFAULT_COMMENTS_PER_PAGE)
                view_comments_loading.setText(R.string.nothing_more);
            else
                isCommentsLoading = false;
        } else
            view_comments_loading.setText(R.string.nothing_more);
    }

}
