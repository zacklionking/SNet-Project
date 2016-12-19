package jcoolj.com.base.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class AnimationHelper {

    private static final long DEFAULT_ANIMATION_DURATION = 250;

    public static Animation createAppearAnimation(View targetView) {
        return createAppearAnimation(targetView, DEFAULT_ANIMATION_DURATION);
    }

    public static Animation createAppearAnimation(View targetView, long duration) {
        Animation anim = new AlphaAnimation(0.0f, Animation.RELATIVE_TO_SELF);
        anim.setDuration(duration);
        return anim;
    }

    public static Animator createDisappearAnimation(@NonNull final View target, long duration){
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "alpha", 1f, 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                target.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(duration);
        return animator;
    }

    public static Animator createDisappearAnimation(@NonNull View target){
        return createDisappearAnimation(target, DEFAULT_ANIMATION_DURATION);
    }

    public static Animation createSpinningAnimation(){
        Animation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(2000);
        anim.setRepeatCount(Animation.INFINITE);
        return anim;
    }

}
