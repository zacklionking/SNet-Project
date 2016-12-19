package jcoolj.com.dribbble.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import jcoolj.com.base.view.ExpandableLayout;
import jcoolj.com.base.view.scrollable.ExtendAdapter;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.R;
import jcoolj.com.dribbble.bean.Comment;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.view.DribbbleView;

public class ShotCardAdapter extends ExtendAdapter {

    private Context context;
    private List<Comment> commentList;

    private OnCommentClickListener listener;
    public interface OnCommentClickListener {
        void onAvatarClick(User user);
        void onCommentClick(Comment comment);
    }

    public ShotCardAdapter(Context context, List<Comment> commentList){
        this.context = context;
        this.commentList = commentList;
    }

    public void setOnCommentClickListener(OnCommentClickListener listener){
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new CommentHolder(LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void setHeaderView(View view){
        super.setHeaderView(view);
    }

    @Override
    public Comment getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public void onBindViewHolder(int position, RecyclerView.ViewHolder holder) {
        CommentHolder commentHolder = (CommentHolder) holder;
        final Comment comment = commentList.get(position);
        Glide.with(context).load(comment.getUser().getAvatarUrl()).into(commentHolder.avatar);
        commentHolder.username.setText(comment.getUser().getName());
        commentHolder.comment.setText(Html.fromHtml(comment.getBody().replaceAll("<p>", "").replaceAll("</p>", "\n")));
        if(listener != null) {
            commentHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAvatarClick(comment.getUser());
                }
            });
            commentHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCommentClick(comment);
                }
            });
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    private static class CommentHolder extends RecyclerView.ViewHolder{
        ImageView avatar;
        TextView username;
        TextView comment;

        public CommentHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            username = (TextView) itemView.findViewById(R.id.user_name);
            comment = (TextView) itemView.findViewById(R.id.comment_body);
            comment.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }


}
