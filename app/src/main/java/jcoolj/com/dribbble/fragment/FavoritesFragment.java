package jcoolj.com.dribbble.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcoolj.com.base.BaseActivity;
import jcoolj.com.base.PageFragment;
import jcoolj.com.base.utils.PixelUtils;
import jcoolj.com.base.view.scrollable.ExtendRecyclerView;
import jcoolj.com.base.view.scrollable.ScrollBehavior;
import jcoolj.com.base.view.scrollable.ScrollState;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.R;
import jcoolj.com.dribbble.adapter.LikesAdapter;
import jcoolj.com.dribbble.data.FavoritesManager;
import jcoolj.com.dribbble.data.FavoritesProvider;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.bean.Shot;

public class FavoritesFragment extends PageFragment implements LoaderManager.LoaderCallbacks<Cursor>, ScrollBehavior {

    private LikesAdapter adapter;
    private List<User> users = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ExtendRecyclerView listView = (ExtendRecyclerView) rootView.findViewById(R.id.favorite_list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.addScrollViewCallbacks(this);

        adapter = new LikesAdapter(getContext(), users);
        listView.setAdapter(adapter);

        View header = inflater.inflate(R.layout.view_blank_bar, container, false);
        header.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.getTitleBarHeight(getContext())));
        listView.setHeaderView(header);
        Logger.d("favorite onInflated");
        getLoaderManager().initLoader(FavoritesManager.TYPE_USER, null, this);
        return rootView;
    }

    @Override
    public int getTitle() {
        return R.string.favorite_title;
    }

    @Override
    public int getIcon() {
        return 0;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void load() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), FavoritesProvider.CONTENT_USER_URI,
                null, null, null, "_id desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && !data.isAfterLast()){
            Map<Long, List<Shot>> userShotsMap = new HashMap<>();
            for(User user : users){
                List<Shot> shots = user.getShots();
                if(shots != null)
                    userShotsMap.put(user.getId(), user.getShots());
            }
            users.clear();
            while (data.moveToNext()){
                Logger.d("favorite user:" + data.getString(1));
                User user = new User();
                user.setId(data.getLong(1));
                user.setAvatarUrl(data.getString(2));
                user.setName(data.getString(3));
                user.setBio(data.getString(4));
                user.setShots(userShotsMap.get(user.getId()));
                users.add(user);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onScrollChanged(ScrollState direction, int position) {

    }

    @Override
    public void onReachEnd() {

    }

    @Override
    public void onScrollDirectionChanged(ScrollState direction) {
        Activity activity = getActivity();
        if(activity != null && activity instanceof ScrollBehavior)
            ((ScrollBehavior) activity).onScrollDirectionChanged(direction);
    }

}
