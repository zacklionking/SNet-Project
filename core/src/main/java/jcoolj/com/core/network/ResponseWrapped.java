package jcoolj.com.core.network;

import okhttp3.Headers;

public class ResponseWrapped {

    private int code;
    private Headers headers;

    private final String body;

    public ResponseWrapped(int code, Headers headers, String body){
        this.code = code;
        this.headers = headers;
        this.body = body;
    }

    public int getCode(){
        return code;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getHeader(String key){
        return headers.get(key);
    }

}
