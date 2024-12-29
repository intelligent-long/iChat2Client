package com.longx.intelligent.android.ichat2.da;

import android.content.Context;
import android.os.Environment;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.net.ServerConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LONG on 2024/3/28 at 6:05 PM.
 */
public class DataPaths {
    public static String serverFolder(Context context){
        if(SharedPreferencesAccessor.ServerPref.isUseCentral(context)){
            ServerConfig centralServerConfig = SharedPreferencesAccessor.ServerPref.getCentralServerConfig(context);
            return centralServerConfig == null ? null : centralServerConfig.getDataFolder();
        }else {
            return SharedPreferencesAccessor.ServerPref.getCustomServerConfig(context).getDataFolder();
        }
    }

    public static class Cache {
        public static String cacheFileRootPath(Context context) {
            return context.getCacheDir().getAbsolutePath() +
                    java.io.File.separator + serverFolder(context);
        }

        public static String chatVoiceTempFilePath(Context context, String channelId) {
            return cacheFileRootPath(context) +
                    File.separator + channelId +
                    File.separator + "voice_temp" +
                    File.separator + "voice_temp.aac";
        }

        public static String appUpdateCacheFilePath(Context context){
            return cacheFileRootPath(context) +
                    File.separator + "app-update" +
                    File.separator + "app-release.apk";
        }
    }

    public static class PrivateFile {
        public static String privateFileRootPath(Context context){
            return context.getFilesDir().getAbsolutePath() +
                    java.io.File.separator + serverFolder(context);
        }

        public static String databaseFilePath(Context context, String ichatId, String databaseFileName) {
            return PrivateFile.privateFileRootPath(context) +
                    java.io.File.separator + "database" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + databaseFileName;
        }

        public static String chatImageFilePath(Context context, String ichatId, String imageFileName){
            return PrivateFile.privateFileRootPath(context) +
                    java.io.File.separator + "chat_image" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + imageFileName;
        }

        public static String chatFileFilePath(Context context, String ichatId, String fileName){
            return PrivateFile.privateFileRootPath(context) +
                    java.io.File.separator + "chat_file" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + fileName;
        }

        public static String chatVideoFilePath(Context context, String ichatId, String fileName){
            return PrivateFile.privateFileRootPath(context) +
                    File.separator + "chat_video" +
                    File.separator + ichatId +
                    File.separator + fileName;
        }

        public static String chatVoiceFilePath(Context context, String ichatId, String fileName){
            return PrivateFile.privateFileRootPath(context) +
                    File.separator + "chat_voice" +
                    File.separator + ichatId +
                    File.separator + fileName;
        }
    }

    public static class PublicFile{
        public static String publicFileRootPath(){
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "iChat";
        }

        public static String chatFilePath(ChatMessage chatMessage){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(chatMessage.getTime().getTime()) + "_" + chatMessage.getUuid() + "_" + chatMessage.getFileName();
            return publicFileRootPath() + File.separator + "Chat" + File.separator + fileName;
        }

        public static String broadcastFilePath(Broadcast broadcast, int mediaIndex){
            BroadcastMedia broadcastMedia = broadcast.getBroadcastMedias().get(mediaIndex);
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(broadcast.getTime().getTime()) + "_" + broadcast.getBroadcastId() + "_" + broadcastMedia.getMediaId() + "." + broadcastMedia.getExtension();
            return publicFileRootPath() + File.separator + "Broadcast" + File.separator + fileName;
        }

        public static String capturedMediaFilePath(){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(new Date());
            return publicFileRootPath() + File.separator + "Captured" + File.separator + fileName;
        }

        public static String avatarFilePath(String avatarHash, String ichatId, String avatarExtension){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(new Date()) + "_" + ichatId + "_" + avatarHash + "." + avatarExtension;
            return publicFileRootPath() + File.separator + "Avatar" + File.separator + fileName;
        }
    }
}
