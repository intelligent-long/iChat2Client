package com.longx.intelligent.android.ichat2.permission;

import android.os.Build;

/**
 * Created by LONG on 2024/4/21 at 11:03 PM.
 */
public class PermissionUtil {

    public static boolean needExternalStoragePermission(){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
    }

}
