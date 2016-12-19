package jcoolj.com.dribbble.view;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jcoolj.com.base.anim.PulseAnimator;
import jcoolj.com.base.view.scrollable.ExtendAdapter;
import jcoolj.com.base.view.scrollable.ExtendRecyclerView;
import jcoolj.com.base.view.scrollable.HeaderSpanSizeLookup;
import jcoolj.com.base.view.scrollable.ScrollBehavior;
import jcoolj.com.base.view.scrollable.ScrollState;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.R;
import jcoolj.com.dribbble.data.FavoritesManager;
import jcoolj.com.dribbble.data.FavoritesProvider;
import jcoolj.com.dribbble.data.TeamManager;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.adapter.ShotsAdapter;
import jcoolj.com.dribbble.utils.Navigator;
import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.data.ShotsManager;

public class UserCard extends FrameLayout implements ScrollBehavior, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DEFAULT_COMMENTS_PER_PAGE = 20;
    private static final int DEFAULT_MEMBERS_PER_LINE = 5;
    private static final int DEFAULT_MEMBERS_SINGLE_ADD = 2;    // Min number of last line

    private ShotsManager shotsManager;
    private Subscriber<List<Shot>> shotsSubscriber;
    private TeamManager teamManager;
    private Subscriber<List<User>> teamSubscriber;
    private User user;

    private ImageView view_avatar;
    private TextView view_username;
    private TextView view_location;
    private TextView view_bio;
    private LinearLayout view_members;
    private TextView view_shots_count;
    private TextView view_projects_count;
    private TextView view_followers_count;
    private View btn_like;
    private boolean isLikeClick;

    private List<Shot> shots;
    private ShotsAdapter adapter;

    private TextView view_loadingText;
    private boolean isLoading = true;
    private boolean isLoadingError;

    private boolean userChanged;

    public UserCard(Context context) {
        this(context, null);
    }

    public UserCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        shots = new ArrayList<>();
        shotsManager = new ShotsManager();
        teamManager = new TeamManager();
        init(context);
    }

    private void init(Context context){
        inflate(context, R.layout.view_user_card, this);

        ExtendRecyclerView gallery = (ExtendRecyclerView) findViewById(R.id.user_shots);
        gallery.setHasFixedSize(true);
        gallery.addScrollViewCallbacks(this);

        View header = LayoutInflater.from(context).inflate(R.layout.view_user_card_header, this, false);
        view_avatar = (ImageView) header.findViewById(R.id.user_avatar);
        view_username = (TextView) header.findViewById(R.id.user_name);
        view_location = (TextView) header.findViewById(R.id.user_location);
        view_bio = (TextView) header.findViewById(R.id.user_bio);
        view_members = (LinearLayout) header.findViewById(R.id.user_relatives);
        view_shots_count = (TextView) header.findViewById(R.id.shots_count);
        view_projects_count = (TextView) header.findViewById(R.id.projects_count);
        view_followers_count = (TextView) header.findViewById(R.id.followers);
        btn_like = header.findViewById(R.id.btn_like);
        btn_like.setOnClickListener(this);
        gallery.setHeaderView(header);

        View footer = LayoutInflater.from(context).inflate(R.layout.view_loading_footer, this, false);
        view_loadingText = (TextView) footer.findViewById(R.id.loading_text);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoadingError) {
                    isLoadingError = false;
                    view_loadingText.setText(R.string.loading_shots);
                    refresh();
                }
            }
        });
        gallery.setFooterView(footer);
        adapter = new ShotsAdapter(context, shots);
        gallery.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new HeaderSpanSizeLookup(adapter, 2));
        gallery.setLayoutManager(gridLayoutManager);
    }

    public void setUser(@NonNull User user){
        if(this.user == null || !this.user.equals(user)) {    // User changed, refresh UI
            userChanged = true;
            this.user = user;
            view_username.setText(user.getName());
            Glide.with(getContext()).load(user.getAvatarUrl()).into(view_avatar);
            String location = user.getLocation();
            if(location == null || location.length() == 0 || location.equals("null"))
                view_location.setVisibility(GONE);
            else {
                view_location.setVisibility(VISIBLE);
                view_location.setText(user.getLocation());
            }
            view_bio.setText(Html.fromHtml(user.getBio()));
            Resources res = getResources();
            view_shots_count.setText(Html.fromHtml("<b>" + user.getShotsCount() + "</b> " + res.getString(R.string.shots)));
            view_projects_count.setText(Html.fromHtml("<b>" + user.getProjectsCount() + "</b> " + res.getString(R.string.projects)));
            view_followers_count.setText(Html.fromHtml("<b>" + user.getFollowersCount() + "</b> " + res.getString(R.string.followers)));

            ((Activity) getContext()).getLoaderManager().initLoader(FavoritesManager.TYPE_USER, null, this);
        } else
            userChanged = false;
    }

    public void updateUser(@NonNull User user){
        if(this.user == null)
            return;
        if(this.user.equals(user) && !user.getAvatarUrl().equals(this.user.getAvatarUrl()))
            Glide.with(getContext()).load(user.getAvatarUrl()).into(view_avatar);
        this.user = user;
        view_username.setText(user.getName());
        String location = user.getLocation();
        if(location == null || location.length() == 0 || location.equals("null"))
            view_location.setVisibility(GONE);
        else {
            view_location.setVisibility(VISIBLE);
            view_location.setText(user.getLocation());
        }
        view_bio.setText(Html.fromHtml(user.getBio()));
        Resources res = getResources();
        view_shots_count.setText(Html.fromHtml("<b>" + user.getShotsCount() + "</b> " + res.getString(R.string.shots)));
        view_projects_count.setText(Html.fromHtml("<b>" + user.getProjectsCount() + "</b> " + res.getString(R.string.projects)));
        view_followers_count.setText(Html.fromHtml("<b>" + user.getFollowersCount() + "</b> " + res.getString(R.string.followers)));

        getTeamMembers(user.getId());
    }

    public void refresh(){
        if(userChanged) {
            shots.clear();
            shotsManager.setShotsRequestPage(1);
            getUserShots(user.getId());
            getTeamMembers(user.getId());
        }
    }

    public User getUser(){
        return user;
    }

    public Shot getShot(int position){
        if(position >= shots.size())
            return null;
        Shot shot = shots.get(position);
        shot.setUser(user);
        return shot;
    }

    public void setOnItemClickListener(ExtendAdapter.OnItemClickListener listener){
        adapter.setOnItemClickListener(listener);
    }

    // 获取用户Shots
    private void getUserShots(long userId) {
        if(shotsSubscriber == null)
            shotsSubscriber = new Subscriber<List<Shot>>() {
                @Override
                public void onSubscribe() {
                    view_loadingText.setText(R.string.loading_shots);
                }

                @Override
                public void onRefuse(Throwable e) {
                    isLoading = false;
                    isLoadingError = true;
                    view_loadingText.setText(R.string.loading_fail);
                }

                @Override
                public void onComplete(List<Shot> shots) {
                    Logger.d("get user shots: " + shots.size());
                    if(shots.size() > 0) {
                        UserCard.this.shots.addAll(shots);
                        adapter.notifyDataSetChanged();
                        if(shots.size() < DEFAULT_COMMENTS_PER_PAGE)
                            view_loadingText.setText(R.string.nothing_more);
                        else
                            isLoading = false;
                    } else {
                        view_loadingText.setText(R.string.nothing_more);
                    }
                    userChanged = false;
                }
            };
        shotsManager.getShots(shotsSubscriber, (int) userId);
    }

    // 获取Team成员，获取失败则不做任何处理
    private void getTeamMembers(long userId) {
        if(user.getMembersCount() > 0) {
            if (teamSubscriber == null)
                teamSubscriber = new Subscriber<List<User>>() {
                    @Override
                    public void onSubscribe() {

                    }

                    @Override
                    public void onRefuse(Throwable e) {

                    }

                    @Override
                    public void onComplete(List<User> members) {
                        int count = members.size();
                        int lines = count / DEFAULT_MEMBERS_PER_LINE + 1;
                        int firstLineCount = DEFAULT_MEMBERS_PER_LINE;
                        int offset = 0;
                        if (count % DEFAULT_MEMBERS_PER_LINE < DEFAULT_MEMBERS_SINGLE_ADD) {
                            lines--;
                            firstLineCount += count % DEFAULT_MEMBERS_PER_LINE;
                        }
                        Logger.d("firstLineCount:" + count);
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        view_members.removeAllViews();
                        for (int i = 0; i < lines; i++) {
                            LinearLayout lineContainer = new LinearLayout(getContext());
                            lineContainer.setOrientation(LinearLayout.HORIZONTAL);
                            lineContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            int lineCount = i == 0 ? firstLineCount : DEFAULT_MEMBERS_PER_LINE;
                            for (int j = 0; j < lineCount; j++) {
                                final User member = members.get(offset);
                                ImageView memberView = (ImageView) inflater.inflate(R.layout.item_member, lineContainer, false);
                                Glide.with(getContext()).load(member.getAvatarUrl()).into(memberView);
                                memberView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Navigator.navigateToUserCard(getContext(), member);
                                    }
                                });
                                lineContainer.addView(memberView);
                                if (++offset >= count)
                                    break;
                            }
                            view_members.addView(lineContainer);
                        }
                        view_members.setVisibility(count > 0 ? VISIBLE : GONE);
                    }
                };
            teamManager.getMembers(teamSubscriber, userId);
        }
    }

    @Override
    public void onScrollChanged(ScrollState direction, int position) {

    }

    @Override
    public void onReachEnd() {
        if(isLoading)
            return;
        isLoading = true;
        getUserShots(user.getId());
    }

    @Override
    public void onScrollDirectionChanged(ScrollState direction) {

    }

    @Override
    public void onClick(View v) {
        if(v == btn_like){
            isLikeClick = true;
            if((boolean) btn_like.getTag())
                FavoritesManager.remove(getContext(), user);
            else {
                FavoritesManager.add(getContext(), user);
                PulseAnimator.pulse(btn_like);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Logger.d("favorite user start load");
        btn_like.setEnabled(false);
        return new CursorLoader(getContext(), ContentUris.withAppendedId(FavoritesProvider.CONTENT_USER_URI, user.getId()),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean isFind = data != null && data.moveToFirst();
        if(isLikeClick && isFind)
            Toast.makeText(getContext(), "Favorite user added", Toast.LENGTH_SHORT).show();
        isLikeClick = false;
        Logger.d("favorite user load finished "+isFind);
        btn_like.setBackgroundResource(isFind ?
                R.drawable.ic_like_pressed : R.drawable.ic_like_normal);
        btn_like.setTag(isFind);
        btn_like.setEnabled(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
