package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/5/1 at 11:38 PM.
 */
public class RequestAddChannelPostBody {

    private String ichatIdUser;

    private String message;

    public RequestAddChannelPostBody() {
    }

    public RequestAddChannelPostBody(String ichatIdUser, String message) {
        this.ichatIdUser = ichatIdUser;
        this.message = message;
    }

    public String getIchatIdUser() {
        return ichatIdUser;
    }

    public String getMessage() {
        return message;
    }
}
