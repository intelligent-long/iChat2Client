package com.longx.intelligent.android.ichat2.dialog;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

import java.util.Objects;

/**
 * Created by LONG on 2024/1/8 at 9:20 PM.
 */
public abstract class AbstractDialog {
    private final AppCompatActivity activity;
    private final MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog dialog;
    private final ContextThemeWrapper dialogContext;

    public AbstractDialog(AppCompatActivity activity) {
        this(activity, false);
    }

    public AbstractDialog(AppCompatActivity activity, boolean centered){
        this.activity = activity;
        if(centered) {
            this.dialogBuilder = new MaterialAlertDialogBuilder(activity, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered);
        }else {
            this.dialogBuilder = new MaterialAlertDialogBuilder(activity);
        }
        TypedValue outValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.dialogTheme, outValue, true);
        this.dialogContext = new ContextThemeWrapper(getActivity(), outValue.resourceId);
    }

    public AbstractDialog(AppCompatActivity activity, int style){
        this.activity = activity;
        this.dialogBuilder = new MaterialAlertDialogBuilder(activity, style);
        this.dialogContext = new ContextThemeWrapper(getActivity(), style);
    }

    protected View createView(LayoutInflater layoutInflater){
        return null;
    }

    protected abstract AlertDialog create(MaterialAlertDialogBuilder builder);

    public void show(){
        activity.runOnUiThread(() -> {
            View view = createView(activity.getLayoutInflater().cloneInContext(dialogContext));
            if(view != null){
                dialogBuilder.setView(view);
            }
            dialog = create(dialogBuilder);
            try {
                dialog.show();
            }catch (WindowManager.BadTokenException ignore){}
            if (view != null) {
                setAutoCancelInput(view);
            }
            onDialogShowed();
        });
    }

    protected void onDialogShowed(){
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setAutoCancelInput(View contentView) {
        contentView.setOnTouchListener((view, motionEvent) -> {
            Window window = dialog.getWindow();
            UiUtil.autoCancelInput(activity, window == null ? null : window.getCurrentFocus(), motionEvent);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                return true;
            }
            return false;
        });
    }

    public void hide() {
        activity.runOnUiThread(() -> {
            if (dialog != null) {
                dialog.hide();
            }
        });
    }

    public void dismiss() {
        activity.runOnUiThread(() -> {
            if (dialog != null) {
                try {
                    dialog.dismiss();
                }catch (Exception ignore){}
            }
        });
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public AlertDialog getDialog() {
        return dialog;
    }
}
