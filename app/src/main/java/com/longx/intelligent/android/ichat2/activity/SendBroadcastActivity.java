package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.SendBroadcastMediasRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.bottomsheet.AddBroadcastImageOrVideoBottomSheet;
import com.longx.intelligent.android.ichat2.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivitySendBroadcastBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ResourceUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.BroadcastReloadYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class SendBroadcastActivity extends BaseActivity {
    private ActivitySendBroadcastBinding binding;
    private ActivityResultLauncher<Intent> addImageResultLauncher;
    private final List<Uri> imageUriList = new ArrayList<>();
    private static final int MEDIA_COLUMN_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendBroadcastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        initResultLauncher();
        setupYiers();
    }

    private void initResultLauncher() {
        addImageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        Parcelable[] parcelableArrayExtra = Objects.requireNonNull(data.getParcelableArrayExtra(ExtraKeys.URIS));
                        List<Uri> uriList = Utils.parseParcelableArray(parcelableArrayExtra);
                        if(imageUriList.size() + uriList.size() > Constants.MAX_BROADCAST_IMAGE_COUNT){
                            MessageDisplayer.autoShow(this, "最多附带 " + Constants.MAX_BROADCAST_IMAGE_COUNT + " 张图片", MessageDisplayer.Duration.LONG);
                        }else {
                            onImagesChosen(uriList);
                        }
                    }
                }
        );
    }

    private void setupYiers() {
        binding.sendBroadcastButton.setOnClickListener(v -> {
            String broadcastText = UiUtil.getEditTextString(binding.textInput);
            if(broadcastText == null || broadcastText.isEmpty()) {
                MessageDisplayer.autoShow(this, "没有内容", MessageDisplayer.Duration.SHORT);
                return;
            };
            SendBroadcastPostBody postBody = new SendBroadcastPostBody(broadcastText);
            BroadcastApiCaller.sendBroadcast(this, this,  postBody, null, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(SendBroadcastActivity.this, new int[]{}, () -> {
                        GlobalYiersHolder.getYiers(BroadcastReloadYier.class).ifPresent(broadcastReloadYiers -> {
                            broadcastReloadYiers.forEach(BroadcastReloadYier::onBroadcastReload);
                        });
                        MessageDisplayer.showToast(getContext(), "广播已发送", Toast.LENGTH_SHORT);
                        finish();
                    });
                }
            });
        });
        binding.addImageOrVideoFab.setOnClickListener(v -> {
            AddBroadcastImageOrVideoBottomSheet bottomSheet = new AddBroadcastImageOrVideoBottomSheet(this);
            bottomSheet.setOnClickAddImageYier(v1 -> {
                Intent intent = new Intent(this, ChooseImagesActivity.class);
                intent.putExtra(ExtraKeys.TOOLBAR_TITLE, "选择图片");
                intent.putExtra(ExtraKeys.MENU_TITLE, "完成");
                intent.putExtra(ExtraKeys.RES_ID, R.drawable.check_24px);
                intent.putExtra(ExtraKeys.URIS, imageUriList.toArray(new Uri[0]));
                addImageResultLauncher.launch(intent);
            });
            bottomSheet.show();
        });
    }

    private void onImagesChosen(List<Uri> uriList) {
        List<Uri> toAdds = new ArrayList<>();
        uriList.forEach(uri -> {
            if(!imageUriList.contains(uri)) toAdds.add(uri);
        });
        List<Uri> toRemoves = new ArrayList<>();
        imageUriList.forEach(uri -> {
            if(!uriList.contains(uri)) toRemoves.add(uri);
        });
        imageUriList.addAll(toAdds);
        imageUriList.removeAll(toRemoves);
        showImages();
    }

    private void showImages(){
        if(imageUriList.isEmpty()){
            binding.recyclerViewMedias.setVisibility(View.GONE);
        }else {
            binding.recyclerViewMedias.setVisibility(View.VISIBLE);
            binding.recyclerViewMedias.setLayoutManager(new GridLayoutManager(this, MEDIA_COLUMN_COUNT));
            new SpaceGridDecorationSetter().setSpace(this, binding.recyclerViewMedias, MEDIA_COLUMN_COUNT, Constants.GRID_SPACE_SEND_BROADCAST_DP, false, null, true);
            SendBroadcastMediasRecyclerAdapter adapter = new SendBroadcastMediasRecyclerAdapter(this, imageUriList);
            binding.recyclerViewMedias.setAdapter(adapter);
        }
    }
}