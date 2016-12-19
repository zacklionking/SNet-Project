package jcoolj.com.base;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import jcoolj.com.core.utils.Logger;

/**
 *  适用Fragment + ViewPager + Tab框架的抽象类，Fragment需实现此类
 *  @see jcoolj.com.base.view.FragmentPagerAdapter
 */
public abstract class PageFragment extends BaseFragment {

    public PageFragment(){ }

    @StringRes
    public abstract int getTitle();

    @DrawableRes
    public abstract int getIcon();

    public abstract void refresh();

    public abstract void load();

}
