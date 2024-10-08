package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.request.AcceptAddChannelPostBody;
import com.longx.intelligent.android.ichat2.data.request.AddChannelTagPostBody;
import com.longx.intelligent.android.ichat2.data.request.AddChannelsToTagPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeChannelTagNamePostBody;
import com.longx.intelligent.android.ichat2.data.request.DeleteChannelAssociationPostBody;
import com.longx.intelligent.android.ichat2.data.request.RemoveChannelsOfTagPostBody;
import com.longx.intelligent.android.ichat2.data.request.RequestAddChannelPostBody;
import com.longx.intelligent.android.ichat2.data.request.SetChannelTagsPostBody;
import com.longx.intelligent.android.ichat2.data.request.SetNoteToAssociatedChannelPostBody;
import com.longx.intelligent.android.ichat2.data.request.SortTagsPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by LONG on 2024/4/28 at 1:07 AM.
 */
public interface ChannelApi {

    @GET("channel/find/ichat_id/{ichatId}")
    CompletableCall<OperationData> findChannelByIchatId(@Path("ichatId") String ichatId);

    @GET("channel/find/ichat_id_user/{ichatIdUser}")
    CompletableCall<OperationData> findChannelByIchatIdUser(@Path("ichatIdUser") String ichatIdUser);

    @GET("channel/find/email/{email}")
    CompletableCall<OperationData> findChannelByEmail(@Path("email") String email);

    @POST("channel/add/request")
    CompletableCall<OperationStatus> requestAddChannel(@Body RequestAddChannelPostBody postBody);

    @POST("channel/add/accept")
    CompletableCall<OperationStatus> acceptAddChannel(@Body AcceptAddChannelPostBody postBody);

    @GET("channel/add/activities/not_viewed_count")
    CompletableCall<OperationData> fetchChannelAdditionNotViewCount();

    @GET("channel/add/activity/all")
    CompletableCall<OperationData> fetchAllAdditionActivities();

    @POST("channel/add/activity/{uuid}/view")
    CompletableCall<OperationStatus> viewOneAdditionActivity(@Path("uuid") String uuid);

    @GET("channel/association/all")
    CompletableCall<OperationData> fetchAllAssociations();

    @POST("channel/association/delete")
    CompletableCall<OperationStatus> deleteAssociatedChannel(@Body DeleteChannelAssociationPostBody postBody);

    @POST("channel/association/note/set")
    CompletableCall<OperationStatus> setNoteToAssociatedChannel(@Body SetNoteToAssociatedChannelPostBody postBody);

    @POST("channel/association/note/delete/{channelIchatId}")
    CompletableCall<OperationStatus> deleteNoteOfAssociatedChannel(@Path("channelIchatId") String channelIchatId);

    @POST("channel/association/tag/add")
    CompletableCall<OperationStatus> addTag(@Body AddChannelTagPostBody postBody);

    @GET("channel/association/tag/all")
    CompletableCall<OperationData> fetchAllTags();

    @POST("channel/association/tag/name/change")
    CompletableCall<OperationStatus> changeTagName(@Body ChangeChannelTagNamePostBody postBody);

    @POST("channel/association/tag/sort")
    CompletableCall<OperationStatus> sortChannelTags(@Body SortTagsPostBody postBody);

    @POST("channel/association/tag/channel/add")
    CompletableCall<OperationStatus> addChannelsToTag(@Body AddChannelsToTagPostBody postBody);

    @POST("channel/association/tag/channel/remove")
    CompletableCall<OperationStatus> removeChannelsOfTag(@Body RemoveChannelsOfTagPostBody postBody);

    @POST("channel/association/tag/delete/{tagId}")
    CompletableCall<OperationStatus> deleteChannelTag(@Path("tagId") String tagId);

    @POST("channel/association/tag/channel/set")
    CompletableCall<OperationStatus> setChannelTags(@Body SetChannelTagsPostBody postBody);
}
