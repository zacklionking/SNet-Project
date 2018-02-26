package jcoolj.com.dribbble;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;

import jcoolj.com.core.network.SNetManager;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        SNetManager sNetManager = SNetManager.getInstance();
//        Glide.get(this).getRegistry().register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(sNetManager.getHttpClient()));
    }

}
