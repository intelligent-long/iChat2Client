package com.longx.intelligent.android.ichat2.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.FileAccessHelper;
import com.longx.intelligent.android.ichat2.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.ichat2.databinding.ActivityTakeAndSendPhotoBinding;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

import java.io.File;
import java.io.IOException;

public class TakeAndSendPhotoActivity extends BaseActivity {
    private ActivityTakeAndSendPhotoBinding binding;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private Uri photoUri;
    private File photoFile;
    private boolean purePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakeAndSendPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        binding.appBar.bringToFront();
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        photoFile = FileAccessHelper.detectAndRenameFile(photoFile);
                        if(photoFile == null){
                            MessageDisplayer.autoShow(this, "创建文件失败", MessageDisplayer.Duration.LONG);
                        }else {
                            getPhotoUri(photoFile);
                            showContent();
                            setupYiers();
                        }
                    }else {
                        finish();
                    }
                }
        );
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createPhotoFile();
                getPhotoUri(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureLauncher.launch(takePictureIntent);
            } catch (IOException e) {
                ErrorLogger.log(getClass(), e);
                MessageDisplayer.autoShow(this, "创建文件失败", MessageDisplayer.Duration.LONG);
            }
        }
    }

    private void getPhotoUri(File file) {
        photoUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider",
                file);
    }

    private File createPhotoFile() throws IOException {
        return PublicFileAccessor.CapturedMedia.createPhotoFile();
    }

    private void showContent() {
        setupPhotoView();
        showPhoto();
    }

    private void setupPhotoView() {
        binding.photo.setOnClickListener(v -> {
            setPurePhoto(!purePhoto);
        });
        binding.photo.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                setPurePhoto(true);
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });
    }

    private void showPhoto() {
        binding.photo.setImage(ImageSource.uri(photoUri));
    }

    private void setPurePhoto(boolean purePhoto) {
        if(purePhoto){
            binding.appBar.setVisibility(View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(this, false);
            this.purePhoto = true;
        }else {
            binding.appBar.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(this, true);
            this.purePhoto = false;
        }
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.send){
                Intent intent = new Intent();
                intent.putExtra(ExtraKeys.URIS, new Uri[]{photoUri});
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
        });
    }
}