package jcoolj.com.dribbble.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.bean.User;

public class FavoritesProvider extends ContentProvider {

    private static final UriMatcher mMatcher;

    private static final int URI_SHOT            = 1;
    private static final int URI_SHOT_ITEM       = 2;
    private static final int URI_USER            = 3;
    private static final int URI_USER_ITEM       = 4;

    public static final Uri CONTENT_SHOT_URI = Uri.parse("content://" + Shot.AUTHORITY + "/shot");
    public static final Uri CONTENT_USER_URI = Uri.parse("content://" + Shot.AUTHORITY + "/user");

    private FavoritesHelper mHelper;

    private ContentResolver mResolver;

    static {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(Shot.AUTHORITY, "shot", URI_SHOT);
        mMatcher.addURI(Shot.AUTHORITY, "shot/#", URI_SHOT_ITEM);
        mMatcher.addURI(Shot.AUTHORITY, "user", URI_USER);
        mMatcher.addURI(Shot.AUTHORITY, "user/#", URI_USER_ITEM);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if(context == null)
            return false;
        mHelper = FavoritesHelper.getInstance(context);
        mResolver = context.getContentResolver();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c;
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String id;
        switch (mMatcher.match(uri)) {
            case URI_SHOT:
                c = db.query(FavoritesHelper.TABLE_SHOT_NAME, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case URI_SHOT_ITEM:
                id = uri.getPathSegments().get(1);
                c = db.query(FavoritesHelper.TABLE_SHOT_NAME, projection, Shot.ShotColumns.ID + "=" + id, selectionArgs, null, null, sortOrder);
                break;
            case URI_USER:
                c = db.query(FavoritesHelper.TABLE_USER_NAME, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case URI_USER_ITEM:
                id = uri.getPathSegments().get(1);
                c = db.query(FavoritesHelper.TABLE_USER_NAME, projection, User.UserColumns.ID + "=" + id, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (c != null) {
            c.setNotificationUri(mResolver, uri);
        }
        Logger.d("Favorite cursor query:"+c);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long insertedId = 0;
        switch (mMatcher.match(uri)) {
            case URI_SHOT:
                if (values.containsKey(Shot.ShotColumns.ID)) {
                    insertedId = values.getAsLong(Shot.ShotColumns.ID);
                }
                Logger.d("Insert favorite shot " + insertedId);
                db.insert(FavoritesHelper.TABLE_SHOT_NAME, null, values);
                break;
            case URI_USER:
                if (values.containsKey(User.UserColumns.ID)) {
                    insertedId = values.getAsLong(User.UserColumns.ID);
                }
                Logger.d("Insert favorite user " + insertedId);
                db.insert(FavoritesHelper.TABLE_USER_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (insertedId > 0)
            mResolver.notifyChange(ContentUris.withAppendedId(uri, insertedId), null);

        return ContentUris.withAppendedId(uri, insertedId);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int deleteId = 0;
        switch (mMatcher.match(uri)) {
            case URI_SHOT:
                deleteId = db.delete(FavoritesHelper.TABLE_SHOT_NAME, selection, selectionArgs);
                break;
            case URI_SHOT_ITEM:
                String id = uri.getPathSegments().get(1);
                deleteId = Integer.parseInt(id);
                Logger.d("Delete favorite shot " + deleteId);
                db.delete(FavoritesHelper.TABLE_SHOT_NAME, Shot.ShotColumns.ID + "=?", new String[]{id});
                break;
            case URI_USER:
                deleteId = db.delete(FavoritesHelper.TABLE_USER_NAME, selection, selectionArgs);
                break;
            case URI_USER_ITEM:
                id = uri.getPathSegments().get(1);
                deleteId = Integer.parseInt(id);
                Logger.d("Delete favorite user " + deleteId);
                deleteId = db.delete(FavoritesHelper.TABLE_USER_NAME, User.UserColumns.ID + "=?", new String[]{id});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (deleteId > 0)
            mResolver.notifyChange(ContentUris.withAppendedId(uri, deleteId), null);

        return deleteId;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
