package com.longx.intelligent.android.ichat2.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.longx.intelligent.android.ichat2.Application;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.activity.settings.RootSettingsActivity;
import com.longx.intelligent.android.ichat2.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.ichat2.behaviorcomponents.GlobalBehaviors;
import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.ActivityMainBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.fragment.main.BroadcastsFragment;
import com.longx.intelligent.android.ichat2.fragment.main.ChannelsFragment;
import com.longx.intelligent.android.ichat2.fragment.main.MessagesFragment;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.permission.SpecialPermissionOperator;
import com.longx.intelligent.android.ichat2.permission.LinkPermissionOperatorActivity;
import com.longx.intelligent.android.ichat2.permission.PermissionOperator;
import com.longx.intelligent.android.ichat2.permission.PermissionRequirementChecker;
import com.longx.intelligent.android.ichat2.permission.ToRequestPermissions;
import com.longx.intelligent.android.ichat2.permission.ToRequestPermissionsItems;
import com.longx.intelligent.android.ichat2.service.ServerMessageService;
import com.longx.intelligent.android.ichat2.ui.BadgeDisplayer;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.yier.BroadcastFetchNewsYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.ChangeUiYier;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import q.rorbin.badgeview.Badge;

public class MainActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier,
        ServerMessageService.OnOnlineStateChangeYier, View.OnClickListener, NewContentBadgeDisplayYier,
        LinkPermissionOperatorActivity, BroadcastFetchNewsYier {
    private ActivityMainBinding binding;
    private Badge messageNavBadge;
    private Badge channelNavBadge;
    private Badge broadcastNavBadge;
    private MenuItem lastBottomNavSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkAndSwitchToAuth()) return;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupNavigationKeepInMemory();
        setupYier();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.holdYier(this, ServerMessageService.OnOnlineStateChangeYier.class, this);
        GlobalYiersHolder.holdYier(this, NewContentBadgeDisplayYier.class, this, ID.MESSAGES);
        GlobalYiersHolder.holdYier(this, NewContentBadgeDisplayYier.class, this, ID.CHANNEL_ADDITION_ACTIVITIES);
        GlobalYiersHolder.holdYier(this, BroadcastFetchNewsYier.class, this);
        animateNavIconVisibility();
        new Thread(() -> {
            runOnUiThread(() -> {
                startServerMessageService();
                setupUi();
                showNavHeaderInfo();
                requestPermissions();
                GlobalBehaviors.checkAndNotifySoftwareUpdate(this);
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.removeYier(this, ServerMessageService.OnOnlineStateChangeYier.class, this);
        GlobalYiersHolder.removeYier(this, NewContentBadgeDisplayYier.class, this, ID.MESSAGES);
        GlobalYiersHolder.removeYier(this, NewContentBadgeDisplayYier.class, this, ID.CHANNEL_ADDITION_ACTIVITIES);
        GlobalYiersHolder.removeYier(this, BroadcastFetchNewsYier.class, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void requestPermissions(){
        if(SpecialPermissionOperator.isIgnoringBatteryOptimizations(this)){
            SharedPreferencesAccessor.DefaultPref.enableRequestIgnoreBatteryOptimize(this);
        }
        if(SharedPreferencesAccessor.DefaultPref.isRequestIgnoreBatteryOptimizeStateEnabled(this)){
            if(!SpecialPermissionOperator.isIgnoringBatteryOptimizations(this)){
                new ConfirmDialog(this, "取消此应用的电池用量限制，应用的功能才能全部正常运行")
                        .setPositiveButton("确定", (dialog, which) -> {
                            boolean success = SpecialPermissionOperator.requestIgnoreBatteryOptimizations(this);
                            if(!success){
                                MessageDisplayer.autoShow(this, "错误", MessageDisplayer.Duration.LONG);
                            }
                        })
                        .setNegativeButton("下次提醒", (dialog, which) -> {
                        })
                        .setNeutralButton("忽略", (dialog, which) -> {
                            SharedPreferencesAccessor.DefaultPref.disableRequestIgnoreBatteryOptimize(this);
                        })
                        .create().show();
            }
        }
        List<ToRequestPermissions> toRequestPermissionsList = new ArrayList<>();
        if (PermissionRequirementChecker.needNotificationPermission()) {
            if(!PermissionOperator.hasPermissions(this, ToRequestPermissionsItems.showNotification)){
                toRequestPermissionsList.add(ToRequestPermissionsItems.showNotification);
            }
        }
        if (PermissionRequirementChecker.needReadMediaImageAndVideoPermission()) {
            if(!PermissionOperator.hasPermissions(this, ToRequestPermissionsItems.readMediaImagesAndVideos)){
                toRequestPermissionsList.add(ToRequestPermissionsItems.readMediaImagesAndVideos);
            }
        } else if(PermissionRequirementChecker.needExternalStoragePermission()) {
            if (!PermissionOperator.hasPermissions(this, ToRequestPermissionsItems.writeAndReadExternalStorage)) {
                toRequestPermissionsList.add(ToRequestPermissionsItems.writeAndReadExternalStorage);
            }
        }
        new PermissionOperator(this, toRequestPermissionsList,
                new PermissionOperator.ShowCommonMessagePermissionResultCallback(this))
                .startRequestPermissions(this);
        if (!SpecialPermissionOperator.isExternalStorageManager()) {
            MessageDisplayer.showToast(this, "请允许本应用的所有文件访问权限", Toast.LENGTH_LONG);
            boolean success = SpecialPermissionOperator.requestManageExternalStorage(this);
            if(!success){
                MessageDisplayer.autoShow(this, "错误", MessageDisplayer.Duration.LONG);
            }
        }
    }

    private void startServerMessageService() {
        boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(this);
        if(loginState) {
            ServerMessageService.work((Application) getApplicationContext());
        }
    }

    public ActivityMainBinding getViewBinding(){
        return binding;
    }

    private void showNavHeaderInfo() {
        View headerView1 = binding.navigationDrawer1.getHeaderView(0);
        Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        ShapeableImageView avatarImageView = headerView1.findViewById(R.id.avatar);
        if (self.getAvatar() == null || self.getAvatar().getHash() == null) {
            GlideBehaviours.loadToImageView(getApplicationContext(), R.drawable.default_avatar, avatarImageView);
        } else {
            GlideBehaviours.loadToImageView(getApplicationContext(), NetDataUrls.getAvatarUrl(this, self.getAvatar().getHash()), avatarImageView);
        }
        String username = self.getUsername();
        String ichatIdUser = self.getIchatIdUser();
        String email = self.getEmail();
        Integer sex = self.getSex();
        String regionDesc = self.buildRegionDesc();
        ((TextView)headerView1.findViewById(R.id.username)).setText(username);
        ((TextView)headerView1.findViewById(R.id.ichat_id_user)).setText(ichatIdUser);
        ((TextView)headerView1.findViewById(R.id.email)).setText(email);
        RelativeLayout sexLayout = headerView1.findViewById(R.id.layout_sex);
        ImageView sexImageView = headerView1.findViewById(R.id.sex);
        RelativeLayout regionLayout = headerView1.findViewById(R.id.layout_region);
        TextView regionTextView = headerView1.findViewById(R.id.region);
        if(sex == null || (sex != 0 && sex != 1)){
            sexLayout.setVisibility(View.GONE);
        }else {
            sexLayout.setVisibility(View.VISIBLE);
            if(sex == 0){
                sexImageView.setImageResource(R.drawable.female_24px);
            }else {
                sexImageView.setImageResource(R.drawable.male_24px);
            }
        }
        if(regionDesc == null){
            regionLayout.setVisibility(View.GONE);
        }else {
            regionLayout.setVisibility(View.VISIBLE);
            regionTextView.setText(regionDesc);
        }
        headerView1.findViewById(R.id.user_info_page).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, self.getIchatId());
            startActivity(intent);
        });
    }

    private boolean checkAndSwitchToAuth() {
        boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(this);
        if (!loginState) {
            ActivityOperator.switchToAuth(this);
            return true;
        }
        return false;
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        navController.setGraph(R.navigation.main_navigation);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (lastBottomNavSelectedItem != null
                    && lastBottomNavSelectedItem.getItemId() == item.getItemId()
                    && item.getItemId() == R.id.navigation_broadcast
            ) {
                Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (fragment instanceof BroadcastsFragment) {
                    ((BroadcastsFragment) fragment).toStart();
                }
                return true;
            }
            lastBottomNavSelectedItem = item;
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
    }

    private void setupNavigationKeepInMemory() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments.size() == 1 && fragments.get(0) instanceof NavHostFragment){
            transaction.hide(fragments.get(0));
        }
        Fragment messageFragment = getSupportFragmentManager().findFragmentByTag(MessagesFragment.class.getSimpleName());
        if (messageFragment == null) {
            transaction.add(R.id.fragment_container_view, new MessagesFragment(), MessagesFragment.class.getSimpleName());
        }
        transaction.commit();
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            Fragment currentFragment = null;
            FragmentManager fragmentManager = getSupportFragmentManager();
            for (Fragment fragment : fragmentManager.getFragments()) {
                if(fragment instanceof NavHostFragment){
                    transaction1.hide(fragment);
                }else if (fragment != null && fragment.isVisible()) {
                    currentFragment = fragment;
                    break;
                }
            }
            Fragment targetFragment1 = null;
            if (item.getItemId() == R.id.navigation_message) {
                targetFragment1 = getSupportFragmentManager().findFragmentByTag(MessagesFragment.class.getSimpleName());
                if (targetFragment1 == null) {
                    targetFragment1 = new MessagesFragment();
                    transaction1.add(R.id.fragment_container_view, targetFragment1, MessagesFragment.class.getSimpleName());
                }
            } else if (item.getItemId() == R.id.navigation_channel) {
                targetFragment1 = getSupportFragmentManager().findFragmentByTag(ChannelsFragment.class.getSimpleName());
                if (targetFragment1 == null) {
                    targetFragment1 = new ChannelsFragment();
                    transaction1.add(R.id.fragment_container_view, targetFragment1, ChannelsFragment.class.getSimpleName());
                }
            } else if (item.getItemId() == R.id.navigation_broadcast) {
                targetFragment1 = getSupportFragmentManager().findFragmentByTag(BroadcastsFragment.class.getSimpleName());
                if (targetFragment1 == null) {
                    targetFragment1 = new BroadcastsFragment();
                    transaction1.add(R.id.fragment_container_view, targetFragment1, BroadcastsFragment.class.getSimpleName());
                }
            }
            if(currentFragment == targetFragment1 && currentFragment instanceof BroadcastsFragment){
                ((BroadcastsFragment) currentFragment).toStart();
            }else if (currentFragment != targetFragment1) {
                transaction1.setCustomAnimations(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out,
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                );
                if(currentFragment != null) transaction1.hide(currentFragment);
                transaction1.show(targetFragment1);
                transaction1.commit();
            }
            return true;
        });
    }

    protected void animateNavIconVisibility(){
        final Drawable[] navIcon = {ContextCompat.getDrawable(this, R.drawable.menu_24px)};
        if(navIcon[0] == null) return;
        navIcon[0].setAlpha(0);
        ValueAnimator animator = ValueAnimator.ofInt(navIcon[0].getAlpha(), 30);
        animator.setDuration(700);
        animator.addUpdateListener(animation -> {
            navIcon[0].setAlpha((Integer) animation.getAnimatedValue());
            changeMainFragmentsNavIcon(navIcon[0]);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator showAnim) {
                if (navIcon[0] == null) return;
                navIcon[0].setAlpha(navIcon[0].getAlpha());
                ValueAnimator animator = ValueAnimator.ofInt(navIcon[0].getAlpha(), 0);
                animator.setDuration(800);
                animator.addUpdateListener(hideAnim -> {
                    navIcon[0].setAlpha((Integer) hideAnim.getAnimatedValue());
                    changeMainFragmentsNavIcon(navIcon[0]);
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        navIcon[0] = null;
                        changeMainFragmentsNavIcon(null);
                    }
                });
                animator.start();
            }
        });
        animator.start();
    }

    private void changeMainFragmentsNavIcon(Drawable navIcon) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments.size() == 1 && fragments.get(0) instanceof NavHostFragment){
            fragments = fragments.get(0).getChildFragmentManager().getFragments();
        }
        fragments.forEach(fragment -> {
            if (fragment instanceof ChangeUiYier) {
                runOnUiThread(() -> ((ChangeUiYier) fragment).changeUi(ChangeUiYier.ID_HIDE_NAV_ICON, navIcon));
            }
        });
    }

    private void setupYier() {
        binding.navigationDrawer2.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, RootSettingsActivity.class);
                intent.putExtra(ExtraKeys.NEED_RESTORE_INSTANCE_STATE, false);
                startActivity(intent);
            }
            return true;
        });
        View headerView = binding.navigationDrawer1.getHeaderView(0);
        ShapeableImageView avatarImageView = headerView.findViewById(R.id.avatar);
        avatarImageView.setOnClickListener(v -> {
            Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
            if(self.getAvatar() != null && self.getAvatar().getHash() != null) {
                Intent intent = new Intent(this, AvatarActivity.class);
                intent.putExtra(ExtraKeys.ICHAT_ID, self.getIchatId());
                intent.putExtra(ExtraKeys.AVATAR_HASH, self.getAvatar().getHash());
                intent.putExtra(ExtraKeys.AVATAR_EXTENSION, self.getAvatar().getExtension());
                startActivity(intent);
            }
        });
    }

    private void setupUi() {
        boolean translucentNavigation = WindowAndSystemUiUtil.extendContentUnderSystemBars(this,
                null, new View[]{binding.navigationDrawer1},
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        if(translucentNavigation) {
            UiUtil.setViewMargin(binding.onlineStateIndicator, -1, -1, -1, (int) (WindowAndSystemUiUtil.getNavigationBarHeight(this) / 2.0));
        }
        setupBottomNavigationViewLabelVisibility();
    }

    private void setupBottomNavigationViewLabelVisibility() {
        int bottomNavigationViewLabelVisibilityMode = SharedPreferencesAccessor.DefaultPref.getBottomNavigationViewLabelVisibilityMode(this);
        switch (bottomNavigationViewLabelVisibilityMode){
            case 0:
                binding.bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
                break;
            case 1:
                binding.bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);
                break;
            case 2:
                binding.bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_SELECTED);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupBottomNavigationViewLabelVisibility();
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds) {
        runOnUiThread(() -> {
            if (!isFinishing() && binding != null) {
                binding.updateIndicator.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds) {
        runOnUiThread(() -> {
            try{
                if(updatingIds.size() == 0) binding.updateIndicator.setVisibility(View.GONE);
            }catch (NullPointerException ignore){}
            if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)){
                showNavHeaderInfo();
            }
        });
    }

    @Override
    public void onOnline() {
        runOnUiThread(() -> {
            try{
                binding.onlineStateIndicator.setVisibility(View.GONE);
                View headerView1 = binding.navigationDrawer1.getHeaderView(0);
                headerView1.findViewById(R.id.layout_offline_time).setVisibility(View.GONE);
            }catch (NullPointerException ignore){}
        });
    }

    @Override
    public void onOffline() {
        runOnUiThread(() -> {
            try{
                binding.onlineStateIndicator.setVisibility(View.VISIBLE);
                View headerView1 = binding.navigationDrawer1.getHeaderView(0);
                TextView offlineTimeTextView = headerView1.findViewById(R.id.offline_time);
                long offlineTime = SharedPreferencesAccessor.NetPref.getOfflineTime(this);
                String formattedOfflineTime = TimeUtil.formatRelativeTime(new Date(offlineTime));
                offlineTimeTextView.setText(formattedOfflineTime);
                headerView1.findViewById(R.id.layout_offline_time).setVisibility(View.VISIBLE);
            }catch (NullPointerException ignore){}
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_chat_fab){
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_channel);
        }
    }

    public synchronized void showNavigationMessageBadge(){
        if(messageNavBadge == null) {
            @SuppressLint("RestrictedApi") View view = ((BottomNavigationMenuView) binding.bottomNavigation.getChildAt(0)).getChildAt(0);
            messageNavBadge = BadgeDisplayer.initIndicatorBadge(this, view, Gravity.START | Gravity.BOTTOM, 73, 56, true);
        }
    }

    public synchronized void hideNavigationMessageBadge(){
        if(messageNavBadge != null) {
            messageNavBadge.hide(true);
            messageNavBadge = null;
        }
    }

    public synchronized void showNavigationChannelBadge(){
        if(channelNavBadge == null) {
            @SuppressLint("RestrictedApi") View view = ((BottomNavigationMenuView) binding.bottomNavigation.getChildAt(0)).getChildAt(1);
            channelNavBadge = BadgeDisplayer.initIndicatorBadge(this, view, Gravity.START | Gravity.BOTTOM, 73, 56, true);
        }
    }

    public synchronized void hideNavigationChannelBadge(){
        if(channelNavBadge != null) {
            channelNavBadge.hide(true);
            channelNavBadge = null;
        }
    }

    public synchronized void showNavigationBroadcastBadge(){
        if(broadcastNavBadge == null) {
            @SuppressLint("RestrictedApi") View view = ((BottomNavigationMenuView) binding.bottomNavigation.getChildAt(0)).getChildAt(2);
            broadcastNavBadge = BadgeDisplayer.initIndicatorBadge(this, view, Gravity.START | Gravity.BOTTOM, 73, 56, true);
        }
    }

    public synchronized void hideNavigationBroadcastBadge(){
        if(broadcastNavBadge != null) {
            broadcastNavBadge.hide(true);
            broadcastNavBadge = null;
        }
    }

    @Override
    public void showNewContentBadge(ID id, int newContentCount) {
        switch (id){
            case MESSAGES:
                AtomicBoolean hideNavigationMessageBadge = new AtomicBoolean(true);
                List<OpenedChat> showOpenedChats = OpenedChatDatabaseManager.getInstance().findAllShow();
                showOpenedChats.forEach(showOpenedChat -> {
                    if(ChannelDatabaseManager.getInstance().findOneChannel(showOpenedChat.getChannelIchatId()) != null) {
                        if (showOpenedChat.getNotViewedCount() > 0)
                            hideNavigationMessageBadge.set(false);
                    }
                });
                if(hideNavigationMessageBadge.get()){
                    hideNavigationMessageBadge();
                }else {
                    showNavigationMessageBadge();
                }
                break;
            case CHANNEL_ADDITION_ACTIVITIES:
                if(newContentCount > 0) {
                    showNavigationChannelBadge();
                } else {
                    hideNavigationChannelBadge();
                }
                break;
            case BROADCAST_LIKES:
            case BROADCAST_COMMENTS:
            case BROADCAST_REPLIES:
                if(newContentCount > 0){
                    showNavigationBroadcastBadge();
                }else {
                    hideNavigationBroadcastBadge();
                }
                break;
        }
    }

    @Override
    public void fetchNews(String ichatId) {
        Fragment fragment = getSupportFragmentManager().getFragments().get(0);
        if (fragment instanceof BroadcastsFragment) {
            BroadcastsFragment.needFetchNewBroadcasts = false;
            ((BroadcastsFragment) fragment).fetchNews();
        }else {
            BroadcastsFragment.needFetchNewBroadcasts = true;
        }
    }
}