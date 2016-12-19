package jcoolj.com.dribbble.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.List;

public class User implements Parcelable, Comparable<User> {

    public final static String TAG = "User";

    public static final String AUTHORITY = "jcoolj_user";

    public interface UserColumns {
        String ID = "user_id";
        String AVATAR = "avatar_url";
        String NAME = "user_name";
        String BIO = "user_bio";
    }

    private long id;

    private String name;

    private String avatarUrl;

    private String location;

    private String bio;

    private int shotsCount;

    private int bucketsCount;

    private int projectsCount;

    private int followersCount;

    private int likesReceivedCount;

    private List<Shot> shots;

    private int membersCount;

    public User(){

    }

    public User(JSONObject userObj){
        setId(userObj.optInt("id"));
        setName(userObj.optString("name"));
        setAvatarUrl(userObj.optString("avatar_url"));
        setLocation(userObj.optString("location"));
        setBio(userObj.optString("bio"));
        setShotsCount(userObj.optInt("shots_count"));
        setBucketsCount(userObj.optInt("buckets_count"));
        setProjectsCount(userObj.optInt("projects_count"));
        setFollowersCount(userObj.optInt("followers_count"));
        setLikesReceivedCount(userObj.optInt("likes_received_count"));
        setMembersCount(userObj.optInt("members_count"));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getShotsCount() {
        return shotsCount;
    }

    public void setShotsCount(int shotsCount) {
        this.shotsCount = shotsCount;
    }

    public int getBucketsCount() {
        return bucketsCount;
    }

    public void setBucketsCount(int bucketsCount) {
        this.bucketsCount = bucketsCount;
    }

    public int getProjectsCount() {
        return projectsCount;
    }

    public void setProjectsCount(int projectsCount) {
        this.projectsCount = projectsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getLikesReceivedCount() {
        return likesReceivedCount;
    }

    public void setLikesReceivedCount(int likesReceivedCount) {
        this.likesReceivedCount = likesReceivedCount;
    }

    public List<Shot> getShots() {
        return shots;
    }

    public void setShots(List<Shot> shots) {
        this.shots = shots;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(avatarUrl);
        dest.writeString(location);
        dest.writeString(bio);
        dest.writeInt(shotsCount);
        dest.writeInt(bucketsCount);
        dest.writeInt(projectsCount);
        dest.writeInt(followersCount);
        dest.writeInt(likesReceivedCount);
        dest.writeInt(membersCount);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){

        public User createFromParcel(Parcel in){
            User user = new User();
            user.id = in.readLong();
            user.name = in.readString();
            user.avatarUrl = in.readString();
            user.location = in.readString();
            user.bio = in.readString();
            user.shotsCount = in.readInt();
            user.bucketsCount = in.readInt();
            user.projectsCount = in.readInt();
            user.followersCount = in.readInt();
            user.likesReceivedCount = in.readInt();
            user.membersCount = in.readInt();
            return user;
        }

        public User[] newArray(int size){
            return new User[size];
        }

    };

    @Override
    public int compareTo(@NonNull User another) {
        return (int) (id - another.getId());
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof User && id == ((User) o).getId();
    }

}
