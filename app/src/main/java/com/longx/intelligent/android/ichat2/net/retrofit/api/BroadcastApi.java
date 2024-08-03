package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.xcheng.retrofit.CompletableCall;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by LONG on 2024/7/28 at 上午2:47.
 */
public interface BroadcastApi {

    @POST("broadcast/send")
    @Multipart
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationStatus> sendBroadcast(@Part("body") RequestBody postBody, @Part List<MultipartBody.Part> images);

    @GET("broadcast/limit")
    CompletableCall<PaginatedOperationData<Broadcast>> fetchBroadcastsLimit(@Query("last_broadcast_id") String lastBroadcastId, @Query("ps") int ps);
}
