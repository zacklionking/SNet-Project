package jcoolj.com.dribbble.oauth;

import android.text.TextUtils;

import java.util.Collection;

import jcoolj.com.dribbble.utils.URLParser;

/**
 * Created by Zack on 2016/11/30.
 */

public class OAuthCredentials {

    private String clientId;

    private String clientSecret;

    private String clientAccessToken;

    private String redirectUrl;

    private String state;

    private Collection<String> scopes;

    private OAuthCredentials(String clientId, String clientSecret, String clientAccessToken, String redirectUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientAccessToken = clientAccessToken;
        this.redirectUrl = redirectUrl;
    }

    public String getOauthUrl() {
        return URLParser.getUrlAuthorize(state);
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientAccessToken() {
        return clientAccessToken;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getState() {
        return state;
    }

    public Collection<String> getScopes() {
        return scopes;
    }

    public static Builder newBuilder(String clientId, String clientSecret, String clientAccessToken, String redirectUrl) {
        return new OAuthCredentials(clientId, clientSecret, clientAccessToken, redirectUrl).new Builder();
    }

    public class Builder {

        private Builder() {
            // private constructor
        }

        public Builder setState(String state) {
            OAuthCredentials.this.state = state;
            return this;
        }

        public Builder setScope(Collection<String> scope) {
            OAuthCredentials.this.scopes = scope;
            return this;
        }

        public OAuthCredentials build() {
            return OAuthCredentials.this;
        }

    }
}
