package jcoolj.com.dribbble.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class Shot implements Parcelable {

    public static final String TAG = "shot";

    public static final String AUTHORITY = "jcoolj_shot";

    public interface ShotColumns {
        String ID = "shot_id";
        String URL = "shot_url";
    }

    private long id;
    private String title;
    private String description;
    private String imgUrl;
    private String teaserUrl;
    private int views_count;
    private int likes_count;
    private int comments_count;
    private boolean animated;
    private int attachments_count;

    private User user;
    private User team;

    public Shot(){

    }

    public Shot(JSONObject obj) throws JSONException {
        setId(obj.optInt("id"));
        setTitle(obj.optString("title"));
        setDescription(obj.optString("description"));
        JSONObject imgs = obj.getJSONObject("images");
        setImgUrl(imgs.optString("hidpi"));
        setTeaserUrl(imgs.optString("teaser"));
        if(TextUtils.isEmpty(imgUrl) || imgUrl.equals("null"))
            imgUrl = teaserUrl;
        setViews_count(obj.optInt("views_count"));
        setComments_count(obj.optInt("comments_count"));
        setLikes_count(obj.optInt("likes_count"));
        setAttachments_count(obj.optInt("attachments_count"));
        setAnimated(obj.optBoolean("animated"));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTeaserUrl() {
        return teaserUrl;
    }

    public void setTeaserUrl(String teaserUrl) {
        this.teaserUrl = teaserUrl;
    }

    public int getViews_count() {
        return views_count;
    }

    public void setViews_count(int views_count) {
        this.views_count = views_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public int getAttachments_count() {
        return attachments_count;
    }

    public void setAttachments_count(int attachments_count) {
        this.attachments_count = attachments_count;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getTeam() {
        return team;
    }

    public void setTeam(User team) {
        this.team = team;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imgUrl);
        dest.writeString(teaserUrl);
        dest.writeInt(views_count);
        dest.writeInt(likes_count);
        dest.writeInt(comments_count);
        dest.writeInt(animated ? 1 : 0);
        dest.writeInt(attachments_count);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(team, flags);
    }

    public static final Parcelable.Creator<Shot> CREATOR = new Parcelable.Creator<Shot>(){

        public Shot createFromParcel(Parcel in){
            Shot shot = new Shot();
            shot.id = in.readLong();
            shot.title = in.readString();
            shot.description = in.readString();
            shot.imgUrl = in.readString();
            shot.teaserUrl = in.readString();
            shot.views_count = in.readInt();
            shot.likes_count = in.readInt();
            shot.comments_count = in.readInt();
            shot.animated = in.readInt() == 1;
            shot.attachments_count = in.readInt();
            shot.user = in.readParcelable(User.class.getClassLoader());
            shot.team = in.readParcelable(User.class.getClassLoader());
            return shot;
        }

        public Shot[] newArray(int size){
            return new Shot[size];
        }

    };

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Shot && id == ((Shot) o).getId();
    }

}
