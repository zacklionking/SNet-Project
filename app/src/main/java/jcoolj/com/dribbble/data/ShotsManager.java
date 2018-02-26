package jcoolj.com.dribbble.data;

import android.support.annotation.StringDef;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import jcoolj.com.core.network.STask;
import jcoolj.com.core.network.TaskManager;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.bean.Attachment;
import jcoolj.com.dribbble.bean.Comment;
import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.utils.URLParser;
import okhttp3.Request;

public class ShotsManager extends TaskManager {

    private final static int DEFAULT_PER_PAGE = 20;
    private final static int MSG_SHOTS = -0x101;
    private final static int MSG_ATTACHMENT = -0x102;
    private final static int MSG_COMMENTS = -0x103;

    public final static String SORT_POPULAR = "";
    public final static String SORT_RECENT = "recent";

    @StringDef({SORT_POPULAR, SORT_RECENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Sort{}

    private int shotPage = 1;
    private int shotPerPage = DEFAULT_PER_PAGE;
    private int commentPage = 1;
    private String sort = "";

    private STask.Interceptor<List<Shot>> interceptorShots = new STask.Interceptor<List<Shot>>() {
        @Override
        public List<Shot> onResponse(int id, String response) throws Exception {
            JSONArray result = new JSONArray(response);
            List<Shot> shots = new ArrayList<>();
            for(int i=0; i < result.length(); i++){
                JSONObject obj = result.getJSONObject(i);
//                Logger.d("Shot ------ > "+obj.toString());
                Shot shot = new Shot(obj);
                JSONObject userObj = obj.optJSONObject("user");
                if(userObj != null){
//                    Logger.d("Shot User ------ > " + userObj.toString());
                    shot.setUser(new User(userObj));
                } else if(id != MSG_SHOTS) {
                    User user = new User();
                    user.setId(id);
                    shot.setUser(user);
                }
                JSONObject teamObj = obj.optJSONObject("team");
                if(teamObj != null) {
                    if(teamObj.optInt("id") != shot.getUser().getId()){
//                        Logger.d("Shot Team ------ > " + teamObj.toString());
                        shot.setTeam(new User(teamObj));
                    } else
                        shot.getUser().setMembersCount(teamObj.optInt("members_count"));
                }
                shots.add(shot);
            }
            shotPage++;
            return shots;
        }
    };

    private STask.Interceptor<List<Attachment>> interceptorAttachment = new STask.Interceptor<List<Attachment>>() {
        @Override
        public List<Attachment> onResponse(int id, String response) throws Exception {
            JSONArray result = new JSONArray(response);
            List<Attachment> attachments = new ArrayList<>();
            for(int i=0; i < result.length(); i++){
                JSONObject obj = result.getJSONObject(i);
                Attachment attachment = new Attachment();
                attachment.setId(obj.optInt("id"));
                attachment.setUrl(obj.optString("url"));
                attachment.setThumbUrl(obj.optString("thumbnail_url"));
                attachments.add(attachment);
            }
            return attachments;
        }
    };

    private STask.Interceptor<List<Comment>> interceptorComments = new STask.Interceptor<List<Comment>>() {
        @Override
        public List<Comment> onResponse(int id, String response) throws Exception {
            JSONArray result = new JSONArray(response);
            List<Comment> comments = new ArrayList<>();
            for(int i=0; i < result.length(); i++){
                JSONObject obj = result.getJSONObject(i);
                Comment comment = new Comment();
                comment.setId(obj.optInt("id"));
                comment.setBody(obj.optString("body"));
                comment.setLikes(obj.optInt("likes_count"));
                comment.setCreatedTime(obj.optString("created_at"));
                comment.setUpdatedTime(obj.optString("updated_at"));
                JSONObject userObj = obj.optJSONObject("user");
                if(userObj != null){
                    User user = new User();
                    user.setId(userObj.optInt("id"));
                    user.setName(userObj.optString("name"));
                    user.setAvatarUrl(userObj.optString("avatar_url"));
                    user.setLocation(userObj.optString("location"));
                    user.setBio(userObj.optString("bio"));
                    user.setShotsCount(userObj.optInt("shots_count"));
                    user.setBucketsCount(userObj.optInt("buckets_count"));
                    user.setProjectsCount(userObj.optInt("projects_count"));
                    user.setFollowersCount(userObj.optInt("followers_count"));
                    user.setLikesReceivedCount(userObj.optInt("likes_received_count"));
                    comment.setUser(user);
                }
                comments.add(comment);
            }
            commentPage++;
            return comments;
        }
    };

    public void getShots(Subscriber<List<Shot>> subscriber) {
        String url = URLParser.getUrlAPI("shots") + "&page=" + shotPage + "&per_page=" + shotPerPage +
                (sort.length() > 0 ? "&sort=" + sort : "");
        Logger.d("GetShots params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        newTask(subscriber, request, MSG_SHOTS).setInterceptor(interceptorShots).start();
    }

    public void getShots(Subscriber<List<Shot>> subscriber, int userId){
        String url = URLParser.getUrlAPI("users/" + userId + "/shots") + "&page=" + shotPage + "&per_page=" + shotPerPage +
                (sort.length() > 0 ? "&sort=" + sort : "");
        Logger.d("GetUserShots params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        newTask(subscriber, request, userId).setInterceptor(interceptorShots).start();
    }

    public void getShots(Subscriber<List<Shot>> subscriber, @Sort String sort){
        this.sort = sort.length() > 0 ? sort.toLowerCase() : this.sort;
        getShots(subscriber);
    }

    public void getAttachments(Subscriber<List<Attachment>> subscriber, long shotId){
        String url = URLParser.getUrlAPI("shots" + "/" + shotId + "/attachments");
        Logger.d("GetAttachments params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        newTask(subscriber, request, MSG_ATTACHMENT).setInterceptor(interceptorAttachment).start();
    }

    public void getComments(Subscriber<List<Comment>> subscriber, long shotId, int perPage){
        String url = URLParser.getUrlAPI("shots" + "/" + shotId + "/comments") + "&page=" + commentPage + "&per_page=" + perPage;
        Request request = new Request.Builder()
                .url(url)
                .build();
        newTask(subscriber, request, MSG_COMMENTS).setInterceptor(interceptorComments).start();
    }

    public void reset(){
        shotPage = commentPage = 1;
    }

}
