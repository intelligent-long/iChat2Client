package com.longx.intelligent.android.ichat2.activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChatMediaPagerAdapter;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatMediaBinding;
import com.longx.intelligent.android.ichat2.dialog.OperationDialog;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;

import java.io.IOException;
import java.util.List;

public class ChatMediaActivity extends BaseActivity implements RecyclerItemYiers.OnRecyclerItemActionYier, RecyclerItemYiers.OnRecyclerItemClickYier {
    private ActivityChatMediaBinding binding;
    private List<ChatMessage> chatMessages;
    private int position;
    private ChatMediaPagerAdapter adapter;
    private boolean pureContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.checkAndExtendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        getIntentData();
        binding.appBar.bringToFront();
        setupYiers();
        showContent();
    }

    private void getIntentData() {
        chatMessages = getIntent().getParcelableArrayListExtra(ExtraKeys.CHAT_MESSAGES);
        position = getIntent().getIntExtra(ExtraKeys.POSITION, 0);
        binding.toolbar.setTitle((position + 1) + " / " + chatMessages.size());
    }

    private void showContent() {
        adapter = new ChatMediaPagerAdapter(this, binding.viewPager, chatMessages);
        adapter.setOnRecyclerItemActionYier(this);
        adapter.setOnRecyclerItemClickYier(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.setCurrentItem(position, false);
        setupPageSwitchTransformer();
    }

    private void setupYiers() {
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            int position = -1;
            boolean right;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(this.position != position){
                    right = this.position > position;
                    this.position = position;
                    binding.toolbar.setTitle((position + 1) + " / " + chatMessages.size());
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(positionOffset == 0){
                    binding.viewPager.post(() -> adapter.notifyItemChanged(right ? this.position + 1 : this.position - 1));
                }
                adapter.checkAndPlayAtPosition(position);
            }
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.save){
                int currentItem = binding.viewPager.getCurrentItem();
                ChatMessage chatMessage = chatMessages.get(currentItem);
                switch (chatMessage.getType()){
                    case ChatMessage.TYPE_IMAGE:{
                        new Thread(() -> {
                            OperationDialog operationDialog = new OperationDialog(this);
                            operationDialog.show();
                            try {
                                PublicFileAccessor.ChatImage.save(chatMessage.getImageFilePath(), chatMessage);
                                operationDialog.dismiss();
                                MessageDisplayer.autoShow(this, "已保存", MessageDisplayer.Duration.SHORT);
                            }catch (IOException e){
                                ErrorLogger.log(e);
                                MessageDisplayer.autoShow(this, "保存失败", MessageDisplayer.Duration.SHORT);
                            }
                        }).start();
                        break;
                    }
                    case ChatMessage.TYPE_VIDEO:{

                    }
                }
            }
            return true;
        });
    }

    private void setupPageSwitchTransformer() {
        binding.viewPager.setPageTransformer((page, position) -> {
            float margin = getResources().getDimension(R.dimen.media_page_margin);
            float offset = getResources().getDimension(R.dimen.media_page_offset);
            if (binding.viewPager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                float scaleFactor = 1 - Math.abs(position) * 0.0f;
                float translationX = position * (2 * margin + offset);
                if (position < -1) {
                    page.setTranslationX(-translationX);
                } else if (position <= 1) {
                    float scale = Math.max(scaleFactor, 0.75f);
                    page.setScaleX(scale);
                    page.setScaleY(scale);
                    page.setTranslationX(translationX);
                } else {
                    page.setTranslationX(translationX);
                }
            }
        });
    }

    private void setPureContent(boolean pureContent) {
        if(pureContent){
            binding.appBar.setVisibility(View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(this, false);
            this.pureContent = true;
        }else {
            binding.appBar.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(this, true);
            this.pureContent = false;
        }
    }

    @Override
    public void onRecyclerItemAction(int position, Object... o) {
        setPureContent(true);
    }

    @Override
    public void onRecyclerItemClick(int position, View view) {
        setPureContent(!pureContent);
    }

    public boolean isPureContent() {
        return pureContent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter.getChatMessages().get(position).getType() == ChatMessage.TYPE_VIDEO){
            adapter.startPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(adapter.getChatMessages().get(position).getType() == ChatMessage.TYPE_VIDEO) {
            adapter.pausePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter.getChatMessages().get(position).getType() == ChatMessage.TYPE_VIDEO) {
            adapter.releasePlayer();
        }
    }
}