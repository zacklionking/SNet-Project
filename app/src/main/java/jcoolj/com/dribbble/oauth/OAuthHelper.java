package jcoolj.com.dribbble.oauth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;

import java.io.IOException;
import java.util.Arrays;

import jcoolj.com.core.network.SNetManager;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.core.network.Task;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.utils.URLParser;

/**
 * Dribbble OAuth2.0 授权帮助类，调用{@link #getAuthorizationUrl()}获取用户授权页地址
 * 用户接受授权后回调时调用{@link #requestAccessToken(Subscriber, String)}请求Access Token
 */
public class OAuthHelper {

    private final static String USER_ID = "client_jcoolj";

    private final static int MSG_TOKEN_SUCCESS = 0x00118;
    private final static int MSG_TOKEN_FAIL = 0x00119;

    private AuthorizationCodeFlow flow;
    private OAuthCredentials credentials;

    private Handler handler;
    private Subscriber<StoredCredential> subscriber;
    private Task task;

    public OAuthHelper(Context context){
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_TOKEN_SUCCESS:
                        subscriber.onComplete((StoredCredential) msg.obj);
                        break;
                    case MSG_TOKEN_FAIL:
                        subscriber.onRefuse((Throwable) msg.obj);
                        break;
                }
            }
        };
        newCredentials(context);
    }

    private void newCredentials(Context context){
        credentials = OAuthCredentials.newBuilder(
                URLParser.CLIENT_ID,
                URLParser.CLIENT_SECRET,
                URLParser.ACCESS_TOKEN,
                URLParser.REDIRECT_URL)
                .setScope(Arrays.asList(
                        "public",
                        "write",
                        "upload",
                        "comment")
                ).setState("jcoolj").build();
        AuthorizationCodeFlow.Builder builder = new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                new ApacheHttpTransport(),
                new JacksonFactory(),
                new GenericUrl(URLParser.getUrlOAuthToken()),
                new ClientParametersAuthentication(credentials.getClientId(), credentials.getClientSecret()),
                credentials.getClientId(),
                credentials.getOauthUrl());
        try {
            SharedPreferencesDataStoreFactory dataStoreFactory = new SharedPreferencesDataStoreFactory(context);
            DataStore<StoredCredential> dataStore = dataStoreFactory.getDataStore(credentials.getClientId());
            builder.setCredentialDataStore(dataStore);
        } catch (IOException ignored) { }
        flow = builder.build();
    }

    public String getAuthorizationUrl(){
        return flow.newAuthorizationUrl().build();
    }

    public StoredCredential getCredential() throws IOException {
        DataStore<StoredCredential> dataStore = flow.getCredentialDataStore();
        if(dataStore != null)
            return flow.getCredentialDataStore().get(USER_ID);
        return null;
    }

    public void requestAccessToken(Subscriber<StoredCredential> subscriber, final String redirectUrl) {
        this.subscriber = subscriber;
        if(task != null)
            task.cancel();
        task = new Task(0, handler) {
            @Override
            public void doWork() throws Exception {
                DataStore<StoredCredential> dataStore = flow.getCredentialDataStore();
                if(dataStore != null) {
                    StoredCredential credential = dataStore.get(USER_ID);
                    if (credential != null && credential.getAccessToken() != null
                            && (credential.getRefreshToken() != null ||
                            credential.getExpirationTimeMilliseconds() == null ||
                            credential.getExpirationTimeMilliseconds() > 60000)) {
                        Message msg = handler.obtainMessage(MSG_TOKEN_SUCCESS);
                        msg.obj = credential;
                        handler.sendMessage(msg);
                        return;
                    }
                }

                AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(redirectUrl);
                String error = responseUrl.getError();
                if (!TextUtils.isEmpty(error)
                        && !TextUtils.isEmpty(responseUrl.getErrorDescription())) {
                    error += (": " + responseUrl.getErrorDescription());
                    Logger.d("requestAuthorizationCode error " + error);
                    Message msg = handler.obtainMessage(MSG_TOKEN_FAIL);
                    msg.obj = new Exception(error);
                    handler.sendMessage(msg);
                    return;
                }
                String code = responseUrl.getCode();
                Logger.d("requestAccessToken " + code);
                String redirectUri = credentials.getRedirectUrl();
                TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
                StoredCredential credential = new StoredCredential();
                credential.setAccessToken(response.getAccessToken());
                credential.setRefreshToken(response.getRefreshToken());
                credential.setExpirationTimeMilliseconds(response.getExpiresInSeconds() == null ? 0 : response.getExpiresInSeconds() * 1000);
                // 保存Credential到SharedPreferences
                if(dataStore != null)
                    dataStore.set(USER_ID, credential);
                Message msg = handler.obtainMessage(MSG_TOKEN_SUCCESS);
                msg.obj = credential;
                handler.sendMessage(msg);
            }
        };
        SNetManager.getInstance().startTask(task);
    }

}
