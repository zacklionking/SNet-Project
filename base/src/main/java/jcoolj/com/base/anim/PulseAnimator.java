package jcoolj.com.base.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

public class PulseAnimator {

    public static void pulse(View view){
        AnimatorSet set = new AnimatorSet();
        Animator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f, 1f);
        Animator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f, 1f);
        set.playTogether(animatorX, animatorY);
        set.start();
    }

}
