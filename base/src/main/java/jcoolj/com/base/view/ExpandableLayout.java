package jcoolj.com.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import jcoolj.com.base.R;

public class ExpandableLayout extends RelativeLayout {

    private Boolean isOpened = false;
    private Integer duration = 350;
    private Animation animation;

    private boolean animationCleared = false;

    public ExpandableLayout(Context context)
    {
        this(context, null);
    }

    public ExpandableLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
        isOpened = typedArray.getBoolean(R.styleable.ExpandableLayout_el_expand, isOpened);
        duration = typedArray.getInt(R.styleable.ExpandableLayout_el_duration, duration);
        typedArray.recycle();
    }

    private void expand() {
        measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = getMeasuredHeight();
        getLayoutParams().height = 0;
        setVisibility(VISIBLE);

        animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                getLayoutParams().height = (interpolatedTime == 1) ? LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
                requestLayout();
            }


            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        clearAnimation();
        startAnimation(animation);
    }

    private void collapse()
    {
        final int initialHeight = getMeasuredHeight();
        animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    setVisibility(View.GONE);
                }else{
                    getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        clearAnimation();
        startAnimation(animation);
    }

    public Boolean isOpened()
    {
        return isOpened;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(animationCleared) {
            if (isOpened)
                show(false);
            else
                hide(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
        animationCleared = true;
    }

    public void show(){
        show(true);
    }

    public void show(boolean animated){
        isOpened = true;
        if (animated){
            expand();
        } else {
            measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            getLayoutParams().height = getMeasuredHeight();
            setVisibility(VISIBLE);
            requestLayout();
        }
    }

    public void hide(){
        hide(true);
    }

    public void hide(boolean animated){
        isOpened = false;
        if (animated){
            collapse();
        } else
            setVisibility(GONE);
    }

    @Override
    public void setLayoutAnimationListener(Animation.AnimationListener animationListener) {
        animation.setAnimationListener(animationListener);
    }

}
