package jcoolj.com.base.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentManager;

import java.util.List;

import jcoolj.com.base.PageFragment;

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private Context context;
    private List<PageFragment> fragmentList;

    public FragmentPagerAdapter(Context context, FragmentManager fm, List<PageFragment> fragmentList){
        super(fm);
        this.context = context;
        this.fragmentList = fragmentList;
    }

    @Override
    public PageFragment getItem(int position) {
        return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (fragmentList == null || fragmentList.size() == 0) ? "" : context.getString(fragmentList.get(position).getTitle());
    }

    public @DrawableRes int getPageIcon(int position) {
        return (fragmentList == null || fragmentList.size() == 0) ? 0 : fragmentList.get(position).getIcon();
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

}
