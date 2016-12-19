package jcoolj.com.core.utils;

import android.util.Log;

public class Logger {

    public static boolean DEBUG = true;
    private static final String TAG = "d_log";

    public static void d(String text){
        if(DEBUG)
            Log.d(TAG, text);
    }

}
