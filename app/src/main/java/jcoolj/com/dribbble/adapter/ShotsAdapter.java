package jcoolj.com.dribbble.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;

import java.util.List;

import jcoolj.com.base.view.scrollable.ExtendAdapter;
import jcoolj.com.dribbble.R;
import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.utils.Navigator;

public class ShotsAdapter extends ExtendAdapter {

    private List<Shot> shots;

    private Context context;
    private LayoutInflater inflater;

    public ShotsAdapter(Context context, List<Shot> shots){
        this.context = context;
        this.shots = shots;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Shot getItem(int position) {
        return shots.get(position);
    }

    @Override
    public int getCount() {
        return shots.size() % 2 == 0? shots.size() : shots.size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ShotHolder(inflater.inflate(R.layout.item_shot, parent, false));
    }

    @Override
    public void onBindViewHolder(int position, RecyclerView.ViewHolder holder) {
        final ShotHolder h = (ShotHolder) holder;
        if(position >= shots.size())
            return;
        final Shot shot = shots.get(position);
        h.tag.setVisibility(shot.isAnimated() ? View.VISIBLE : View.GONE);
        h.error.setVisibility(View.GONE);
        Glide.with(context).load(shot.getTeaserUrl()).centerCrop().into(new GlideDrawableImageViewTarget(h.img) {
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                h.error.setVisibility(View.VISIBLE);
            }
        });
    }

    private class ShotHolder extends RecyclerView.ViewHolder{
        ImageView img;
        View tag;
        View error;
        TextView loadingText;

        public ShotHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.shot_img);
            tag = itemView.findViewById(R.id.shot_animated_btn);
            tag.setAlpha(0.6f);
            error = itemView.findViewById(R.id.img_error);
            loadingText = (TextView) itemView.findViewById(R.id.loading_text);
        }
    }

}
