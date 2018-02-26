package jcoolj.com.dribbble.data;

import org.json.JSONObject;

import jcoolj.com.core.network.STask;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.core.network.TaskManager;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.utils.URLParser;
import okhttp3.Request;

public class UserManager extends TaskManager {

    private final static int MSG_USER = -0x101;

    private STask.Interceptor<User> interceptor = new STask.Interceptor<User>() {
        @Override
        public User onResponse(int id, String response) throws Exception {
            return new User(new JSONObject(response));
        }
    };

    public void getUserInfo(Subscriber<User> subscriber) {
        String url = URLParser.getUrlAPI("user");
        Logger.d("getUser params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        newTask(subscriber, request, MSG_USER).setInterceptor(interceptor);
    }

    public void getUserInfo(Subscriber<User> subscriber, long id){
        String url = URLParser.getUrlAPI("users/" + id);
        Logger.d("getUser params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        newTask(subscriber, request, (int) id).setInterceptor(interceptor);
    }

}
