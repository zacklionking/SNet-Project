package jcoolj.com.core.network;

import android.content.Context;

import jcoolj.com.core.R;

public class ErrorCode {

    public static String getErrorString(Context context, int code){
        switch (code){
            case 503:
                return context.getString(R.string.error_service_out);
            default:
                return context.getString(R.string.error_unknown);
        }
    }

}
