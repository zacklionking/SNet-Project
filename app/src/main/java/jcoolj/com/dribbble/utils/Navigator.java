package jcoolj.com.dribbble.utils;

import android.content.Context;
import android.content.Intent;

import jcoolj.com.dribbble.ImageViewerActivity;
import jcoolj.com.dribbble.ShotDetailActivity;
import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.bean.User;

public class Navigator {

    private static final String INTENT_TRANSITION = "isOverridePendingTransition";
    private static final String INTENT_REFRESH_FLAG = "needRefresh";
    private static final String INTENT_SHOT = "shot";
    private static final String INTENT_USER = "user";
    private static final String INTENT_IMAGE_URL = "imageUrl";

    public static void navigateToShotCard(Context context, Shot shot){
        Intent intent = new Intent(context, ShotDetailActivity.class);
        intent.putExtra(INTENT_TRANSITION, true);
        intent.putExtra(INTENT_SHOT, shot);
        context.startActivity(intent);
    }

    public static void navigateToShotCard(Context context, Shot shot, boolean needRefresh){
        Intent intent = new Intent(context, ShotDetailActivity.class);
        intent.putExtra(INTENT_TRANSITION, true);
        intent.putExtra(INTENT_REFRESH_FLAG, needRefresh);
        intent.putExtra(INTENT_SHOT, shot);
        context.startActivity(intent);
    }

    public static void navigateToUserCard(Context context, User user){
        Intent intent = new Intent(context, ShotDetailActivity.class);
        intent.putExtra(INTENT_USER, user);
        context.startActivity(intent);
    }

    public static void navigateToUserCard(Context context, User user, boolean needRefresh){
        Intent intent = new Intent(context, ShotDetailActivity.class);
        intent.putExtra(INTENT_REFRESH_FLAG, needRefresh);
        intent.putExtra(INTENT_USER, user);
        context.startActivity(intent);
    }

    public static void navigateToImageViewer(Context context, String url) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(INTENT_IMAGE_URL, url);
        context.startActivity(intent);
    }

    public static boolean isNeedRefresh(Intent intent) {
        return intent.getBooleanExtra(INTENT_REFRESH_FLAG, false);
    }

    public static Shot getShot(Intent intent) {
        return intent.getParcelableExtra(INTENT_SHOT);
    }

    public static User getUser(Intent intent) {
        return intent.getParcelableExtra(INTENT_USER);
    }

    public static String getImageUrl(Intent intent) {
        return intent.getStringExtra(INTENT_IMAGE_URL);
    }

}
