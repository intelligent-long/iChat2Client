package com.longx.intelligent.android.ichat2.popupwindow;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.ForwardMessageActivity;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.PopupWindowChatMessageActionsBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.CopyTextDialog;
import com.longx.intelligent.android.ichat2.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;

/**
 * Created by LONG on 2024/5/17 at 9:23 AM.
 */
public class ChatMessageActionsPopupWindow {
    private final AppCompatActivity activity;
    private final ChatMessage chatMessage;
    private final PopupWindow popupWindow;
    private final PopupWindowChatMessageActionsBinding binding;
    private final int HEIGHT_DP = 86;
    private OnDeletedYier onDeletedYier;

    public ChatMessageActionsPopupWindow(AppCompatActivity activity, ChatMessage chatMessage) {
        this.activity = activity;
        this.chatMessage = chatMessage;
        binding = PopupWindowChatMessageActionsBinding.inflate(activity.getLayoutInflater());
        switch (chatMessage.getType()){
            case ChatMessage.TYPE_IMAGE:
            case ChatMessage.TYPE_FILE:
            case ChatMessage.TYPE_VIDEO:
            case ChatMessage.TYPE_VOICE: {
                binding.clickViewCopy.setVisibility(View.GONE);
                break;
            }
        }
        if(!chatMessage.getFrom().equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity).getIchatId())){
            binding.clickViewDelete.setVisibility(View.GONE);
            binding.clickViewRevoke.setVisibility(View.GONE);
        }
        popupWindow = new PopupWindow(binding.getRoot(),  ViewGroup.LayoutParams.WRAP_CONTENT,  UiUtil.dpToPx(activity, HEIGHT_DP), true);
        setupYiers();
    }

    private void setupYiers() {
        binding.clickViewTime.setOnClickListener(v -> {
            popupWindow.dismiss();
            String timeText = TimeUtil.formatDetailedRelativeTime(chatMessage.getTime());
            new CustomViewMessageDialog(activity, timeText).create().show();
        });
        binding.clickViewCopy.setOnClickListener(v -> {
            popupWindow.dismiss();
            new CopyTextDialog(activity, chatMessage.getText()).create().show();
        });
        binding.clickViewDelete.setOnClickListener(v -> {
            new ConfirmDialog(activity, "是否继续？")
                    .setNegativeButton(null)
                    .setPositiveButton((dialog, which) -> {
                        popupWindow.dismiss();
                        String other = chatMessage.getOther(activity);
                        ChatMessageDatabaseManager databaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(activity, other);
                        databaseManager.delete(chatMessage.getUuid());
                        switch (chatMessage.getType()){
                            case ChatMessage.TYPE_IMAGE:{
                                PrivateFilesAccessor.ChatImage.delete(chatMessage);
                                break;
                            }
                            case ChatMessage.TYPE_FILE:{
                                PrivateFilesAccessor.ChatFile.delete(chatMessage);
                                break;
                            }
                            case ChatMessage.TYPE_VIDEO:{
                                PrivateFilesAccessor.ChatVideo.delete(chatMessage);
                                break;
                            }
                            case ChatMessage.TYPE_VOICE:{
                                PrivateFilesAccessor.ChatVoice.delete(chatMessage);
                                break;
                            }
                        }
                        onDeletedYier.onDeleted();
                    })
                    .create().show();
        });
        binding.clickViewForward.setOnClickListener(v -> {
            popupWindow.dismiss();
            Intent intent = new Intent(activity, ForwardMessageActivity.class);
            intent.putExtra(ExtraKeys.CHAT_MESSAGE, chatMessage);
            activity.startActivity(intent);
        });
    }

    public void show(View anchorView, boolean right) {
        int yOffset = -anchorView.getHeight() - UiUtil.dpToPx(activity, HEIGHT_DP + 5);
        int xOffset;
        if(right){
            xOffset = 0;
        }else {
            xOffset = -UiUtil.dpToPx(activity, 210);
        }
        popupWindow.showAsDropDown(anchorView, xOffset, yOffset);
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void setOnDeletedYier(OnDeletedYier onDeletedYier) {
        this.onDeletedYier = onDeletedYier;
    }

    public interface OnDeletedYier{
        void onDeleted();
    }
}
