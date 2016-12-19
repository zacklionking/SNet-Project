package jcoolj.com.base;

import android.os.Bundle;

import jcoolj.com.base.view.TitleBar;
import jcoolj.com.core.support.IFragment;
import jcoolj.com.core.utils.Logger;

public abstract class BaseFragment extends IFragment {

    protected boolean isVisible;

    public BaseFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = getUserVisibleHint();
        if(getView() == null)
            return;
        Logger.d(getClass().getName() + " visible:" + isVisible);
        if(isVisible)
            onShowFragment();
        else
            onHideFragment();
    }

    protected void onShowFragment(){}

    protected void onHideFragment(){}

    public TitleBar getTitleBar(){
        return ((BaseActivity) getActivity()).getTitleBar();
    }

}
