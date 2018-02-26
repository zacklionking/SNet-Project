package jcoolj.com.base.view.card;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import jcoolj.com.base.R;
import jcoolj.com.base.anim.Rotate3dAnimation;

public class FlipCard extends FrameLayout implements Animation.AnimationListener {

    private View frontView;
    private View backView;

    private boolean isBackside;
    private Rotate3dAnimation rotateAnim;

    private FlipListener listener;

    public interface FlipListener{
        void onFlipped();
        void onCardChanged();
    }

    public FlipCard(Context context) {
        this(context, null);
    }

    public FlipCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlipCard, 0, 0);
        LayoutInflater inflater = LayoutInflater.from(context);
        int front_layout = typedArray.getInt(R.styleable.FlipCard_front, 0);
        if(front_layout > 0) {
            frontView = inflater.inflate(front_layout, this, false);
            addView(frontView, 1);
        }
        int back_layout = typedArray.getInt(R.styleable.FlipCard_back, 0);
        if(back_layout > 0) {
            backView = inflater.inflate(back_layout, this, false);
            backView.setVisibility(GONE);
            addView(backView, 0);
        }
        typedArray.recycle();

        rotateAnim = new Rotate3dAnimation(context);
        rotateAnim.setAnimationListener(this);
        rotateAnim.setDuration(150);
        rotateAnim.setInterpolator(new AccelerateInterpolator());
    }

    public void setFlipListener(FlipListener listener){
        this.listener = listener;
    }

    public void setFrontView(View view){
        if(frontView != null)
            removeView(frontView);
        frontView = view;
        addView(frontView);
    }

    public void setBackView(View view){
        if(backView != null)
            removeView(backView);
        backView = view;
        backView.setVisibility(GONE);
        addView(backView);
    }

    public void flip(){
        rotateAnim.setCenter(getWidth()/2, 0).setDegree(0, isBackside? -90 : 90);
        rotateAnim.setAnimationListener(this);
        startAnimation(rotateAnim);
    }

    public boolean isBackside(){
        return isBackside;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        backView.setVisibility(isBackside ? GONE : VISIBLE);
        frontView.setVisibility(isBackside ? VISIBLE : GONE);
        rotateAnim.setCenter(getWidth()/2, 0).setDegree(isBackside ? 90 : -90, 0);
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isBackside = !isBackside;
                if(listener != null)
                    listener.onFlipped();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(rotateAnim);
        if (listener != null)
            listener.onCardChanged();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}
