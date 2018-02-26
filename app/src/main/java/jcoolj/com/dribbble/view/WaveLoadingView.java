package jcoolj.com.dribbble.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jcoolj.com.base.R;
import jcoolj.com.base.anim.DrawerAnimator;
import jcoolj.com.base.view.wave.Wave;
import jcoolj.com.base.utils.AnimationHelper;

/**
 * Created by John on 2014/10/15.
 */
public class WaveLoadingView extends RelativeLayout {

    private Wave view_wave;
    private TextView view_text;

    private DrawerAnimator drawerAnimator;

    private boolean isError;
    protected boolean isLoading;

    public WaveLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_wave_loading, this);
        view_wave = (Wave) findViewById(R.id.loading_wave);
        view_text = (TextView) findViewById(R.id.loading_text);
        drawerAnimator = new DrawerAnimator();
        drawerAnimator.setDuration(800);
        drawerAnimator.setPosition(DrawerAnimator.BOTTOM);
    }

    public void start(){
        if(!isError)
            grow();
        isError = false;
        isLoading = true;
        view_wave.startWave();
        view_text.setAlpha(1);
        view_text.setText(getResources().getString(R.string.loading));
    }

    public void stop(){
        isError = false;
        isLoading = false;
        fall();
    }

    public void error(){
        isError = true;
        isLoading = false;
        view_wave.stopWave();
        view_text.setText(getResources().getString(R.string.loading_fail));
    }

    public boolean isError(){
        return isError;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private void grow(){
        if(drawerAnimator != null)
            drawerAnimator.cancel();
        if(getHeight() == 0)
            // 尝试在layout完成后再执行动画
            post(new Runnable() {
                @Override
                public void run() {
                    growImp();
                }
            });
        else
            growImp();
    }

    private void growImp(){
        int height = getHeight();
        if(height == 0)
            return;
        drawerAnimator.toggle(this, true);
    }

    private void fall(){
        if(drawerAnimator != null)
            drawerAnimator.cancel();
        if(getHeight() == 0)
            // 尝试在layout完成后再执行动画
            post(new Runnable() {
                @Override
                public void run() {
                    fallImp();
                }
            });
        else
            fallImp();
    }

    private void fallImp(){
        int height = getHeight();
        if(height == 0)
            return;
        drawerAnimator.toggle(this, false);
    }

}
