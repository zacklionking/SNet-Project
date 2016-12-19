package jcoolj.com.base;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import jcoolj.com.base.view.TitleBar;

public abstract class BaseActivity extends FullScreenActivity {

    private TitleBar mTitleBar;

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
        ViewGroup windowView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);

        FrameLayout rootView = new FrameLayout(this);

        mTitleBar = new TitleBar(this);
        mTitleBar.setTitle(getTitle());
        mTitleBar.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView.addView(mTitleBar);
        windowView.addView(rootView);

        initView();
    }

    public TitleBar getTitleBar(){
        return mTitleBar;
    }

    protected abstract void initView();

}
