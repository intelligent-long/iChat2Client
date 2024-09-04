package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.EditBroadcastMediasRecyclerAdapter;
import com.longx.intelligent.android.ichat2.bottomsheet.AddBroadcastMediaBottomSheet;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.databinding.ActivityEditBroadcastBinding;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.util.CollectionUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.KeyboardVisibilityYier;
import com.longx.intelligent.android.ichat2.yier.TextChangedYier;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;

import java.util.ArrayList;
import java.util.List;

public class EditBroadcastActivity extends BaseActivity {
    private ActivityEditBroadcastBinding binding;
    private Broadcast broadcast;
    private EditBroadcastMediasRecyclerAdapter adapter;
    private final SpaceGridDecorationSetter spaceGridDecorationSetter = new SpaceGridDecorationSetter();
    private ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBroadcastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        intentData();
        init();
        registerResultLauncher();
        setupYiers();
        showContent();
    }

    private void intentData() {
        broadcast = getIntent().getParcelableExtra(ExtraKeys.BROADCAST);
    }

    private void init(){
        binding.recyclerViewMedias.setLayoutManager(new GridLayoutManager(this, Constants.EDIT_BROADCAST_MEDIA_COLUMN_COUNT));
    }

    private void registerResultLauncher() {
        returnFromPreviewToSendMediaResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                }
        );
    }

    private void showContent() {
        binding.textInput.setText(broadcast.getText());
        if(!CollectionUtil.isEmpty(broadcast.getBroadcastMedias())){
            binding.recyclerViewMedias.setVisibility(View.VISIBLE);
            spaceGridDecorationSetter.setSpace(this, binding.recyclerViewMedias, Constants.EDIT_BROADCAST_MEDIA_COLUMN_COUNT,
                    Constants.GRID_SPACE_SEND_BROADCAST_DP, false, null, true);
            ArrayList<MediaInfo> mediaInfoList = new ArrayList<>();
            broadcast.getBroadcastMedias().forEach(broadcastMedia -> {
                MediaType mediaType = null;
                if(broadcastMedia.getType() == BroadcastMedia.TYPE_IMAGE){
                    mediaType = MediaType.IMAGE;
                }else if(broadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO){
                    mediaType = MediaType.VIDEO;
                }
                mediaInfoList.add(new MediaInfo(Uri.parse(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId())),
                        null, mediaType, -1, -1, -1,
                        mediaType == MediaType.VIDEO ? (broadcastMedia.getVideoDuration() == null ? -1 : broadcastMedia.getVideoDuration()) : -1,
                        -1, -1, -1, -1));
            });
            adapter = new EditBroadcastMediasRecyclerAdapter(this, returnFromPreviewToSendMediaResultLauncher, mediaInfoList, true);
            binding.recyclerViewMedias.setAdapter(adapter);
        }
    }

    private void setupYiers() {
        binding.editBroadcastButton.setOnClickListener(v -> {

        });
        binding.addMediaFab.setOnClickListener(v -> {
            AddBroadcastMediaBottomSheet bottomSheet = new AddBroadcastMediaBottomSheet(this);
            bottomSheet.show();
        });
        binding.textCounter.setText("0/" + Constants.MAX_BROADCAST_TEXT_LENGTH);
        binding.textInput.addTextChangedListener(new TextChangedYier(){
            private final int textColorNormal = binding.textCounter.getCurrentTextColor();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                binding.textCounter.setText(s.length() + "/" + Constants.MAX_BROADCAST_TEXT_LENGTH);
                if (s.length() > Constants.MAX_BROADCAST_TEXT_LENGTH) {
                    binding.textCounter.setTextColor(getColor(R.color.negative_red));
                } else {
                    binding.textCounter.setTextColor(textColorNormal);
                }
            }
        });
        binding.textInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                if(!CollectionUtil.isEmpty(broadcast.getBroadcastMedias())){
                    binding.recyclerViewMedias.setVisibility(View.GONE);
                }
            }
        });
        binding.textInput.setOnClickListener(v -> {
            if(!CollectionUtil.isEmpty(broadcast.getBroadcastMedias())){
                binding.recyclerViewMedias.setVisibility(View.GONE);
            }

        });
        new KeyboardVisibilityYier(this).setYier(new KeyboardVisibilityYier.Yier() {

            @Override
            public void onKeyboardOpened() {
            }

            @Override
            public void onKeyboardClosed() {
                if(!CollectionUtil.isEmpty(broadcast.getBroadcastMedias())){
                    binding.recyclerViewMedias.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}