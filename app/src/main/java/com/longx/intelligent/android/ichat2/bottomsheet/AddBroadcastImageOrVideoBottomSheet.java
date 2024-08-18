package com.longx.intelligent.android.ichat2.bottomsheet;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.databinding.BottomSheetAddBroadcastImageOrVideo2Binding;

/**
 * Created by LONG on 2024/8/4 at 上午4:40.
 */
public class AddBroadcastImageOrVideoBottomSheet extends AbstractBottomSheet {
    private BottomSheetAddBroadcastImageOrVideo2Binding binding;
    private View.OnClickListener onClickAddMediaYier;
    private View.OnClickListener onClickTakePhotoYier;
    private View.OnClickListener onClickRecordVideoYier;

    public AddBroadcastImageOrVideoBottomSheet(AppCompatActivity activity) {
        super(activity);
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddBroadcastImageOrVideo2Binding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupListeners();
    }

    private void setupListeners() {
        binding.addMedia.setOnClickListener(v -> {
            dismiss();
            if(onClickAddMediaYier != null) onClickAddMediaYier.onClick(v);
        });
        binding.takePhoto.setOnClickListener(v -> {
            dismiss();
            if(onClickTakePhotoYier != null) onClickTakePhotoYier.onClick(v);
        });
        binding.recordVideo.setOnClickListener(v -> {
            dismiss();
            if(onClickRecordVideoYier != null) onClickRecordVideoYier.onClick(v);
        });
    }

    public void setOnClickAddMediaYier(View.OnClickListener onClickAddMediaYier) {
        this.onClickAddMediaYier = onClickAddMediaYier;
    }

    public void setOnClickTakePhotoYier(View.OnClickListener onClickTakePhotoYier) {
        this.onClickTakePhotoYier = onClickTakePhotoYier;
    }

    public void setOnClickRecordVideoYier(View.OnClickListener onClickRecordVideoYier) {
        this.onClickRecordVideoYier = onClickRecordVideoYier;
    }
}
