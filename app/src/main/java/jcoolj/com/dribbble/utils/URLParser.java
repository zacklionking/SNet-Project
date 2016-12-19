package jcoolj.com.dribbble.utils;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Set;

public class URLParser {

    public static final String CLIENT_ID = "84d39563f91e18c8b679ee8299eff817f14f7fd96f0027f3b1989e6ea6d21573";
    public static final String CLIENT_SECRET = "2f569d2c4bd9f1c1157778a876dd07af204ad1ba0a36c7612060cb9ee2132a90";
    public static final String ACCESS_TOKEN = "9963d9c680a43ad990f69bebf2877398456ce119c4e79b63f2d2687b9ad40c4a";
    public static final String REDIRECT_URL = "https://callback";

    private static final String URL_API = "https://api.dribbble.com/v1/";
    private static final String URL_AUTH = "https://dribbble.com/oauth/authorize";
    private static final String URL_TOKEN = "https://dribbble.com/oauth/token";

    private static final String OAUTH_URL_STATE_TEMPLATE = "?state=%s";

    public static String getUrlAPI(String api){
        return URL_API + api + "?access_token=" + ACCESS_TOKEN;
    }

    public static String getUrlAuthorize(String state){
        if(TextUtils.isEmpty(state))
            return getUrlAuthorize();
        return URL_AUTH + String.format(OAUTH_URL_STATE_TEMPLATE, state);
    }

    public static String getUrlAuthorize(){
        return URL_AUTH + OAUTH_URL_STATE_TEMPLATE;
    }

    public static String getUrlOAuthToken(){
        return URL_TOKEN;
    }

    /**
     * @param code  授权返回的code参数值
     * @return
     */
    public static String getUrlOAuthToken(String code){
        return URL_TOKEN + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&code=" + code;
    }

    public static boolean accessAccepted(String uri) {
        Uri u = null;
        Uri r = null;
        try {
            u = Uri.parse(uri);
            r = Uri.parse(REDIRECT_URL);
        } catch (NullPointerException e) {
            return false;
        }

        if (u == null || r == null) {
            return false;
        }
        boolean rOpaque = r.isOpaque();
        boolean uOpaque = u.isOpaque();
        if (rOpaque != uOpaque) {
            return false;
        }
        if (rOpaque) {
            return TextUtils.equals(uri, REDIRECT_URL);
        }

        if (!TextUtils.equals(r.getScheme(), u.getScheme())) {
            return false;
        }

        if (!TextUtils.equals(r.getAuthority(), u.getAuthority())) {
            return false;
        }

        if (r.getPort() != u.getPort()) {
            return false;
        }

        if (!TextUtils.isEmpty(r.getPath()) && !TextUtils.equals(r.getPath(), u.getPath())) {
            return false;
        }

        Set<String> paramKeys = CompatUri.getQueryParameterNames(r);
        for (String key : paramKeys) {
            if (!TextUtils.equals(r.getQueryParameter(key), u.getQueryParameter(key))) {
                return false;
            }
        }

        String frag = r.getFragment();
        if (!TextUtils.isEmpty(frag)
                && !TextUtils.equals(frag, u.getFragment())) {
            return false;
        }
        return true;
    }

}
