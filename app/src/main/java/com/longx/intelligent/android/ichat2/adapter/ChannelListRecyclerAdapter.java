package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelBinding;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.io.File;
import java.util.List;

/**
 * Created by LONG on 2024/4/25 at 5:35 PM.
 */
public class ChannelListRecyclerAdapter extends WrappableRecyclerViewAdapter<ChannelListRecyclerAdapter.ViewHolder, ChannelListRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;

    public ChannelListRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
        private Character indexChar;
        private ChannelInfo channelInfo;

        public ItemData(ChannelInfo channelInfo) {
            indexChar = PinyinUtil.getPinyin(channelInfo.getUsername()).toUpperCase().charAt(0);
            if(!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))){
                indexChar = '#';
            }
            this.channelInfo = channelInfo;
        }

        public Character getIndexChar() {
            return indexChar;
        }

        public ChannelInfo getChannelInfo() {
            return channelInfo;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemChannelBinding binding;
        public ViewHolder(RecyclerItemChannelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ChannelListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChannelBinding binding = RecyclerItemChannelBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelListRecyclerAdapter.ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        File avatarFile = itemData.channelInfo.getAvatarFile(activity);
        if (avatarFile == null) {
            GlideApp.with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp.with(activity.getApplicationContext())
                    .load(avatarFile)
                    .into(holder.binding.avatar);
        }
        holder.binding.indexBar.setText(String.valueOf(itemData.indexChar));
        int previousPosition = position - 1;
        if(position == 0){
            holder.binding.indexBar.setVisibility(View.VISIBLE);
        } else {
            ItemData previousItemData = itemDataList.get(previousPosition);
            if (previousItemData.indexChar.equals(itemData.indexChar)) {
                holder.binding.indexBar.setVisibility(View.GONE);
            } else {
                holder.binding.indexBar.setVisibility(View.VISIBLE);
            }
        }
        holder.binding.username.setText(itemData.channelInfo.getUsername());
        holder.binding.clickItem.setOnClickListener(v -> {
            getOnItemClickYier().onItemClick(position, itemData);
        });
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }
}
