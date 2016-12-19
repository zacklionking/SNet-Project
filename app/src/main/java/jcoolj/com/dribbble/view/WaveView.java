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
public class WaveView extends RelativeLayout {

    private Wave view_wave;
    private TextView view_text;

    private DrawerAnimator drawerAnimator;

    private boolean isError;

    public WaveView(Context context, AttributeSet attrs) {
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
        view_wave.startWave();
        view_text.setAlpha(1);
        view_text.setText(getResources().getString(R.string.loading));
    }

    public void stop(){
        isError = false;
        fall();
    }

    public void error(){
        isError = true;
        view_wave.stopWave();
        view_text.setText(getResources().getString(R.string.loading_fail));
    }

    public boolean isError(){
        return isError;
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
//        float translationY = getTranslationY();
//        float currentHeight = (float)height - translationY;
//        setVisibility(VISIBLE);
//        levelAnimation = ValueAnimator.ofFloat(currentHeight, 0);
//        levelAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                setTranslationY((Float) animation.getAnimatedValue());
//            }
//        });
//        levelAnimation.setInterpolator(new DecelerateInterpolator());
//        int duration = (int) (800 * currentHeight / (float)height);
//        levelAnimation.setDuration(duration);
//        levelAnimation.start();
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
//        float translationY = getTranslationY();
//        float currentHeight = (float)height - translationY;
//        levelAnimation = ValueAnimator.ofFloat(translationY, height);
//        levelAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                setTranslationY((Float) animation.getAnimatedValue());
//            }
//        });
//        levelAnimation.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                setVisibility(GONE);
//                setTranslationY(0);
//                view_wave.clearAnimation();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        levelAnimation.setInterpolator(new DecelerateInterpolator());
//        int duration = (int) (800 * currentHeight / (float)height);
//        levelAnimation.setDuration(duration);
//        setTranslationY(0);
//        levelAnimation.start();
    }

}
