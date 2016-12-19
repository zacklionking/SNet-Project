package jcoolj.com.dribbble.data;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.bean.User;

public class FavoritesManager {

    public static final int TYPE_SHOT = 0;
    public static final int TYPE_USER = 1;

    public static void registerFavorUsers(@NonNull Activity activity, LoaderManager.LoaderCallbacks callbacks){
        activity.getLoaderManager().initLoader(TYPE_USER, null, callbacks);
    }

    public static void registerFavorShots(@NonNull Activity activity, LoaderManager.LoaderCallbacks callbacks){
        activity.getLoaderManager().initLoader(TYPE_SHOT, null, callbacks);
    }

    public static void add(@NonNull Context context, Shot shot){
        ContentValues values = new ContentValues();
        values.put(Shot.ShotColumns.ID, shot.getId());
        values.put(Shot.ShotColumns.URL, shot.getTeaserUrl());
        context.getContentResolver().insert(FavoritesProvider.CONTENT_SHOT_URI, values);
    }

    public static void remove(@NonNull Context context, Shot shot){
        context.getContentResolver().delete(ContentUris.withAppendedId(FavoritesProvider.CONTENT_SHOT_URI, shot.getId()), null, null);
    }

    public static void add(@NonNull Context context, User user){
        ContentValues values = new ContentValues();
        values.put(User.UserColumns.ID, user.getId());
        values.put(User.UserColumns.AVATAR, user.getAvatarUrl());
        values.put(User.UserColumns.NAME, user.getName());
        values.put(User.UserColumns.BIO, user.getBio());
        context.getContentResolver().insert(FavoritesProvider.CONTENT_SHOT_URI, values);
    }

    public static void remove(@NonNull Context context, User user){
        context.getContentResolver().delete(ContentUris.withAppendedId(FavoritesProvider.CONTENT_SHOT_URI, user.getId()), null, null);
    }

}
