package jcoolj.com.base;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import jcoolj.com.base.utils.PixelUtils;
import jcoolj.com.base.view.actionbar.SmartActionBar;

public abstract class BaseActivity extends FullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
    }

    protected abstract void initView();

    public int getTitleBarHeight(){
        Resources res = getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return res.getDimensionPixelSize(R.dimen.title_bar_height) + res.getDimensionPixelSize(R.dimen.tab_bar_height) + PixelUtils.getStatusBarHeight(this);
        else
            return res.getDimensionPixelSize(R.dimen.title_bar_height) + res.getDimensionPixelSize(R.dimen.tab_bar_height) - PixelUtils.getStatusBarHeight(this);
    }

}
