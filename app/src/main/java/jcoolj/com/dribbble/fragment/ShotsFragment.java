package jcoolj.com.dribbble.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import jcoolj.com.base.PageFragment;
import jcoolj.com.base.utils.PixelUtils;
import jcoolj.com.base.view.scrollable.ExtendAdapter;
import jcoolj.com.base.view.scrollable.ExtendRecyclerView;
import jcoolj.com.base.view.scrollable.HeaderSpanSizeLookup;
import jcoolj.com.base.view.scrollable.ScrollBehavior;
import jcoolj.com.base.view.scrollable.ScrollState;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.R;
import jcoolj.com.dribbble.adapter.ShotsAdapter;
import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.data.ShotsManager;
import jcoolj.com.dribbble.utils.Navigator;
import jcoolj.com.base.OnLoadListener;

public abstract class ShotsFragment extends PageFragment implements ScrollBehavior, Subscriber<List<Shot>> {

    protected List<Shot> shots = new ArrayList<>();
    private ShotsAdapter adapter;

    protected ShotsManager shotsManager;

    protected OnLoadListener onLoadListener;
    protected boolean isRefreshing;       // 是否刷新，刷新则清空页面
    protected boolean isLoading;          // 是否正在加载中
    protected boolean isLoaded;           // 初次加载或刷新是否完成

    public ShotsFragment(){
        shotsManager = new ShotsManager();
        shotsManager.bindLifeCycleCallbacks(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ExtendRecyclerView gallery = (ExtendRecyclerView) inflater.inflate(R.layout.fragmant_shots, container, false);
        gallery.setHasFixedSize(true);
        gallery.addScrollViewCallbacks(this);

        final Context context = getContext();
        adapter = new ShotsAdapter(context, shots);
        adapter.setOnItemClickListener(new ExtendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(onLoadListener != null)
                    onLoadListener.onLoadFinished();
                Navigator.navigateToShotCard(context, adapter.getItem(position));
            }
        });
        gallery.setAdapter(adapter);

        View header = new View(context);
        header.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.getTitleBarHeight(context)));
        gallery.setHeaderView(header);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        gridLayoutManager.setSpanSizeLookup(new HeaderSpanSizeLookup(adapter, 2));
        gallery.setLayoutManager(gridLayoutManager);

        RelativeLayout rootView = new RelativeLayout(context);
        rootView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.addView(gallery);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isVisible && !isLoaded && !isLoading)
            load();
    }

    public void setOnLoadListener(OnLoadListener listener) {
        onLoadListener = listener;
    }

    public void load(){
        if(onLoadListener != null)
            onLoadListener.onLoadStarted();
    };

    @Override
    protected void onShowFragment() {
        if(!isLoaded && !isLoading)
            load();
    }

    @Override
    protected void onHideFragment() {
        if(onLoadListener != null)
            onLoadListener.onLoadFinished();
    }

    @Override
    public void refresh() {
        shotsManager.setShotsRequestPage(1);
        isRefreshing = true;
        load();
    }

    @Override
    public void onSubscribe() {
        isLoading = true;
    }

    @Override
    public void onRefuse(Throwable e) {
        isLoading = false;
        if(onLoadListener != null)
            onLoadListener.onLoadFailed();
    }

    @Override
    public void onComplete(List<Shot> shots) {
        isLoaded = true;
        isLoading = false;
        if(onLoadListener != null)
            onLoadListener.onLoadFinished();
        if(shots.size() > 0) {
            if(isRefreshing){
                isRefreshing = false;
                this.shots.clear();
            }
            this.shots.addAll(shots);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    // 到达底端自动加载更多
    public void onReachEnd() {
        if(isLoading || !isLoaded)
            return;
        load();
    }

    @Override
    public void onScrollDirectionChanged(ScrollState direction) {
        if (direction == ScrollState.UP) {
            getTitleBar().hide();
        } else if (direction == ScrollState.DOWN) {
            getTitleBar().show();
        }
    }

    @Override
    public void onScrollChanged(ScrollState direction, int position) {

    }

}
