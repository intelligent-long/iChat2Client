package com.longx.intelligent.android.ichat2.behavior;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.longx.intelligent.android.ichat2.util.UiUtil;

/**
 * Created by LONG on 2024/1/30 at 6:26 AM.
 */
public class MessageDisplayer {
    public enum Duration{SHORT, LONG}

    public static void showToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void showSnackbar(Activity activity, String message, int duration){
        showSnackbar(activity.getWindow().getDecorView(), message, duration, true);
    }

    public static void showSnackbar(View view, String message, int duration){
        showSnackbar(view, message, duration, false);
    }

    public static void showSnackbar(View view, String message, int duration, boolean setBottomMargin){
        try {
            Snackbar snackbar = Snackbar.make(view, message, duration);
            TextView snackbarTextView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            snackbarTextView.setMaxLines(Integer.MAX_VALUE);
            if(setBottomMargin){
                UiUtil.setSnackbarBottomMargin(snackbar, 210);
            }
            snackbar.show();
        }catch (Exception e){
            Log.e(Application.class.getName(), "showSnackBar() 出错", e);
        }
    }

    public static void autoShow(Context context, String message, Duration duration){
        if(context instanceof Activity){
            int snackbarDuration;
            if(duration.equals(Duration.LONG)){
                snackbarDuration = Snackbar.LENGTH_LONG;
            }else {
                snackbarDuration = Snackbar.LENGTH_SHORT;
            }
            showSnackbar((Activity) context, message, snackbarDuration);
        }else {
            int toastDuration;
            if(duration.equals(Duration.LONG)){
                toastDuration = Toast.LENGTH_LONG;
            }else {
                toastDuration = Toast.LENGTH_SHORT;
            }
            showToast(context, message, toastDuration);
        }
    }
}
