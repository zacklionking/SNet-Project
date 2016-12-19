package jcoolj.com.dribbble.fragment;

import jcoolj.com.dribbble.R;
import jcoolj.com.dribbble.data.ShotsManager;

public class RecentFragment extends ShotsFragment {

    @Override
    public int getTitle() {
        return R.string.recent_title;
    }

    @Override
    public int getIcon() {
        return 0;
    }

    @Override
    public void load() {
        super.load();
        shotsManager.getShots(this, ShotsManager.SORT_RECENT);
    }

}
