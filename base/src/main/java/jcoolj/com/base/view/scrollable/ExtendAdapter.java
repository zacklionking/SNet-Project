package jcoolj.com.base.view.scrollable;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import jcoolj.com.core.utils.Logger;

public abstract class ExtendAdapter extends RecyclerView.Adapter {

    private final static int TYPE_HEADER = 0x001;
    private final static int TYPE_CONTENT = 0x002;
    private final static int TYPE_FOOTER = 0x003;

    protected View headerView;
    protected View footerView;

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setHeaderView(View view){
        headerView = view;
    }

    public void setFooterView(View view){
        footerView = view;
    }

    public void removeHeaderView(){
        headerView = null;
        notifyDataSetChanged();
    }

    public void removeFooterView(){
        footerView = null;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    protected boolean isHeader(int position){
        return (headerView != null && position == 0) || (footerView != null && position == getItemCount() - 1);
    }

    public abstract Object getItem(int position);

    @Override
    public final int getItemCount() {
        int count = getCount();
        if(headerView != null)
            count ++;
        if(footerView != null)
            count ++;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if(headerView != null && position == 0)
            return TYPE_HEADER;
        else if(footerView != null && position == getItemCount() - 1)
            return TYPE_FOOTER;
        return TYPE_CONTENT;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_HEADER:
                return new ViewHolder(headerView);
            case TYPE_FOOTER:
                return new ViewHolder(footerView);
        }
        return onCreateViewHolder(parent);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType){
            case TYPE_HEADER:
                onBindHeader(holder);
                return;
            case TYPE_FOOTER:
                onBindFooter(holder);
                return;
            default:
                int count = 0;
                if(headerView != null)
                    count ++;
                final int adjPosition = position - count;
                if(adjPosition < getCount()) {
                    onBindViewHolder(adjPosition, holder);
                    if(listener != null){
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onItemClick(adjPosition);
                            }
                        });
                    }
                }
        }
    }

    public abstract int getCount();

    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

    public abstract void onBindViewHolder(int position, RecyclerView.ViewHolder holder);

    public void onBindHeader(RecyclerView.ViewHolder holder){ }

    public void onBindFooter(RecyclerView.ViewHolder holder){ }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
