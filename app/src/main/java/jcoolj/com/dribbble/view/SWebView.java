package jcoolj.com.dribbble.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jcoolj.com.core.utils.Logger;

public class SWebView extends WebView {

    private SWebViewClient client;

    public SWebView(Context context) {
        super(context);
    }

    public SWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // defStyleAttr为0时可屏蔽软键盘弹出
    public SWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setWebViewClient(SWebViewClient client) {
        this.client = client;
        super.setWebViewClient(client);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if(getContentHeight() > 0 && client != null && !client.isRenderFinished() && !client.isLoadingFailed())
            client.onRenderingFinished(this, getUrl());
    }

    public static class SWebViewClient extends WebViewClient{

        private boolean isRendered = true;
        private boolean isLoadFail;

        public boolean isRenderFinished(){
            return isRendered;
        }

        public boolean isLoadingFailed(){
            return isLoadFail;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Logger.d("onPageStarted " + url);
            if(isRendered){
                isLoadFail = false;
                isRendered = false;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Logger.d("onPageFinished " + url);
        }

        public void onRenderingFinished(WebView view, String utl){
            Logger.d("onRenderingFinished ");
            isRendered = true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Logger.d("onReceivedError " + errorCode);
            isLoadFail = true;
        }
    }

}
