package jcoolj.com.dribbble.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.R;

/**
 *  弹跳动画效果的Dribbble Loading控件
 */
public class DribbbleView extends View {

    // 默认初始速度
    private static final float  DEFAULT_SPEED           = 3.5f;

    //  默认加速度
    private static final float  DEFAULT_ACC_BOUNCE      = 0.3f;

    // 默认停止弹跳后的衰减次数
    private static final int    DEFAULT_BOUNCE_DECAY_COUNT = 3;

    // 默认旋转速度
    private static final float  DEFAULT_ROTATE_SPEED    = 5;

    // 阴影可见的最大高度
    private static final float  DEFAULT_SHADOW_VHEIGHT  = 100f;

    // 阴影大小与实物大小的比例
    private static final float  SHADOW_SCALE        = 0.6f;

    // 摇摆衰减
    private static final float  SWING_DECAY_FACTOR = 0.6f;

    private Drawable dribbble;
    private int dribbbleWidth;

    private int color;

    /**
     *  初始速度（弹起速度），与弹跳高度成正比
     */
    private float initialSpeed = DEFAULT_SPEED;

    /**
     *  加速度，与弹跳频率成正比
     */
    private float accelerateSpeed = DEFAULT_ACC_BOUNCE;

    /**
     *  阴影可见的最大高度
     */
    private float shadowVisibleHeight = DEFAULT_SHADOW_VHEIGHT;

    /**
     *  弹跳终止的衰减次数
     */
    private int decayCount = DEFAULT_BOUNCE_DECAY_COUNT;

    /**
     *  旋转速度
     */
    private float rotationalSpeed = DEFAULT_ROTATE_SPEED;

    // 弹跳高度
    private double height;
    // 弹跳速度
    private double speed = initialSpeed;
    // 衰减次数
    private int bounceDecay;
    // 滚动角度
    private double angle;
    // 摇摆角度
    private double swingSpeed;
    private int swingVFactor;
    // 摇摆位移
    private float swingDistance;

    private int direction = 1;

    private Animation animation;

    private Rect dribbbleBounds;
    private RectF shadowBounds;
    private Paint shadowPaint;

    private boolean stop = true;
    private boolean animationCleared = false;

    public DribbbleView(Context context) {
        this(context, null);
    }

