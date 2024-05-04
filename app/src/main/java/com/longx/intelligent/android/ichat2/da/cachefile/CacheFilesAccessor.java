package com.longx.intelligent.android.ichat2.da.cachefile;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileAccessor;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.io.File;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/4/30 at 12:34 AM.
 */
public class CacheFilesAccessor {

    public static void cacheAvatarTempFromServer(Context context, String avatarHash, String ichatId, ResultsYier resultsYier){
        UserApiCaller.fetchAvatar(null, avatarHash, new RetrofitApiCaller.CommonYier<ResponseBody>() {
            @Override
            public void ok(ResponseBody data, Response<ResponseBody> row, Call<ResponseBody> call) {
                super.ok(data, row, call);
                InputStream contentStream = data.byteStream();
                String fileName = FileUtil.extractFileNameInHttpHeader(row.headers().get("Content-Disposition"));
                String extension = FileUtil.getFileExtension(fileName);
                String avatarCachePath = DataPaths.Cache.getAvatarCachePath(context, ichatId, extension);
                FileAccessor.save(contentStream, avatarCachePath);
                resultsYier.onResults(getAvatarCache(context, ichatId, extension));
            }
        });
    }

    public static File getAvatarCache(Context context, String ichatId, String extension){
        String avatarCachePath = DataPaths.Cache.getAvatarCachePath(context, ichatId, extension);
        return new File(avatarCachePath);
    }
}
