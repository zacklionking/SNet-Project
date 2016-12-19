package jcoolj.com.dribbble.data;

import android.support.annotation.StringDef;

import com.squareup.okhttp.Request;

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

    public void getShots(Subscriber<List<Shot>> subscriber) {
        String url = URLParser.getUrlAPI("shots") + "&page=" + shotPage + "&per_page=" + shotPerPage +
                (sort.length() > 0 ? "&sort=" + sort : "");
        Logger.d("GetShots params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        sendTask(subscriber, request, MSG_SHOTS);
    }

    public void getShots(Subscriber<List<Shot>> subscriber, int userId){
        String url = URLParser.getUrlAPI("users/" + userId + "/shots") + "&page=" + shotPage + "&per_page=" + shotPerPage +
                (sort.length() > 0 ? "&sort=" + sort : "");
        Logger.d("GetUserShots params: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        sendTask(subscriber, request, userId);
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
        sendTask(subscriber, request, MSG_ATTACHMENT);
    }

    public void getComments(Subscriber<List<Comment>> subscriber, long shotId, int perPage){
        String url = URLParser.getUrlAPI("shots" + "/" + shotId + "/comments") + "&page=" + commentPage + "&per_page=" + perPage;
        Request request = new Request.Builder()
                .url(url)
                .build();
        sendTask(subscriber, request, MSG_COMMENTS);
    }

    public void setShotsRequestPerPage(int perPage) {
        shotPerPage = perPage;
    }

    public void setShotsRequestPage(int page){
        if(page < 1)
            page = 1;
        shotPage = page;
    }

    public void setCommentRequestPage(int page) {
        if(page < 1)
            page = 1;
        commentPage = page;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onResponse(STask task) throws Exception {
        JSONArray result = new JSONArray((String) task.getResponse().getBody());
        switch (task.getTaskId()) {
            case MSG_ATTACHMENT:
                List<Attachment> attachments = new ArrayList<>();
                for(int i=0; i<result.length(); i++){
                    JSONObject obj = result.getJSONObject(i);
                    Attachment attachment = new Attachment();
                    attachment.setId(obj.optInt("id"));
                    attachment.setUrl(obj.optString("url"));
                    attachment.setThumbUrl(obj.optString("thumbnail_url"));
                    attachments.add(attachment);
                }
                task.getSubscriber().onComplete(attachments);
                break;
            case MSG_COMMENTS:
                List<Comment> comments = new ArrayList<>();
                for(int i=0; i<result.length(); i++){
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
                task.getSubscriber().onComplete(comments);
                break;
            default:
                List<Shot> shots = new ArrayList<>();
                for(int i=0; i<result.length(); i++){
                    JSONObject obj = result.getJSONObject(i);
                    Logger.d("Shot ------ > "+obj.toString());
                    Shot shot = new Shot(obj);
                    JSONObject userObj = obj.optJSONObject("user");
                    if(userObj != null){
                        Logger.d("Shot User ------ > " + userObj.toString());
                        shot.setUser(new User(userObj));
                    } else if(task.getTaskId() != MSG_SHOTS) {
                        User user = new User();
                        user.setId(task.getTaskId());
                        shot.setUser(user);
                    }
                    JSONObject teamObj = obj.optJSONObject("team");
                    if(teamObj != null) {
                        if(teamObj.optInt("id") != shot.getUser().getId()){
                            Logger.d("Shot Team ------ > " + teamObj.toString());
                            shot.setTeam(new User(teamObj));
                        } else
                            shot.getUser().setMembersCount(teamObj.optInt("members_count"));
                    }
                    shots.add(shot);
                }
                shotPage++;
                task.getSubscriber().onComplete(shots);
        }
    }

}
