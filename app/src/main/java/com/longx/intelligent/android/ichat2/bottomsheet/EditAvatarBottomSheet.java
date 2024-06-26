package com.longx.intelligent.android.ichat2.bottomsheet;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetEditAvatarBinding;

/**
 * Created by LONG on 2024/4/16 at 11:40 AM.
 */
public class EditAvatarBottomSheet extends AbstractBottomSheet {
    private BottomSheetEditAvatarBinding bottomSheetEditAvatarBinding;
    private final View.OnClickListener onClickSetAvatarYier;
    private final View.OnClickListener onClickRemoveAvatarYier;

    public EditAvatarBottomSheet(AppCompatActivity activity, View.OnClickListener onClickSetAvatarYier, View.OnClickListener onClickRemoveAvatarYier) {
        super(activity);
        this.onClickSetAvatarYier = onClickSetAvatarYier;
        this.onClickRemoveAvatarYier = onClickRemoveAvatarYier;
        create();
    }

    @Override
    protected void onCreate() {
        bottomSheetEditAvatarBinding = BottomSheetEditAvatarBinding.inflate(getActivity().getLayoutInflater());
        Self currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(getActivity());
        if(currentUserInfo.getAvatar() == null || currentUserInfo.getAvatar().getHash() == null){
            bottomSheetEditAvatarBinding.removeAvatar.setVisibility(View.GONE);
        }
        setContentView(bottomSheetEditAvatarBinding.getRoot());
        setupListeners();
    }

    private void setupListeners() {
        bottomSheetEditAvatarBinding.setAvatar.setOnClickListener(v -> {
            onClickSetAvatarYier.onClick(v);
            dismiss();
        });
        bottomSheetEditAvatarBinding.removeAvatar.setOnClickListener(v -> {
            onClickRemoveAvatarYier.onClick(v);
            dismiss();
        });
    }
}
