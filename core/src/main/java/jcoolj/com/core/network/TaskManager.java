package jcoolj.com.core.network;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.okhttp.Request;

import org.json.JSONObject;

import jcoolj.com.core.support.IActivity;
import jcoolj.com.core.support.IFragment;
import jcoolj.com.core.support.IFragmentActivity;
import jcoolj.com.core.utils.Logger;

import static jcoolj.com.core.network.SNetManager.MSG_COMPLETE;
import static jcoolj.com.core.network.SNetManager.MSG_FAILED;
import static jcoolj.com.core.network.SNetManager.MSG_SERVICE_OUT;
import static jcoolj.com.core.network.SNetManager.MSG_TIMEOUT;

/**
 *  负责{@link STask}的新建，派发和完成后回调处理的抽象基类
 */
public abstract class TaskManager {

    private Handler handler;

    private SparseArray<STask> tasks;

    public TaskManager(){
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                STask task = (STask) msg.obj;
                int id = task.getTaskId();
                tasks.remove(id);
                switch (msg.arg1){
                    case MSG_COMPLETE:
                        onTaskComplete(task);
                        break;
                    case MSG_SERVICE_OUT:
                    case MSG_FAILED:
                    case MSG_TIMEOUT:
                        onError(task);
                        break;
                }
            }
        };
        tasks = new SparseArray<>();
    }

    public void bindLifeCycleCallbacks(IActivity activity){
        activity.bindLifeCycleCallbacks(new ActivityLifeCycleCallbacks());
    }

    public void bindLifeCycleCallbacks(IFragmentActivity activity){
        activity.bindLifeCycleCallbacks(new ActivityLifeCycleCallbacks());
    }

    public void bindLifeCycleCallbacks(IFragment fragment){
        fragment.bindLifeCycleCallbacks(new FragmentLifeCycleCallbacks());
    }

    protected STask sendTask(Subscriber subscriber, Request request, int requestId) {
        STask task = tasks.get(requestId);
        if(task != null){
            // 已有相同的任务，取消当前任务并发布新任务
            Logger.d(toString() + " canceled at " + System.currentTimeMillis() + ", start new one");
            task.cancel();
        }
        task = new STask(requestId, request, subscriber, handler);
        tasks.put(requestId, task);
        Logger.d(toString() + " started at " + System.currentTimeMillis() + ", post by " + this + ", subscribed on " + subscriber);
        SNetManager.getInstance().startTask(task);
        subscriber.onSubscribe();
        return task;
    }

    private void clearTasks(){
        int count = tasks.size();
        for(int i=0; i<count; i++){
            STask task = tasks.valueAt(i);
            task.cancel();
        }
        tasks.clear();
        Logger.d("Tasks cleared");
    }

    private void onTaskComplete(STask task){
        Logger.d(toString() + " completed at " + System.currentTimeMillis());
        ResponseWrapped response = task.getResponse();
        try {
            if(response.getCode() == 200){
                onResponse(task);
            } else {
                JSONObject errorMsg = new JSONObject((String) response.getBody());
                if(task.getSubscriber() != null)
                    task.getSubscriber().onRefuse(new Exception(errorMsg.getString("message")));
            }
        } catch (Exception e) {
            if(task.getSubscriber() != null)
                task.getSubscriber().onRefuse(new Exception("Unknown error:" + response.getBody()));
        }
    }

    protected abstract void onResponse(STask task) throws Exception;

    protected void onError(STask task) {
        if(task.getSubscriber() != null)
            task.getSubscriber().onRefuse(task.getException());
    }

    private class ActivityLifeCycleCallbacks implements jcoolj.com.core.support.ActivityLifeCycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            clearTasks();
        }

    }

    private class FragmentLifeCycleCallbacks implements jcoolj.com.core.support.FragmentLifeCycleCallbacks{

        @Override
        public void onFragmentCreated(Fragment fragment, Bundle savedInstanceState) {

        }

        @Override
        public void onFragmentStarted(Fragment fragment) {

        }

        @Override
        public void onFragmentResumed(Fragment fragment) {

        }

        @Override
        public void onFragmentPaused(Fragment fragment) {

        }

        @Override
        public void onFragmentStopped(Fragment fragment) {

        }

        @Override
        public void onFragmentSaveInstanceState(Fragment fragment, Bundle outState) {

        }

        @Override
        public void onFragmentDestroyed(Fragment fragment) {
            clearTasks();
        }

        @Override
        public void onFragmentAttach(Fragment fragment, Activity activity) {

        }

        @Override
        public void onFragmentDetach(Fragment fragment) {

        }

        @Override
        public void onFragmentActivityCreated(Fragment fragment, Bundle savedInstanceState) {

        }

        @Override
        public void onFragmentCreateView(Fragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        }

        @Override
        public void onFragmentViewCreated(Fragment fragment, View view, Bundle savedInstanceState) {

        }

    }

}
