package jcoolj.com.core.network;

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import jcoolj.com.core.utils.Logger;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Zack on 2016/11/2.
 */
public class STask<T> extends Task {

    private Request request;
    private String response;

    private Subscriber<T> subscriber;
    private T data;

    private Interceptor<T> interceptor;
    public interface Interceptor<T>  {
        T onResponse(int id, String response) throws Exception;
    }

    STask(int taskId, Request request, Subscriber<T> subscriber, Handler handler){
        super(taskId, handler);
        this.subscriber = subscriber;
        this.request = request;
    }

    @Override
    public void start(){
        super.start();
        if(subscriber != null) {
            Logger.d(toString() + " started at " + System.currentTimeMillis() + ", " + this);
            subscriber.onSubscribe();
        } else
            Logger.d(toString() + " started at " + System.currentTimeMillis() + ", " + this);
    }

    /**
     * Task在工作线程中运行时的回调
     * @param interceptor
     * @return
     */
    public STask<T> setInterceptor(Interceptor<T> interceptor){
        this.interceptor = interceptor;
        return this;
    }

    void complete(){
        Logger.d(this + " completed.");
        if(subscriber != null)
            subscriber.onComplete(data);
    }

    void reportError(){
        if(subscriber != null)
            subscriber.onRefuse(getException());
    }

    void reportError(Throwable e){
        if(subscriber != null)
            subscriber.onRefuse(e);
    }

    @Override
    public void doWork() throws Exception {
        Response response = SNetManager.getInstance().getHttpClient().newCall(request).execute();
        if(canceled) {
            Logger.d(toString() + "cancelled");
            return;
        }
        if(response == null || response.body() == null)
            return;
        int code = response.code();
        this.response = response.body().string();
        switch (code) {
            case 503:
                handleException(new ServiceOutException());
                break;
            case 200:
                try {
                    if(interceptor != null)
                        data = interceptor.onResponse(getTaskId(), this.response);
                    Message msg = handler.obtainMessage(SNetManager.MSG_COMPLETE);
                    msg.obj = this;
                    handler.sendMessage(msg);
                } catch (Exception e){
                    handleException(e);
                }
                break;
            default:
                try {
                    String responseBody = response.body().string();
                    handleException(new Exception(new JSONObject(responseBody).getString("message")));
                } catch (Exception e){
                    handleException(new Exception("Unknown error."));
                }
                break;
        }
    }

    @Override
    public String toString() {
        return "STask ---------- " + getTaskId() + (subscriber != null ? " post by " + subscriber : "") + " ";
    }

}
