package jcoolj.com.dribbble.data;

import com.squareup.okhttp.Request;

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

public class TeamManager extends TaskManager {

    private static final int MSG_MEMBERS = 0;

    public void getMembers(Subscriber<List<User>> subscriber, long id){
        String url = URLParser.getUrlAPI("teams/" + id + "/members");
        Logger.d("getMembers params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        sendTask(subscriber, request, MSG_MEMBERS);
    }

    @Override
    protected void onResponse(STask task) throws Exception {
        JSONArray result = new JSONArray((String) task.getResponse().getBody());
        List<User> members = new ArrayList<>();
        int count = result.length();
        for(int i=0; i<count; i++){
            JSONObject memberObj = result.getJSONObject(i);
            Logger.d("Member ------> "+memberObj.toString());
            members.add(new User(memberObj));
        }
        task.getSubscriber().onComplete(members);
    }

}
