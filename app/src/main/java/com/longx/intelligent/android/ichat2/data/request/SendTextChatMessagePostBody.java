package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/5/12 at 10:53 PM.
 */
public class SendTextChatMessagePostBody {
    private String toIchatId;
    private String text;

    public SendTextChatMessagePostBody() {
    }

    public SendTextChatMessagePostBody(String toIchatId, String text) {
        this.toIchatId = toIchatId;
        this.text = text;
    }

    public String getToIchatId() {
        return toIchatId;
    }

    public String getText() {
        return text;
    }
}