    public DribbbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DribbbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DribbbleView);
        color = a.getColor(R.styleable.DribbbleView_color, getResources().getColor(R.color.colorPrimary));
        initialSpeed = a.getFloat(R.styleable.DribbbleView_initialSpeed, initialSpeed);
        accelerateSpeed = a.getFloat(R.styleable.DribbbleView_acceleratedSpeed, accelerateSpeed);
        shadowVisibleHeight = a.getFloat(R.styleable.DribbbleView_shadowVisibleHeight, shadowVisibleHeight);
        decayCount = a.getInt(R.styleable.DribbbleView_decayCount, decayCount);
        rotationalSpeed = a.getFloat(R.styleable.DribbbleView_rotationalSpeed, rotationalSpeed);
        // 摇摆速度略低于旋转速度
        swingSpeed = rotationalSpeed * SWING_DECAY_FACTOR;
        a.recycle();

        dribbble = getResources().getDrawable(R.drawable.ic_dribbble);
        dribbbleWidth = getResources().getDimensionPixelSize(R.dimen.dribbble_radius);
        dribbbleBounds = new Rect();
        dribbble = DrawableCompat.wrap(dribbble.mutate());
        DrawableCompat.setTint(dribbble, color);

        shadowBounds = new RectF();
        shadowPaint = new Paint();
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(color);
        shadowPaint.setAlpha(125);

        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                transformer();
            }
        };
        animation.setRepeatCount(Animation.INFINITE);
    }

    public float getInitialSpeed() {
        return initialSpeed;
    }

    public void setInitialSpeed(float initialSpeed) {
        this.initialSpeed = initialSpeed;
    }

    public float getAccelerateSpeed() {
        return accelerateSpeed;
    }

    public void setAccelerateSpeed(float accelerateSpeed) {
        this.accelerateSpeed = accelerateSpeed;
    }

    public float getShadowVisibleHeight() {
        return shadowVisibleHeight;
    }

    public void setShadowVisibleHeight(float shadowVisibleHeight) {
        this.shadowVisibleHeight = shadowVisibleHeight;
    }

    public int getDecayCount() {
        return decayCount;
    }

    public void setDecayCount(int decayCount) {
        this.decayCount = decayCount;
    }

    public float getRotationalSpeed() {
        return rotationalSpeed;
    }

    public void setRotationalSpeed(float rotationalSpeed) {
        this.rotationalSpeed = rotationalSpeed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void bounce(){
        if(getVisibility() != VISIBLE)
            setVisibility(VISIBLE);
        stop = false;
        bounceDecay = 0;
        swingDistance = 0;
        speed = initialSpeed;
        swingSpeed = rotationalSpeed * SWING_DECAY_FACTOR;
        swingVFactor = 0;
        if(hasWindowFocus())
            startAnimation();
        else
            // 未获取焦点时动画尚不可播放，需要在UI线程消息队列中等待处理
            post(new Runnable() {
                @Override
                public void run() {
                    startAnimation();
                }
            });
    }

    public void stop(){
        stop = true;
    }

    public boolean isStop(){
        return stop;
    }

    private void startAnimation(){
        clearAnimation();
        startAnimation(animation);
        animationCleared = false;
    }

    protected void transformer(){
        if(stop && bounceDecay == decayCount) {
            // 弹跳终止时球左右晃动
            height = 0;
            if(rotationalSpeed > 0) {
                swingVFactor += 20;
                if(swingVFactor > 360) {
                    swingVFactor = 20;
                    swingSpeed--;
                }
                double factor = swingSpeed * Math.sin(swingVFactor * Math.PI/180);
                angle += factor;
                swingDistance += factor / 180 * Math.PI * dribbbleWidth / 2;
            }
            if(rotationalSpeed == 0 || swingSpeed < 0) {
                invalidate();
                clearAnimation();
                return;
            }
        } else {
            if(rotationalSpeed > 0) {
                angle += rotationalSpeed;
                if(angle > 360)
                    angle = 1;
            }

            height += direction * speed;
            speed -= direction * accelerateSpeed;
            if(speed < 0) {
                speed = 0;
                direction = -1;
            }
            if(speed > initialSpeed - (float) bounceDecay / decayCount * initialSpeed) {
                if(stop) {
                    // 弹跳停止
                    bounceDecay++;
                    speed = initialSpeed - (float) bounceDecay / decayCount * initialSpeed;
                } else
                    speed = initialSpeed;
                height = 0;
                direction = 1;
            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec){
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY)
            result = specSize;
        else
            result = (int) (dribbbleWidth + 2 * swingSpeed / 180 * Math.PI * dribbbleWidth);
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY)
            result = specSize;
        else {
            result = (int) (dribbbleWidth + initialSpeed * initialSpeed / accelerateSpeed / 2f + (float) dribbbleWidth * SHADOW_SCALE / 2f);
            if(specMode == MeasureSpec.AT_MOST)
                result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float shadowMaxHeight = (float) dribbbleWidth * SHADOW_SCALE / 2f;
        float dribbbleMaxHeight = dribbbleWidth + initialSpeed * initialSpeed / accelerateSpeed / 2f;
        int shadowWidth = (int) (shadowMaxHeight * 2 * (1- height / shadowVisibleHeight));
        int shadowHeight = (int) (shadowMaxHeight * (1- height / shadowVisibleHeight));
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        shadowBounds.set(centerX - shadowWidth / 2 + swingDistance, centerY + dribbbleMaxHeight / 2 - shadowHeight / 2 ,
                centerX + shadowWidth / 2 + swingDistance, centerY + dribbbleMaxHeight / 2 + shadowHeight / 2 );
        canvas.drawOval(shadowBounds, shadowPaint);

        dribbbleBounds.set(centerX - dribbbleWidth / 2 + (int) swingDistance, (int) (centerY + dribbbleMaxHeight / 2 - height - dribbbleWidth),
                centerX + dribbbleWidth / 2 + (int) swingDistance, (int) (centerY + dribbbleMaxHeight / 2 - height));
        dribbble.setBounds(dribbbleBounds);
        canvas.save();
        canvas.rotate((int)angle, dribbbleBounds.centerX(), dribbbleBounds.centerY());
        dribbble.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(!animationCleared && animation.hasStarted() && visibility != VISIBLE) {
            stop = true;
            animationCleared = true;
            clearAnimation();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!stop && animationCleared)
            bounce();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
        animationCleared = true;
    }

}
