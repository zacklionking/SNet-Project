package jcoolj.com.core.network;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jcoolj.com.core.utils.Logger;

/**
 * Created by Zack on 2016/12/1.
 */
public abstract class Task implements Runnable {

    private int taskId;

    private Throwable e;

//    private long timeout = SNetManager.DEFAULT_TIMEOUT;
//    private TimeUnit unit = TimeUnit.SECONDS;

    protected Handler handler;

    protected boolean canceled;

    public Task(int taskId, Handler handler){
        this.taskId = taskId;
        this.handler = handler;
    }

    public void cancel() {
        canceled = true;
        handler.removeCallbacksAndMessages(this);
    }

    public int getTaskId() {
        return taskId;
    }

    public void setException(Throwable e){
        this.e = e;
    }

    public Throwable getException() {
        return e;
    }

//    public long getTimeout() {
//        return timeout;
//    }
//
//    public void setTimeout(long timeout) {
//        this.timeout = timeout;
//    }
//
//    public void setTimeout(long timeout, TimeUnit unit) {
//        this.timeout = timeout;
//        this.unit = unit;
//    }

    public boolean isCanceled(){
        return canceled;
    }

    public abstract void doWork() throws Exception;

    protected void handleMessage(int msgId){
        Message msg = handler.obtainMessage(msgId);
        msg.arg1 = msgId;
        msg.obj = this;
        handler.sendMessage(msg);
    }

    protected void handleException(Exception e){
        Logger.d(toString() + "failed. Cause of " + e.getMessage());
        this.e = e;
        handleMessage(SNetManager.MSG_FAILED);
    }

    @Override
    public void run() {
        try {
            doWork();
        } catch (Exception e) {
            if(canceled) {
                Logger.d(toString() + "cancelled");
                return;
            }
            handleException(e);
        } finally {
            if(canceled) {
                handler.removeCallbacksAndMessages(this);
            }
        }
    }

    @Override
    public String toString() {
        return "Task ---------- " + taskId + " ";
    }

}
