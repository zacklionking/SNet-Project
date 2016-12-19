package jcoolj.com.dribbble.fragment;

import jcoolj.com.dribbble.R;

public class PopularFragment extends ShotsFragment {

    @Override
    public int getTitle() {
        return R.string.popular_title;
    }

    @Override
    public int getIcon() {
        return 0;
    }

    @Override
    public void load() {
        super.load();
        shotsManager.getShots(this);
    }

}
