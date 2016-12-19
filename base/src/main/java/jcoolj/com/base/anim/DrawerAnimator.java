package jcoolj.com.base.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

public class DrawerAnimator {

    private static final int STATE_IDLE = -1;
    private static final int STATE_COLLAPSE = 0;
    private static final int STATE_EXPAND = 1;

    public static final int TOP = 1;
    public static final int BOTTOM = 2;

    private static final long DEFAULT_DURATION = 250;

    private long duration = DEFAULT_DURATION;
    private int position = TOP;

    private ObjectAnimator animator;

    private float initY;
    private int animationState = STATE_IDLE;
    private boolean isCancel;

    public void setDuration(long duration){
        this.duration = duration;
        if(animator != null)
            animator.setDuration(duration);
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void toggle(final View target, final boolean open){
        int height = target.getHeight();
        float translationY = target.getTranslationY();
        switch (animationState){
            case STATE_IDLE:
                if(open) {
                    switch (position){
                        case TOP:
                            target.setTranslationY(-height);
                            break;
                        case BOTTOM:
                            break;
                    }
                    target.setVisibility(View.VISIBLE);
                } else
                    switch (position){
                        case TOP:
                            initY = translationY;
                            break;
                        case BOTTOM:
                            break;
                    }
                animationState = open? STATE_EXPAND : STATE_COLLAPSE;
                break;
            case STATE_COLLAPSE:
                if(open)
                    animationState = STATE_EXPAND;
                else
                    return;
                break;
            case STATE_EXPAND:
                if(open)
                    return;
                else
                    animationState = STATE_COLLAPSE;
                break;
        }
        float startPosition = 0;
        float targetPosition = 0;
        switch (position){
            case TOP:
                startPosition = translationY;
                targetPosition = animationState == STATE_COLLAPSE ? - initY - height : initY;
                break;
            case BOTTOM:
                startPosition = animationState == STATE_COLLAPSE ? translationY : (float)height - translationY;
                targetPosition = animationState == STATE_COLLAPSE ? height : 0;
                break;
        }
        animator = ObjectAnimator.ofFloat(target, position == TOP ? "y" : "translationY", startPosition, targetPosition);
        animator.setDuration(duration);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isCancel) {
                    isCancel = false;
                    return;
                }
                animationState = STATE_IDLE;
                if(position == BOTTOM && !open)
                    target.setTranslationY(0);
                if (!open)
                    target.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isCancel = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public void cancel(){
        if(animator != null)
            animator.cancel();
    }

}
