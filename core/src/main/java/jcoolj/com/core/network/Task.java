package jcoolj.com.core.network;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jcoolj.com.core.utils.Logger;

import static jcoolj.com.core.network.SNetManager.MSG_FAILED;

/**
 * Created by Zack on 2016/12/1.
 */
public abstract class Task implements Runnable {

    private int taskId;

    private Throwable e;

    protected Handler handler;

    protected boolean canceled;

    public Task(int taskId, Handler handler){
        this.taskId = taskId;
        this.handler = handler;
    }

    public void start(){
        SNetManager.getInstance().startTask(this);
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

    public boolean isCanceled(){
        return canceled;
    }

    public abstract void doWork() throws Exception;

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

    void handleException(Throwable e){
        Logger.d(toString() + "failed. Cause of " + e.getMessage());
        Message msg = handler.obtainMessage(MSG_FAILED);
        this.e = e;
        msg.obj = this;
        handler.sendMessage(msg);
    }

    @Override
    public String toString() {
        return "Task ---------- " + taskId + " ";
    }

}
