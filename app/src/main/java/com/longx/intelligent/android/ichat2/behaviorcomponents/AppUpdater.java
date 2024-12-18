package com.longx.intelligent.android.ichat2.behaviorcomponents;

import android.app.Activity;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.da.cachefile.CacheFilesAccessor;
import com.longx.intelligent.android.ichat2.dialog.ProgressOperatingDialog;
import com.longx.intelligent.android.ichat2.net.download.Downloader;
import com.longx.intelligent.android.ichat2.util.AppInstaller;
import com.longx.intelligent.android.ichat2.util.ColorUtil;

import java.io.File;
import java.util.function.Consumer;

import cn.zhxu.okhttps.Download;
import cn.zhxu.okhttps.Process;

/**
 * Created by LONG on 2024/10/30 at 上午7:13.
 */
public class AppUpdater {
    private final Activity activity;
    private final String url;
    private final ProgressOperatingDialog progressOperatingDialog;
    private Downloader downloader;

    public AppUpdater(Activity activity, String url) {
        this.activity = activity;
        this.url = url;
        progressOperatingDialog = new ProgressOperatingDialog(activity, () -> {
            if(downloader != null) downloader.stop();
        });
    }

    public void start(){
        progressOperatingDialog.create().show();
        progressOperatingDialog.updateText("准备中...");

        downloader = new Downloader(url, CacheFilesAccessor.App.prepareAppUpdateCacheFile(activity));
        downloader
                .setSuccessYier(file -> {
                    progressOperatingDialog.dismiss();
                    AppInstaller.installApk(activity, file.getAbsolutePath());
                })
                .setFailureYier(failure -> {
                    progressOperatingDialog.getBinding().indicator.setIndicatorColor(ColorUtil.getColor(activity, R.color.negative_red));
                    progressOperatingDialog.updateText("下载失败 > " + failure.getException().getClass().getName());
                })
                .setProgressYier(process -> {
                    progressOperatingDialog.updateText("下载中...");
                    progressOperatingDialog.updateProgress(process.getDoneBytes(), process.getTotalBytes());
                })
                .start();
    }
}
