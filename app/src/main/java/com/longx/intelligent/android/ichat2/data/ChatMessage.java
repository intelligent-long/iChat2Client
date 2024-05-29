package com.longx.intelligent.android.ichat2.data;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.media.helper.MediaHelper;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.yier.ChatMessageUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/5/13 at 12:08 AM.
 */
public class ChatMessage implements Parcelable {

    public static void mainDoOnNewChatMessage(ChatMessage chatMessage, Context context, ResultsYier resultsYier){
        if(chatMessage.getType() == ChatMessage.TYPE_IMAGE){
            ChatApiCaller.fetchChatMessageImage(null, chatMessage.imageId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context){
                @Override
                public void ok(ResponseBody data, Response<ResponseBody> row, Call<ResponseBody> call) {
                    super.ok(data, row, call);
                    try {
                        byte[] bytes = data.bytes();
                        chatMessage.setImageBytes(bytes);
                        String imageFilePath = PrivateFilesAccessor.ChatImage.save(context, chatMessage);
                        chatMessage.setImageFilePath(imageFilePath);
                        Size imageSize = MediaHelper.getImageSize(bytes);
                        chatMessage.setImageSize(imageSize);
                        commonDoOfMainDoOnNewChatMessage(chatMessage, context);
                        resultsYier.onResults();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }else {
            commonDoOfMainDoOnNewChatMessage(chatMessage, context);
            resultsYier.onResults();
        }
    }

    private static synchronized void commonDoOfMainDoOnNewChatMessage(ChatMessage chatMessage, Context context) {
        ChatMessage.determineShowTime(chatMessage, context);
        String other = chatMessage.getOther(context);
        ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, other);
        boolean success = chatMessageDatabaseManager.insertOrIgnore(chatMessage);
        if (success && chatMessage.isShowTime()) {
            SharedPreferencesAccessor.ChatMessageTimeShowing.saveLastShowingTime(context, other, chatMessage.getTime());
        }
    }

    private static void determineShowTime(ChatMessage chatMessage, Context context) {
        chatMessage.showTime = SharedPreferencesAccessor.ChatMessageTimeShowing.isShowTime(context, chatMessage.getOther(context), chatMessage.getTime());
    }

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_VOICE = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_FILE = 4;
    public static final int TYPE_NOTICE = 5;
    private int type;
    private String uuid;
    private String from;
    private String to;
    private Date time;
    private String text;
    private String imageId;
    private String imageExtension;

    @JsonIgnore
    private Boolean showTime;
    @JsonIgnore
    private Boolean viewed;
    @JsonIgnore
    private String imageFilePath;
    @JsonIgnore
    private Size imageSize;
    @JsonIgnore
    private byte[] imageBytes;

    public ChatMessage() {
    }

    public ChatMessage(int type, String uuid, String from, String to, Date time, String text, String imageId, String imageExtension) {
        this.type = type;
        this.uuid = uuid;
        this.from = from;
        this.to = to;
        this.text = text;
        this.time = time;
        this.imageId = imageId;
        this.imageExtension = imageExtension;
    }

    public int getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public Date getTime() {
        return time;
    }

    public String getImageId() {
        return imageId;
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public boolean isSelfSender(Context context){
        return SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context).getIchatId().equals(from);
    }

    public String getOther(Context context){
        if(isSelfSender(context)){
            return getTo();
        }else {
            return getFrom();
        }
    }

    public boolean isShowTime() {
        if(showTime == null) throw new RuntimeException();
        return showTime;
    }

    public void setShowTime(Boolean showTime) {
        this.showTime = showTime;
    }

    public Boolean isViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public Size getImageSize() {
        return imageSize;
    }

    public void setImageSize(Size imageSize) {
        this.imageSize = imageSize;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return type == that.type && Objects.equals(uuid, that.uuid) && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, uuid, from, to, time);
    }

    protected ChatMessage(Parcel in) {
        type = in.readInt();
        uuid = in.readString();
        from = in.readString();
        to = in.readString();
        time = new Date(in.readLong());
        text = in.readString();
        imageId = in.readString();
        imageExtension = in.readString();
        byte tmpShowTime = in.readByte();
        showTime = tmpShowTime == 0 ? null : tmpShowTime == 1;
        byte tmpViewed = in.readByte();
        viewed = tmpViewed == 0 ? null : tmpViewed == 1;
        imageFilePath = in.readString();
        imageSize = in.readSize();
        int imageBytesLength = in.readInt();
        if (imageBytesLength >= 0) {
            imageBytes = new byte[imageBytesLength];
            in.readByteArray(imageBytes);
        } else {
            imageBytes = null;
        }
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(uuid);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeLong(time.getTime());
        dest.writeString(text);
        dest.writeString(imageId);
        dest.writeString(imageExtension);
        dest.writeByte((byte) (showTime == null ? 0 : showTime ? 1 : 2));
        dest.writeByte((byte) (viewed == null ? 0 : viewed ? 1 : 2));
        dest.writeString(imageFilePath);
        dest.writeSize(imageSize);
        if (imageBytes != null) {
            dest.writeInt(imageBytes.length);
            dest.writeByteArray(imageBytes);
        } else {
            dest.writeInt(-1);
        }
    }
}
