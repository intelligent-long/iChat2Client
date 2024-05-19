package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChatActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.MainActivity;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemOpenedChatBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.BadgeDisplayer;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import q.rorbin.badgeview.Badge;

/**
 * Created by LONG on 2024/5/16 at 9:45 PM.
 */
public class OpenedChatsRecyclerAdapter extends WrappableRecyclerViewAdapter<OpenedChatsRecyclerAdapter.ViewHolder, OpenedChatsRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList = new ArrayList<>();

    public OpenedChatsRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }
    public static class ItemData{
        private final OpenedChat openedChat;

        public ItemData(OpenedChat openedChat) {
            this.openedChat = openedChat;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemOpenedChatBinding binding;
        private final Badge badge;
        public ViewHolder(Context context, RecyclerItemOpenedChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            badge = BadgeDisplayer.initBadge(context, binding.badgeHost, 0, Gravity.CENTER | Gravity.END);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemOpenedChatBinding binding = RecyclerItemOpenedChatBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(activity, binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        ChatMessage newestChatMessage = itemData.openedChat.getNewestChatMessage();
        Channel channel = itemData.openedChat.getChannel();
        if(channel != null) {
            String avatarHash = channel.getAvatar().getHash();
            if (avatarHash == null) {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatar);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatar);
            }
        }
        if(channel != null) holder.binding.name.setText(channel.getUsername());
        switch (newestChatMessage.getType()){
            case ChatMessage.TYPE_TEXT:
                String newestChatMessageText = newestChatMessage.getText();
                if(newestChatMessageText != null) {
                    if (newestChatMessageText.length() > 25) {
                        newestChatMessageText = newestChatMessageText.substring(0, 25) + "...";
                    }
                }
                holder.binding.newestChatMessage.setText(newestChatMessageText);
        }
        holder.binding.time.setText(TimeUtil.formatSimpleRelativeTime(newestChatMessage.getTime()));
        displayBadges(holder, itemData);
        setupYiers(holder, position);
    }

    private void displayBadges(@NonNull ViewHolder holder, ItemData itemData) {
        int notViewedCount = itemData.openedChat.getNotViewedCount();
        holder.badge.setBadgeNumber(notViewedCount);
    }

    private void setupYiers(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        Channel channel = itemData.openedChat.getChannel();
        holder.binding.clickView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, channel);
            activity.startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeAllItemsAndShow(List<OpenedChat> openedChats){
        itemDataList.clear();
        openedChats.forEach(openedChat -> {
            ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(activity, openedChat.getChannelIchatId());
            List<ChatMessage> limit = chatMessageDatabaseManager.findLimit(0, 1, true);
            if(limit.size() == 1){
                openedChat.setNewestChatMessage(limit.get(0));
            }
            Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(openedChat.getChannelIchatId());
            openedChat.setChannel(channel);
            itemDataList.add(new ItemData(openedChat));
        });
        itemDataList.sort(Comparator.comparing(o -> o.openedChat.getNewestChatMessage().getTime()));
        notifyDataSetChanged();
    }
}
