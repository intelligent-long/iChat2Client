package com.longx.intelligent.android.ichat2.net.stomp;

/**
 * Created by LONG on 2024/1/23 at 7:54 PM.
 */
public class StompDestinations {
    public static final String PREFIX_TOPIC = "/topic";
    public static final String PREFIX_QUEUE = "/queue";
    public static final String PREFIX_APP = "/app";
    public static final String PREFIX_USER = "/user";

    public static final String USER_PROFILE_UPDATE = "/user" +  PREFIX_QUEUE + "/user_profile_update";
    public static final String CHANNEL_ADDITIONS_UPDATE = "/user" +  PREFIX_QUEUE + "/channel_additions_update";
    public static final String CHANNEL_ADDITIONS_NOT_VIEW_COUNT_UPDATE = "/user" + PREFIX_QUEUE + "/channel_additions_not_view_count_update";
    public static final String CHANNELS_UPDATE = "/user" + PREFIX_QUEUE + "/channels_update";
    public static final String CHAT_MESSAGES_UPDATE = "/user" + PREFIX_QUEUE + "/chat_messages_update";
    public static final String CHANNEL_TAGS_UPDATE = "/user" + PREFIX_QUEUE + "/channel_tags_update";
}
