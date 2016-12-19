package jcoolj.com.dribbble.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import jcoolj.com.base.view.scrollable.ExtendAdapter;
import jcoolj.com.dribbble.R;
import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.data.ShotsManager;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.utils.Navigator;

public class LikesAdapter extends ExtendAdapter {

    private static final int LIMIT_SHOTS = 3;

    private Context context;
    private List<User> users;

    private ShotsManager shotsAgency;

    public LikesAdapter(Context context, List<User> users){
        this.context = context;
        this.users = users;
//        shotsAgency = new ShotsManager(this);
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new LikesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(int position, RecyclerView.ViewHolder holder) {
        if(holder instanceof LikesViewHolder) {
            final User user = users.get(position);
            LikesViewHolder likeHolder = (LikesViewHolder) holder;
            Glide.with(context).load(user.getAvatarUrl()).into(likeHolder.avatar);
            likeHolder.username.setText(user.getName());
            likeHolder.userBio.setText(Html.fromHtml(user.getBio()));
//            if(user.getShots() == null)
//                shotsAgency.getShots(user.getId(), LIMIT_SHOTS);
//            else if(likeHolder.userShot.getTag() == null || !likeHolder.userShot.getTag().equals(user.getId())) {
//                for(int i = 0;i<user.getShots().size();i++){
//                    ImageView imageView = (ImageView) likeHolder.userShot.getChildAt(i);
//                    final Shot shot = user.getShots().get(i);
//                    Glide.with(context).load(shot.getTeaserUrl()).into(imageView);
//                    imageView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Navigator.navigateToShotCard(context, shot, true);
//                        }
//                    });
//                }
//                likeHolder.userShot.setTag(user.getId());
//            }
            likeHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigator.navigateToUserCard(context, user, true);
                }
            });
        }
    }

    public void onGetShots(List<Shot> shots) {
        if(shots.size() <= 0)
            return;
        for(int i=0; i<users.size(); i++){
            User user = users.get(i);
            if(user.getId() == shots.get(0).getUser().getId()) {
                user.setShots(shots);
                for(Shot shot : shots){
                    shot.setUser(user);
                }
                notifyDataSetChanged();
                break;
            }
        }
    }

    private static class LikesViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView username;
        private TextView userBio;
        private LinearLayout userShot;
        public LikesViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            username = (TextView) itemView.findViewById(R.id.user_name);
            userBio = (TextView) itemView.findViewById(R.id.user_bio);
            userShot = (LinearLayout) itemView.findViewById(R.id.user_shots);
            for(int i = 0;i<LIMIT_SHOTS;i++){
                ImageView imageView = new ImageView(itemView.getContext());
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                userShot.addView(imageView);
            }
        }
    }

}
