package jcoolj.com.dribbble.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.bean.User;

public class FavoritesHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "favorites.db";
    public static final String TABLE_SHOT_NAME = "shot";
    public static final String TABLE_USER_NAME = "user";

    private static final int DB_VERSION = 7;

    private static final String CREATE_SHOT_TABLE_SQL =
            "CREATE TABLE " + TABLE_SHOT_NAME + "(" +
                    "_id INTEGER PRIMARY KEY," +
                    Shot.ShotColumns.ID + " INTEGER," +
                    Shot.ShotColumns.URL + " TEXT NOT NULL DEFAULT '')";

    private static final String CREATE_USER_TABLE_SQL =
            "CREATE TABLE " + TABLE_USER_NAME + "(" +
                    "_id INTEGER PRIMARY KEY," +
                    User.UserColumns.ID + " INTEGER," +
                    User.UserColumns.AVATAR + " TEXT NOT NULL DEFAULT ''," +
                    User.UserColumns.NAME + " TEXT NOT NULL DEFAULT ''," +
                    User.UserColumns.BIO + " TEXT NOT NULL DEFAULT '')";

    private static FavoritesHelper instance;
    public static FavoritesHelper getInstance(Context context){
        if(instance == null)
            instance = new FavoritesHelper(context.getApplicationContext());
        return instance;
    }

    protected FavoritesHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SHOT_TABLE_SQL);
        db.execSQL(CREATE_USER_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOT_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_NAME);
        onCreate(db);
    }

}
