package com.longx.intelligent.android.ichat2.behavior;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/4/1 at 4:41 AM.
 */
public class ServerContentUpdater {
    private static final List<String> updatingIds = new ArrayList<>();

    public interface OnServerContentUpdateYier {
        String ID_CURRENT_USER_INFO = "current_user_info";

        void onStartUpdate(String id);

        void onUpdateComplete(String id);
    }

    private static class ContentUpdateApiYier<T> extends RetrofitApiCaller.BaseYier<T>{
        private final String updateId;

        public ContentUpdateApiYier(String updateId) {
            this.updateId = updateId;
        }

        @Override
        public void start(Call<T> call) {
            synchronized (ServerContentUpdater.class) {
                updatingIds.add(updateId);
                super.start(call);
                onStartUpdate(updateId);
            }
        }

        @Override
        public void complete(Call<T> call) {
            synchronized (ServerContentUpdater.class) {
                updatingIds.remove(updateId);
                super.complete(call);
                onUpdateComplete(updateId);
            }
        }
    };

    public static boolean isUpdating(){
        return updatingIds.size() != 0;
    }

    public static List<String> getUpdatingIds() {
        return updatingIds;
    }

    private static void onStartUpdate(String updateId) {
        GlobalYiersHolder.getYiers(OnServerContentUpdateYier.class)
                .ifPresent(onServerContentUpdateYiers -> onServerContentUpdateYiers.forEach(onServerContentUpdateYier -> {
                    onServerContentUpdateYier.onStartUpdate(updateId);
                }));
    }

    private static void onUpdateComplete(String updateId) {
        GlobalYiersHolder.getYiers(OnServerContentUpdateYier.class)
                .ifPresent(onServerContentUpdateYiers -> onServerContentUpdateYiers.forEach(onServerContentUpdateYier -> {
                    onServerContentUpdateYier.onUpdateComplete(updateId);
                }));
    }

    public static void updateCurrentUserInfo(Context context){
        updateCurrentUserInfo(context, null);
    }

    public static void updateCurrentUserInfo(Context context, SelfInfo selfInfo) {
        if(selfInfo != null){
            fetchAvatarAndStoreUserInfoAndAvatar(context, selfInfo);
        }else {
            UserApiCaller.whoAmI(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CURRENT_USER_INFO) {
                @Override
                public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                    super.ok(data, row, call);
                    data.commonHandleSuccessResult(() -> {
                        SelfInfo selfInfo = data.getData(SelfInfo.class);
                        fetchAvatarAndStoreUserInfoAndAvatar(context, selfInfo);
                    });
                }
            });
        }
    }

    private static void fetchAvatarAndStoreUserInfoAndAvatar(Context context, SelfInfo selfInfo) {
        if (selfInfo.getAvatarHash() != null) {
            UserApiCaller.fetchAvatar(null, selfInfo.getAvatarHash(), new ContentUpdateApiYier<ResponseBody>(OnServerContentUpdateYier.ID_CURRENT_USER_INFO) {
                @Override
                public void ok(ResponseBody data, Response<ResponseBody> row, Call<ResponseBody> call) {
                    super.ok(data, row, call);
                    InputStream contentStream = data.byteStream();
                    String fileName = FileUtil.extractFileNameInHttpHeader(row.headers().get("Content-Disposition"));
                    String extension = FileUtil.getFileExtension(fileName);
                    SelfInfo selfInfo1 = selfInfo.setAvatarExtension(extension);
                    SharedPreferencesAccessor.UserInfoPref.saveCurrentUserInfo(context, selfInfo1);
                    PrivateFilesAccessor.saveAvatar(context, contentStream, selfInfo1.getIchatId(), extension);
                }
            });
        } else {
            SharedPreferencesAccessor.UserInfoPref.saveCurrentUserInfo(context, selfInfo);
        }
    }

}
