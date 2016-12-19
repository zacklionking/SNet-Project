package jcoolj.com.core.network;

import android.support.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Basic network manager
 */
public class SNetManager {

    public static final long DEFAULT_TIMEOUT = 10;

    static final int MSG_TIMEOUT = 0x001;
    static final int MSG_COMPLETE = 0x002;
    static final int MSG_FAILED = 0x003;
    static final int MSG_SERVICE_OUT = 0x004;

    private OkHttpClient mHttpClient;

    private ExecutorService mThreadService;

    private static SNetManager mInstance;
    public static SNetManager getInstance(){
        if(mInstance == null)
            mInstance = new SNetManager();
        return mInstance;
    }

    private SNetManager(){
        mHttpClient = new OkHttpClient();
        mHttpClient.setConnectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mHttpClient.setReadTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mHttpClient.setWriteTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    private void newThreadPool(){
        mThreadService = Executors.newCachedThreadPool();
    }

    public OkHttpClient getHttpClient(){
        return mHttpClient;
    }

    public void startTask(@NonNull final Task task) {
        if(mThreadService == null)
            newThreadPool();
        mThreadService.submit(task);
    }

}
