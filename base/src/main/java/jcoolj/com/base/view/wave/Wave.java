package jcoolj.com.base.view.wave;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import jcoolj.com.base.R;
import jcoolj.com.core.utils.Logger;

// y=Asin(ωx+φ)+k
public class Wave extends View {
    private final int WAVE_HEIGHT_LARGE = 16;
    private final int WAVE_HEIGHT_MIDDLE = 8;
    private final int WAVE_HEIGHT_LITTLE = 5;

    private final float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
    private final float WAVE_LENGTH_MULTIPLE_MIDDLE = 1f;
    private final float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;

    private final float WAVE_HZ_FAST = 0.13f;
    private final float WAVE_HZ_NORMAL = 0.09f;
    private final float WAVE_HZ_SLOW = 0.05f;

    public final int DEFAULT_ABOVE_WAVE_ALPHA = 200;
    public final int DEFAULT_BLOW_WAVE_ALPHA = 100;

    protected static final int LARGE = 1;
    protected static final int MIDDLE = 2;
    protected static final int LITTLE = 3;

    private final int DEFAULT_ABOVE_WAVE_COLOR = Color.WHITE;
    private final int DEFAULT_BLOW_WAVE_COLOR = Color.WHITE;

    private final float X_SPACE = 20;
    private final double PI2 = 2 * Math.PI;

    private Path mAboveWavePath = new Path();
    private Path mBlowWavePath = new Path();

    private Paint mAboveWavePaint = new Paint();
    private Paint mBlowWavePaint = new Paint();

    private int mAboveWaveColor;
    private int mBlowWaveColor;

    private float mWaveMultiple;
    private float mWaveLength;
    private float mWaveHeight;
    private float mMaxRight;
    private float mWaveHz;

    private float mCurrentWaveHeight;

    // wave waveAnimation
    private float mAboveOffset = 0.0f;
    private float mBlowOffset;

    private int left, right, bottom;
    // ω
    private double omega;

    private Animation waveAnimation;
    private boolean waving;
    private boolean animationCleared = false;

