package jcoolj.com.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import jcoolj.com.base.BaseActivity;
import jcoolj.com.base.R;

public class PixelUtils {

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getTitleBarHeight(Context context) {
        if(context instanceof BaseActivity)
            return ((BaseActivity) context).getTitleBar().getTitleBarHeight();
        else
            return context.getResources().getDimensionPixelSize(R.dimen.title_bar_height);
    }

    public static int dp2px(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

}
