package jcoolj.com.core.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IFragment extends android.support.v4.app.Fragment {

    private FragmentLifeCycleCallbacks callbacks;
    public void bindLifeCycleCallbacks(FragmentLifeCycleCallbacks callbacks){
        this.callbacks = callbacks;
    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        if(callbacks != null)
            callbacks.onFragmentAttach(this, activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(callbacks != null)
            callbacks.onFragmentDetach(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(callbacks != null)
            callbacks.onFragmentCreated(this, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(callbacks != null)
            callbacks.onFragmentStarted(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(callbacks != null)
            callbacks.onFragmentResumed(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(callbacks != null)
            callbacks.onFragmentPaused(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(callbacks != null)
            callbacks.onFragmentStopped(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(callbacks != null)
            callbacks.onFragmentDestroyed(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(callbacks != null)
            callbacks.onFragmentSaveInstanceState(this, outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(callbacks != null)
            callbacks.onFragmentActivityCreated(this, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(callbacks != null)
            callbacks.onFragmentCreateView(this, inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(callbacks != null)
            callbacks.onFragmentViewCreated(this, view, savedInstanceState);
    }

}