    public Wave(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Wave(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveView, 0, 0);
        mAboveWaveColor = attributes.getColor(R.styleable.WaveView_above_wave_color, DEFAULT_ABOVE_WAVE_COLOR);
        mBlowWaveColor = attributes.getColor(R.styleable.WaveView_blow_wave_color, DEFAULT_BLOW_WAVE_COLOR);
        mWaveHeight = attributes.getInt(R.styleable.WaveView_wave_height, MIDDLE);
        mWaveMultiple = attributes.getInt(R.styleable.WaveView_wave_length, LARGE);
        mWaveHz = attributes.getInt(R.styleable.WaveView_wave_hz, MIDDLE);
        attributes.recycle();

        initializeWaveSize(mWaveMultiple, mWaveHeight, mWaveHz);
        initializePainters();

        waveAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(!waving){
                    mCurrentWaveHeight -= 0.1f;
                    if(mCurrentWaveHeight < 0){
                        mCurrentWaveHeight = 0;
                        clearAnimation();
                    }
                } else if(waving && mCurrentWaveHeight < mWaveHeight) {
                    mCurrentWaveHeight += 0.2f;
                    if(mCurrentWaveHeight > mWaveHeight)
                        mCurrentWaveHeight = mWaveHeight;
                }
                calculatePath();
                invalidate();
            }
        };
        waveAnimation.setRepeatCount(Animation.INFINITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mBlowWavePath, mBlowWavePaint);
        canvas.drawPath(mAboveWavePath, mAboveWavePaint);
    }

    public void setAboveWaveColor(int aboveWaveColor) {
        this.mAboveWaveColor = aboveWaveColor;
    }

    public void setBlowWaveColor(int blowWaveColor) {
        this.mBlowWaveColor = blowWaveColor;
    }

    public Paint getAboveWavePaint() {
        return mAboveWavePaint;
    }

    public Paint getBlowWavePaint() {
        return mBlowWavePaint;
    }

    public void initializeWaveSize(float waveMultiple, float waveHeight, float waveHz) {
        mWaveMultiple = getWaveMultiple((int) waveMultiple);
        mCurrentWaveHeight = mWaveHeight = getWaveHeight((int) waveHeight);
        mWaveHz = getWaveHz((int) waveHz);
        mBlowOffset = mWaveHeight * 0.4f;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (mWaveHeight * 2));
        setLayoutParams(params);
    }

    public void initializePainters() {
        mAboveWavePaint.setColor(mAboveWaveColor);
        mAboveWavePaint.setAlpha(DEFAULT_ABOVE_WAVE_ALPHA);
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setAntiAlias(true);

        mBlowWavePaint.setColor(mBlowWaveColor);
        mBlowWavePaint.setAlpha(DEFAULT_BLOW_WAVE_ALPHA);
        mBlowWavePaint.setStyle(Paint.Style.FILL);
        mBlowWavePaint.setAntiAlias(true);
    }

    private float getWaveMultiple(int size) {
        switch (size) {
            case LARGE:
                return WAVE_LENGTH_MULTIPLE_LARGE;
            case MIDDLE:
                return WAVE_LENGTH_MULTIPLE_MIDDLE;
            case LITTLE:
                return WAVE_LENGTH_MULTIPLE_LITTLE;
        }
        return 0;
    }

    private int getWaveHeight(int size) {
        switch (size) {
            case LARGE:
                return WAVE_HEIGHT_LARGE;
            case MIDDLE:
                return WAVE_HEIGHT_MIDDLE;
            case LITTLE:
                return WAVE_HEIGHT_LITTLE;
        }
        return 0;
    }

    private float getWaveHz(int size) {
        switch (size) {
            case LARGE:
                return WAVE_HZ_FAST;
            case MIDDLE:
                return WAVE_HZ_NORMAL;
            case LITTLE:
                return WAVE_HZ_SLOW;
        }
        return 0;
    }

    /**
     * calculate wave track
     */
    private void calculatePath() {
        mAboveWavePath.reset();
        mBlowWavePath.reset();

        getWaveOffset();

        float y;
        mAboveWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mCurrentWaveHeight * Math.sin(omega * x + mAboveOffset) + mCurrentWaveHeight);
            mAboveWavePath.lineTo(x, y);
        }
        mAboveWavePath.lineTo(right, bottom);

        mBlowWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mCurrentWaveHeight * Math.sin(omega * x + mBlowOffset) + mCurrentWaveHeight);
            mBlowWavePath.lineTo(x, y);
        }
        mBlowWavePath.lineTo(right, bottom);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(waving && animationCleared)
            startWave();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(waving) {
            clearAnimation();
            animationCleared = true;
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            waving = false;
            clearAnimation();
        }
    }

    public boolean isWaving(){
        return waving;
    }

    public void startWave() {
        if(getWidth() == 0 || getHeight() == 0)
            // 尝试在layout完成后再执行动画
            post(new Runnable() {
                @Override
                public void run() {
                    wavingImp();
                }
            });
        else
            wavingImp();
    }

    private void wavingImp(){
        int width = getWidth();
        Logger.d("wave start");
        if(getWidth() == 0)
            return;
        setVisibility(VISIBLE);
        waving = true;
        mWaveLength = width * mWaveMultiple;
        left = getLeft();
        right = getRight();
        bottom = getBottom() + 2;
        mMaxRight = right + X_SPACE;
        omega = PI2 / mWaveLength;
        clearAnimation();
        startAnimation(waveAnimation);
        animationCleared = false;
    }

    public void stopWave() {
        waving = false;
    }

    private void getWaveOffset() {
        if (mBlowOffset > Float.MAX_VALUE - 100) {
            mBlowOffset = 0;
        } else {
            mBlowOffset += mWaveHz / 1.5;
        }

        if (mAboveOffset > Float.MAX_VALUE - 100) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mWaveHz;
        }
    }

}
