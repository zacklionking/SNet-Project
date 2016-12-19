package jcoolj.com.dribbble.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import jcoolj.com.dribbble.R;
import jcoolj.com.dribbble.bean.Attachment;

public class AttachmentsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Attachment> attachmentList;

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Attachment attachment);
    }

    public AttachmentsAdapter(Context context, List<Attachment> attachmentList){
        this.context = context;
        this.attachmentList = attachmentList;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView img = new ImageView(context);
        img.setScaleType(ImageView.ScaleType.CENTER);
        img.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, context.getResources().getDimensionPixelSize(R.dimen.attachments_height)));
        return new AttachmentHolder(img);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Glide.with(context).load(attachmentList.get(position).getThumbUrl()).into((ImageView) holder.itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(attachmentList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }

    private static class AttachmentHolder extends RecyclerView.ViewHolder{
        public AttachmentHolder(View itemView) {
            super(itemView);
        }
    }

}
