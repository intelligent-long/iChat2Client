package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.request.ChangeUserProfileVisibilityPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by LONG on 2024/6/7 at 6:00 PM.
 */
public interface PrivacyApi {

    @POST("privacy/user/profile_visibility/change")
    CompletableCall<OperationStatus> changeUserProfileVisibility(@Body ChangeUserProfileVisibilityPostBody postBody);
}