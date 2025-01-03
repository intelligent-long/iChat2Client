package com.longx.intelligent.android.ichat2.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.ichat2.databinding.ActivityAvatarBinding;
import com.longx.intelligent.android.ichat2.dialog.OperatingDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

import java.io.IOException;
import java.util.Objects;

public class AvatarActivity extends BaseActivity {
    private ActivityAvatarBinding binding;
    private String ichatId;
    private String avatarExtension;
    private String avatarHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        ichatId = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.ICHAT_ID));
        avatarHash = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.AVATAR_HASH));
        avatarExtension = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.AVATAR_EXTENSION));
        setupToolbar();
        showAvatar();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.save_avatar){
                saveAvatar();
            }
            return true;
        });
    }

    private void saveAvatar() {
        new Thread(() -> {
            OperatingDialog operatingDialog = new OperatingDialog(this);
            operatingDialog.create().show();
            try {
                PublicFileAccessor.User.saveAvatar(this, ichatId, avatarHash, avatarExtension);
                operatingDialog.dismiss();
                MessageDisplayer.autoShow(this, "已保存", MessageDisplayer.Duration.SHORT);
            } catch (IOException | InterruptedException e) {
                ErrorLogger.log(e);
                MessageDisplayer.autoShow(this, "保存失败", MessageDisplayer.Duration.SHORT);
            }
        }).start();
    }

    private void showAvatar() {
        binding.loadingIndicator.hide();
        binding.loadingIndicator.show();

        GlideBehaviours.loadToBitmap(getApplicationContext(), NetDataUrls.getAvatarUrl(this, avatarHash),
                new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.loadingIndicator.hide();
                        binding.loadingIndicator.setVisibility(View.GONE);
                        binding.avatarView.setVisibility(View.VISIBLE);
                        binding.avatarView.setImage(ImageSource.bitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                }, true);
    }
}