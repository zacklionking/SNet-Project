package jcoolj.com.dribbble.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jcoolj.com.core.network.STask;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.core.network.TaskManager;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.utils.URLParser;
import okhttp3.Request;

public class TeamManager extends TaskManager {

    private static final int MSG_MEMBERS = 0;

    public void getMembers(Subscriber<List<User>> subscriber, long id){
        String url = URLParser.getUrlAPI("teams/" + id + "/members");
        Logger.d("getMembers params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        newTask(subscriber, request, MSG_MEMBERS).setInterceptor(new STask.Interceptor<List<User>>() {
            @Override
            public List<User> onResponse(int id, String response) throws Exception {
                JSONArray result = new JSONArray(response);
                List<User> members = new ArrayList<>();
                int count = result.length();
                for(int i=0; i<count; i++){
                    JSONObject memberObj = result.getJSONObject(i);
                    Logger.d("Member ------> "+memberObj.toString());
                    members.add(new User(memberObj));
                }
                return members;
            }
        });
    }

}
