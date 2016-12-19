package jcoolj.com.core.network;

import com.squareup.okhttp.Headers;

public class ResponseWrapped {

    private int code;
    private Headers headers;

    private final Object body;

    public ResponseWrapped(int code, Headers headers, Object body){
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

    public Object getBody() {
        return body;
    }

    public String getHeader(String key){
        return headers.get(key);
    }

}
