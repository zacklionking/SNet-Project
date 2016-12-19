package jcoolj.com.core.support;

import android.os.Bundle;

public class IFragmentActivity extends android.support.v4.app.FragmentActivity {

    private ActivityLifeCycleCallbacks callbacks;
    public void bindLifeCycleCallbacks(ActivityLifeCycleCallbacks callbacks){
        this.callbacks = callbacks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(callbacks != null)
            callbacks.onActivityCreated(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(callbacks != null)
            callbacks.onActivitySaveInstanceState(this, outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(callbacks != null)
            callbacks.onActivityStarted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(callbacks != null)
            callbacks.onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(callbacks != null)
            callbacks.onActivityPaused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(callbacks != null)
            callbacks.onActivityStopped(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(callbacks != null)
            callbacks.onActivityDestroyed(this);
    }

}
