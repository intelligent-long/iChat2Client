package com.longx.intelligent.android.ichat2.activity.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.color.DynamicColors;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.permission.LinkPermissionOperatorActivity;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.permission.PermissionOperator;
import com.longx.intelligent.android.ichat2.util.UiUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LONG on 2024/1/10 at 8:17 PM.
 */
public class BaseActivity extends HoldableActivity implements LinkPermissionOperatorActivity {
    private Bundle savedInstanceState;
    private final Set<PermissionOperator> permissionOperators = new HashSet<>();
    private boolean autoCancelInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        checkAndSetNightMode();
        super.onCreate(savedInstanceState);
        checkAndEnableDynamicColor();
    }

    protected Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    private void checkAndEnableDynamicColor() {
        boolean useDynamicColorEnabled = SharedPreferencesAccessor.DefaultPref.getUseDynamicColorEnabled(this);
        if (useDynamicColorEnabled) {
            DynamicColors.applyToActivityIfAvailable(this);
        }
    }

    private void checkAndSetNightMode(){
        int nightMode = SharedPreferencesAccessor.DefaultPref.getNightMode(this);
        switch (nightMode){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    public void setAutoCancelInput(boolean autoCancelInput) {
        this.autoCancelInput = autoCancelInput;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(autoCancelInput) UiUtil.autoCancelInput(this, event);
        return super.dispatchTouchEvent(event);
    }

    protected void setupDefaultBackNavigation(MaterialToolbar materialToolbar){
        setupBackNavigation(R.drawable.arrow_back_24px, materialToolbar);
    }

    protected void setupCloseBackNavigation(MaterialToolbar materialToolbar){
        setupBackNavigation(R.drawable.close_24px, materialToolbar);
    }

    protected void setupBackNavigation(int resId, MaterialToolbar materialToolbar) {
        setupBackNavigation(resId, materialToolbar, ColorUtil.getAttrColor(this, android.R.attr.colorControlNormal));
    }

    protected void setupBackNavigation(MaterialToolbar materialToolbar, int color) {
        setupBackNavigation(R.drawable.arrow_back_24px, materialToolbar, color);
    }

    protected void setupBackNavigation(int resId, MaterialToolbar materialToolbar, int color) {
        materialToolbar.setNavigationIcon(resId);
        materialToolbar.setNavigationIconTint(color);
        materialToolbar.setNavigationContentDescription(R.string.navigation_back);
        materialToolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!isUiShowing()){
            onHome();
        }
    }

    public void onHome() {
    }

    public boolean isUiShowing() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED
                    || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE)
            ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionOperators.forEach(permissionOperator -> {
            permissionOperator.onRequestPermissionsResult(requestCode, permissions, grantResults);
        });
    }

    public Set<PermissionOperator> getPermissionOperators() {
        return permissionOperators;
    }

    @Override
    public void linkPermissionOperator(PermissionOperator permissionOperator) {
        permissionOperators.add(permissionOperator);
    }
}
