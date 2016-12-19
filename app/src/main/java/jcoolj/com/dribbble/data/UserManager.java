package jcoolj.com.dribbble.data;

import com.squareup.okhttp.Request;

import org.json.JSONObject;

import jcoolj.com.core.network.STask;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.core.network.TaskManager;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.utils.URLParser;

public class UserManager extends TaskManager {

    public void getUserInfo(Subscriber<User> subscriber, long id){
        String url = URLParser.getUrlAPI("users/" + id);
        Logger.d("getUser params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        sendTask(subscriber, request, (int) id);
    }

    @Override
    protected void onResponse(STask task) throws Exception {
        task.getSubscriber().onComplete(new User(new JSONObject((String) task.getResponse().getBody())));
    }

}
