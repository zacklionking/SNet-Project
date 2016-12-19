package jcoolj.com.core.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import jcoolj.com.core.utils.Logger;

/**
 * Created by Zack on 2016/11/2.
 */
public class STask extends Task {

    private Request request;

    private Subscriber subscriber;
    private ResponseWrapped responseWrapped;

    public STask(int taskId, Request request, Subscriber subscriber, Handler handler){
        super(taskId, handler);
        this.subscriber = subscriber;
        this.request = request;
//        setTimeout(0);  // 使用HttpClient的默认设置
    }

    @Override
    public void doWork() throws Exception {
        Response response = SNetManager.getInstance().getHttpClient().newCall(request).execute();
        if(canceled) {
            Logger.d(toString() + "cancelled");
            return;
        }
        int code = response.code();
        String responseBody = response.body().string();
        switch (code) {
            case 503:
                responseWrapped = new ResponseWrapped(response.code(), response.headers(), "");
                setException(new Exception("Service out"));
                handleMessage(SNetManager.MSG_SERVICE_OUT);
                break;
            default:
                Logger.d(toString() + "completed. Result:" + responseBody);
                responseWrapped = new ResponseWrapped(response.code(), response.headers(), responseBody);
                handleMessage(SNetManager.MSG_COMPLETE);
                break;
        }
    }

    public Request getRequest() {
        return request;
    }

    public ResponseWrapped getResponse() {
        return responseWrapped;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    @Override
    public String toString() {
        return "STask ---------- " + getTaskId() + (subscriber != null ? " post by " + subscriber : "") + " ";
    }

}
