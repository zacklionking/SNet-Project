package jcoolj.com.dribbble;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import jcoolj.com.base.FullScreenActivity;
import jcoolj.com.base.view.TouchImageView;
import jcoolj.com.base.utils.AnimationHelper;
import jcoolj.com.dribbble.utils.Navigator;

/**
 *  图片展示页
 */
public class ImageViewerActivity extends FullScreenActivity {

    private AnimationSet anim_loading;
    private View view_loading;
    private TextView view_loadingTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(jcoolj.com.base.R.anim.anim_scale_enter, jcoolj.com.base.R.anim.anim_scale_enter);
        setContentView(R.layout.activity_image_viewer);
        anim_loading = new AnimationSet(false);
        anim_loading.setRepeatMode(Animation.RESTART);
        anim_loading.addAnimation(AnimationHelper.createSpinningAnimation());
        view_loading = findViewById(R.id.loading);
        view_loadingTip = (TextView) findViewById(R.id.loading_tip);
        TouchImageView imageView = (TouchImageView) findViewById(R.id.image);
        // 按原图尺寸加载图片
        Glide.with(this).load(Navigator.getImageUrl(getIntent())).transform(new BitmapTransformation(this) {
            @Override
            protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
                if (toTransform == null) return null;
                int width = toTransform.getWidth();
                int height = toTransform.getHeight();
                // 图片宽度大于高度时则将图片旋转90°横屏展示
                if(width > height) {
                    Bitmap result = pool.get(toTransform.getHeight(), toTransform.getWidth(), Bitmap.Config.ARGB_8888);
                    if (result == null) {
                        result = Bitmap.createBitmap(toTransform.getHeight(), toTransform.getWidth(), Bitmap.Config.ARGB_8888);
                    }
                    Canvas canvas = new Canvas(result);
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90, (float) toTransform.getWidth() / 2, (float) toTransform.getHeight() / 2);
                    float targetX = toTransform.getHeight(), targetY = 0;
                    final float[] values = new float[9];
                    matrix.getValues(values);
                    float x1 = values[Matrix.MTRANS_X];
                    float y1 = values[Matrix.MTRANS_Y];
                    matrix.postTranslate(targetX - x1, targetY - y1);
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    canvas.drawBitmap(toTransform, matrix, paint);
                    return result;
                } else
                    return toTransform;
            }

            @Override
            public String getId() {
                return getClass().getName();
            }
        }).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.bg_shot_placeholder).into(new GlideDrawableImageViewTarget(imageView){
            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
                if(view_loading != null)
                    view_loading.startAnimation(anim_loading);
                if(view_loadingTip != null)
                    view_loadingTip.setVisibility(View.GONE);
            }

            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                if(view_loading != null)
                    view_loading.clearAnimation();
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                if(view_loading != null)
                    view_loading.clearAnimation();
                if(view_loadingTip != null) {
                    view_loadingTip.setText(e.toString());
                    view_loadingTip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(jcoolj.com.base.R.anim.anim_scale_exit, jcoolj.com.base.R.anim.anim_scale_exit);
    }

}
