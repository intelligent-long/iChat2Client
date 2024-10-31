package com.longx.intelligent.android.ichat2.net.stomp;

import android.content.Context;

import com.longx.intelligent.android.ichat2.behaviorcomponents.GlobalBehaviors;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ServerSetting;
import com.longx.intelligent.android.ichat2.net.CookieJar;
import com.longx.intelligent.android.ichat2.service.ServerMessageService;
import com.longx.intelligent.android.ichat2.service.ServerMessageServiceNotRunningNotifier;
import com.longx.intelligent.android.ichat2.util.AppUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

import java.util.concurrent.TimeUnit;

import cn.zhxu.okhttps.HTTP;
import cn.zhxu.okhttps.WHttpTask;
import cn.zhxu.stomp.Stomp;

/**
 * Created by LONG on 2024/4/4 at 8:54 PM.
 */
public class ServerMessageServiceStomp {
    private static final int HEARTBEAT_INTERVAL_SECONDS = 10 * 60;
    private static Stomp stomp;

    public static synchronized void launchWith(ServerMessageService serverMessageService){
        disconnect();
        connect(serverMessageService);
        subscribe(serverMessageService.getContext());
    }

    private static synchronized void connect(ServerMessageService serverMessageService) {
        ServerMessageServiceNotRunningNotifier.recordAndNotify(serverMessageService.getContext(), System.currentTimeMillis());
        ServerSetting serverSetting = SharedPreferencesAccessor.ServerSettingPref.getServerSetting(serverMessageService.getContext());
        String wsUrl = "ws://" + serverSetting.getHost() + ":" + serverSetting.getPort() + WebsocketConsts.WEBSOCKET_ENDPOINT;
        WHttpTask wHttpTask = HTTP.builder()
                .config(builder1 -> builder1.cookieJar(CookieJar.get()).pingInterval(10, TimeUnit.SECONDS))
                .build()
                .webSocket(wsUrl)
                .addHeader("Client-Version", String.valueOf(AppUtil.getVersionCode(serverMessageService.getContext())))
                .setMaxClosingSecs(1)
                .pingSupplier(() -> {
                    ServerMessageServiceNotRunningNotifier.recordAndNotify(serverMessageService.getContext(), System.currentTimeMillis());
                    return null;
                })
                .heatbeat(HEARTBEAT_INTERVAL_SECONDS, 0);
        stomp = Stomp.over(wHttpTask)
                .setOnConnected(stomp -> {
                    ErrorLogger.log(ServerMessageServiceStomp.class, "Stomp connected");
                    serverMessageService.onGetOnline();
                })
                .setOnDisconnected(close -> {
                    ErrorLogger.log(ServerMessageServiceStomp.class, "Stomp disconnected > Code: " + close.getCode() + ", Reason: " + close.getReason());
                    if (close.getCode() == 1000 && close.getReason().equals("disconnect by user")) {
                        serverMessageService.cancelBackingOnline();
                        return;
                    }
                    switch (close.getCode()) {
                        //主动下线
                        case WebsocketConsts.CLOSE_CODE_SERVER_ACTIVE_CLOSE:
                            serverMessageService.cancelBackingOnline();
                            GlobalBehaviors.onOtherOnline(serverMessageService.getContext());
                            break;
                        //有新版本
                        case WebsocketConsts.CLOSE_CODE_CLOSE_FOR_CLIENT_UPDATE:
                            serverMessageService.cancelBackingOnline();
                            //TODO

                            break;
                        //版本过高
                        case WebsocketConsts.CLOSE_CODE_CLOSE_FOR_CLIENT_VERSION_HIGHER:
                            serverMessageService.cancelBackingOnline();
                            //TODO

                            break;
                        default:
                            serverMessageService.onGetDisconnected();
                    }
                })
                .setOnError(message -> {
                    ErrorLogger.log(ServerMessageServiceStomp.class, "Stomp error");
                    serverMessageService.onGetDisconnected();
                })
                .setOnException(throwable -> {
                    ErrorLogger.log(ServerMessageServiceStomp.class, "Stomp exception");
                    //防止重新连接上时触发 OnConnected 监听后, 最后一个心跳超时导致错误地触发 ServerEventListener 的 onGetDisconnected 监听
                    if (!stomp.isConnected()) {
                        serverMessageService.onGetDisconnected();
                    }
                })
                .connect();
    }

    private static synchronized void subscribe(Context context) {
        stomp.subscribe(StompDestinations.USER_PROFILE_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateCurrentUserProfile(context);
        });
        stomp.subscribe(StompDestinations.CHANNEL_ADDITIONS_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateChannelAdditionActivities(context);
        });
        stomp.subscribe(StompDestinations.CHANNEL_ADDITIONS_NOT_VIEW_COUNT_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateChannelAdditionsNotViewCount(context);
        });
        stomp.subscribe(StompDestinations.CHANNELS_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateChannels(context);
        });
        stomp.subscribe(StompDestinations.CHAT_MESSAGES_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateChatMessages(context);
        });
        stomp.subscribe(StompDestinations.CHANNEL_TAGS_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateChannelTags(context);
        });
        stomp.subscribe(StompDestinations.BROADCASTS_NEWS_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateBroadcastsNews(context, message.getPayload());
        });
        stomp.subscribe(StompDestinations.RECENT_BROADCAST_MEDIAS_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateRecentBroadcastMedias(context, message.getPayload());
        });
        stomp.subscribe(StompDestinations.BROADCASTS_LIKES_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateNewBroadcastLikesCount(context);
        });
        stomp.subscribe(StompDestinations.BROADCASTS_COMMENTS_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateNewBroadcastCommentsCount(context);
        });
        stomp.subscribe(StompDestinations.BROADCASTS_REPLIES_UPDATE, null, message -> {
            ServerMessageServiceStompActions.updateNewBroadcastRepliesCount(context);
        });
    }

    public static synchronized boolean isConnected(){
        return stomp != null && stomp.isConnected();
    }

    public static synchronized void disconnect(){
        if(stomp != null) {
            //防止下线后, 最后一个心跳超时导致触发 OnException 监听, 从而不断重试
            stomp.setOnDisconnected(null);
            stomp.setOnConnected(null);
            stomp.setOnException(null);
            stomp.setOnError(null);
            if (stomp.isConnected()) {
                stomp.disconnect();
            }
        }
    }
}
