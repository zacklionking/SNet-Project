package jcoolj.com.base;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import jcoolj.com.core.support.IFragmentActivity;

public abstract class FullScreenActivity extends IFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
    }

}
