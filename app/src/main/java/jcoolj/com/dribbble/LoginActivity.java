package jcoolj.com.dribbble;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;

import jcoolj.com.base.FullScreenActivity;
import jcoolj.com.base.OnLoadListener;
import jcoolj.com.base.utils.AnimationHelper;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.oauth.OAuthHelper;
import jcoolj.com.dribbble.utils.URLParser;
import jcoolj.com.dribbble.view.DribbbleView;
import jcoolj.com.dribbble.view.SWebView;

import static jcoolj.com.dribbble.view.SWebView.*;

public class LoginActivity extends FullScreenActivity implements OnLoadListener, Subscriber<StoredCredential> {

    private View view_loadingContainer;
    private DribbbleView view_loading;
    private TextView view_loadingTip;
    private SWebView view_login;

    private OAuthHelper oAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        view_loadingContainer = findViewById(R.id.loading_container);
        view_loadingContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view_loadingTip.getVisibility() == VISIBLE) {
                    onLoadStarted();
                    view_login.reload();
                }
            }
        });
        view_loading = (DribbbleView) findViewById(R.id.web_loading);
        view_loadingTip = (TextView) findViewById(R.id.loading_tips);

        view_login = (SWebView) findViewById(R.id.login_view);

        oAuthHelper = new OAuthHelper(this);
        view_login.loadUrl(oAuthHelper.getAuthorizationUrl());

        WebSettings webSettings = view_login.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        view_login.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                WebView wv = (WebView) v;
                if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()) {
                    wv.goBack();
                    return true;
                }
                return false;
            }
        });
        view_login.setWebViewClient(new SWebView.SWebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                interceptUrlCompat(view, url);
                return true;
            }

            @Override
            public void onRenderingFinished(WebView view, String url) {
                super.onRenderingFinished(view, url);
                onLoadFinished();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                onLoadFailed();
            }
        });
        onLoadStarted();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view_login.destroy();
    }

    private void interceptUrlCompat(WebView view, String url) {
        if (URLParser.accessAccepted(url)) {
            // 用户接受授权，申请Access Token
            oAuthHelper.requestAccessToken(this, url);
        } else
            view.loadUrl(url);
    }

    @Override
    public void onLoadStarted() {
        view_loading.bounce();
        view_loadingTip.setVisibility(GONE);
    }

    @Override
    public void onLoadFailed() {
        view_loading.stop();
        view_loadingTip.setVisibility(VISIBLE);
        view_loadingTip.setText(getString(R.string.loading_fail));
    }

    @Override
    public void onLoadFinished() {
        AnimationHelper.createDisappearAnimation(view_loadingContainer).start();
    }

    @Override
    public void onSubscribe() {

    }

    @Override
    public void onRefuse(Throwable e) {

    }

    @Override
    public void onComplete(StoredCredential credential) {
        Logger.d("Get accessToken "+credential.getAccessToken() + " Expired "+credential.getExpirationTimeMilliseconds());
    }

}
