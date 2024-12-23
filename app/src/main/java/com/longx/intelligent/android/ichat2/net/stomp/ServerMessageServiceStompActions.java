package com.longx.intelligent.android.ichat2.net.stomp;

import android.content.Context;

import com.longx.intelligent.android.ichat2.Application;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.BroadcastInteractionsActivity;
import com.longx.intelligent.android.ichat2.activity.ChatActivity;
import com.longx.intelligent.android.ichat2.activity.MainActivity;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.activity.helper.HoldableActivity;
import com.longx.intelligent.android.ichat2.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionNotViewedCount;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.notification.Notifications;
import com.longx.intelligent.android.ichat2.yier.BroadcastFetchNewsYier;
import com.longx.intelligent.android.ichat2.yier.ChannelAdditionActivitiesUpdateYier;
import com.longx.intelligent.android.ichat2.yier.ChatMessageUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.ichat2.yier.RecentBroadcastMediasUpdateYier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/4/1 at 5:03 AM.
 */
//处理数据更新，以及附带其他操作
public class ServerMessageServiceStompActions {

    public static void updateCurrentUserProfile(Context context){
        ContentUpdater.updateCurrentUserProfile(context);
    }

    public static void updateChannelAdditionActivities(Context context){
        GlobalYiersHolder.getYiers(ChannelAdditionActivitiesUpdateYier.class).ifPresent(channelAdditionActivitiesUpdateYiers -> {
            channelAdditionActivitiesUpdateYiers.forEach(ChannelAdditionActivitiesUpdateYier::onChannelAdditionActivitiesUpdate);
        });
    }

    public static void updateChannelAdditionsNotViewCount(Context context) {
        ContentUpdater.updateChannelAdditionNotViewCount(context, results -> {
            ChannelAdditionNotViewedCount notViewedCount = (ChannelAdditionNotViewedCount) results[0];
            if(notViewedCount != null) {
                if (!Application.foreground) {
                    Notifications.notifyChannelAdditionActivity(context, notViewedCount.getNotificationRequest(), notViewedCount.getNotificationRespond());
                } else {
                    if (!(ActivityOperator.getTopActivity() instanceof MainActivity)) {
                        Notifications.notifyChannelAdditionActivity(context, notViewedCount.getNotificationRequest(), notViewedCount.getNotificationRespond());
                    }
                }
            }
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.CHANNEL_ADDITION_ACTIVITIES);
                });
            });
        });
    }

    public static void updateChannels(Context context){
        ContentUpdater.updateChannels(context, results -> {
            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
            });
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.MESSAGES);
                });
            });
            Notifications.notifyPendingNotifications(Notifications.PendingNotificationId.CHAT_MESSAGE);
        });
    }

    public static void updateChatMessages(Context context){
        ContentUpdater.updateChatMessages(context, results -> {
            List<ChatMessage> chatMessages = (List<ChatMessage>) results[0];
            List<ChatMessage> toUnsendChatMessages = (List<ChatMessage>) results[1];
            chatMessages.forEach(chatMessage -> {
                if (!Application.foreground) {
                    Notifications.notifyChatMessage(context, chatMessage);
                } else {
                    HoldableActivity topActivity = ActivityOperator.getTopActivity();
                    if (!(topActivity instanceof MainActivity)) {
                        if (!(topActivity instanceof ChatActivity && ((ChatActivity) topActivity).getChannel().getIchatId().equals(chatMessage.getOther(context)))) {
                            Notifications.notifyChatMessage(context, chatMessage);
                        }
                    }
                }
            });
            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
            });
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.MESSAGES);
                });
            });
            GlobalYiersHolder.getYiers(ChatMessageUpdateYier.class).ifPresent(chatMessageUpdateYiers -> {
                chatMessageUpdateYiers.forEach(chatMessageUpdateYier -> {
                    chatMessageUpdateYier.onNewChatMessage(chatMessages);
                    chatMessageUpdateYier.onUnsendChatMessage(toUnsendChatMessages);
                });
            });
        });
    }

    public static void updateChannelTags(Context context){
        ContentUpdater.updateChannelTags(context, results -> {
        });
    }

    public static void updateBroadcastsNews(Context context, String ichatId){
        GlobalYiersHolder.getYiers(BroadcastFetchNewsYier.class).ifPresent(broadcastFetchNewsYiers -> {
            broadcastFetchNewsYiers.forEach(broadcastFetchNewsYier -> broadcastFetchNewsYier.fetchNews(ichatId));
        });
    }

    public static void updateRecentBroadcastMedias(Context context, String ichatId){
        if(ichatId == null || ichatId.isEmpty()){
            List<String> channelIds = new ArrayList<>();
            channelIds.add(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(context).getIchatId());
            ChannelDatabaseManager.getInstance().findAllAssociations().forEach(channelAssociation -> {
                channelIds.add(channelAssociation.getChannelIchatId());
            });
            channelIds.forEach(channelId -> {
                ContentUpdater.updateRecentBroadcastMedias(context, channelId, results -> {
                    GlobalYiersHolder.getYiers(RecentBroadcastMediasUpdateYier.class).ifPresent(recentBroadcastMediasUpdateYiers -> {
                        recentBroadcastMediasUpdateYiers.forEach(recentBroadcastMediasUpdateYier -> {
                            recentBroadcastMediasUpdateYier.onRecentBroadcastMediasUpdate(channelId);
                        });
                    });
                });
            });
        }else {
            ContentUpdater.updateRecentBroadcastMedias(context, ichatId, results -> {
                GlobalYiersHolder.getYiers(RecentBroadcastMediasUpdateYier.class).ifPresent(recentBroadcastMediasUpdateYiers -> {
                    recentBroadcastMediasUpdateYiers.forEach(recentBroadcastMediasUpdateYier -> {
                        recentBroadcastMediasUpdateYier.onRecentBroadcastMediasUpdate(ichatId);
                    });
                });
            });
        }
    }

    public static void updateNewBroadcastLikesCount(Context context){
        ContentUpdater.updateNewBroadcastLikesCount(context, results -> {
            int newsCount = (int) results[0];
            if(newsCount > 0 && ! (ActivityOperator.getTopActivity() instanceof BroadcastInteractionsActivity)){
                Notifications.notifyBroadcastInteractionNewsContent(context, "广播互动", newsCount + " 个新的广播喜欢", R.drawable.favorite_fill_24px);
            }
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.BROADCAST_LIKES);
                });
            });
        });
    }

    public static void updateNewBroadcastCommentsCount(Context context){
        ContentUpdater.updateNewBroadcastCommentsCount(context, results -> {
            int newsCount = (int) results[0];
            if(newsCount > 0 && ! (ActivityOperator.getTopActivity() instanceof BroadcastInteractionsActivity)){
                Notifications.notifyBroadcastInteractionNewsContent(context, "广播互动", newsCount + " 个新的广播评论", R.drawable.mode_comment_fill_24px);
            }
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.BROADCAST_COMMENTS);
                });
            });
        });
    }

    public static void updateNewBroadcastRepliesCount(Context context){
        ContentUpdater.updateNewBroadcastRepliesCount(context, results -> {
            int newsCount = (int) results[0];
            if(newsCount > 0 && ! (ActivityOperator.getTopActivity() instanceof BroadcastInteractionsActivity)){
                Notifications.notifyBroadcastInteractionNewsContent(context, "广播互动", newsCount + " 个新的广播回复", R.drawable.mode_comment_fill_24px);
            }
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.BROADCAST_REPLIES);
                });
            });
        });
    }

}
