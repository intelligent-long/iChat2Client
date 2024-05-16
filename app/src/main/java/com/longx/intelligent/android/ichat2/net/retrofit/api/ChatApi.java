package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.request.SendChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by LONG on 2024/5/15 at 4:07 AM.
 */
public interface ChatApi {
    @POST("chat/message/send")
    CompletableCall<OperationData> sendChatMessage(@Body SendChatMessagePostBody postBody);

    @GET("chat/message/new/all")
    CompletableCall<OperationData> fetchAllNewChatMessages();
}
