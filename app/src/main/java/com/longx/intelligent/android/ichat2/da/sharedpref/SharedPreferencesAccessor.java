package com.longx.intelligent.android.ichat2.da.sharedpref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.data.Avatar;
import com.longx.intelligent.android.ichat2.data.ServerSetting;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.UserInfo;
import com.longx.intelligent.android.ichat2.net.ServerProperties;
import com.longx.intelligent.android.ichat2.util.JsonUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LONG on 2024/3/26 at 11:22 PM.
 */
public class SharedPreferencesAccessor {

    public static class DefaultPref {
        private static class Key {
            private static final String IGNORE_REQUEST_IGNORE_BATTERY_OPTIMIZE = "ignore_request_ignore_battery_optimize";
            private static final String SEARCH_CHANNEL_BY = "search_channel_by";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }

        public static boolean getUseDynamicColorEnabled(Context context){
            return getSharedPreferences(context).getBoolean(context.getString(R.string.preference_key_use_dynamic_color), false);
        }

        public static int getNightMode(Context context){
            return Integer.parseInt(getSharedPreferences(context).getString(context.getString(R.string.preference_key_night_mode), "-1"));
        }

        public static void enableRequestIgnoreBatteryOptimize(Context context){
            saveRequestIgnoreBatteryOptimize(context, true);
        }

        public static void disableRequestIgnoreBatteryOptimize(Context context){
            saveRequestIgnoreBatteryOptimize(context, false);
        }

        private static void saveRequestIgnoreBatteryOptimize(Context context, boolean value){
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.IGNORE_REQUEST_IGNORE_BATTERY_OPTIMIZE, value)
                    .apply();
        }

        public static boolean isRequestIgnoreBatteryOptimizeStateEnabled(Context context){
            return getSharedPreferences(context)
                    .getBoolean(Key.IGNORE_REQUEST_IGNORE_BATTERY_OPTIMIZE, true);
        }

        public static void saveSearchChannelBy(Context context, String searchChannelBy){
            getSharedPreferences(context)
                    .edit()
                    .putString(Key.SEARCH_CHANNEL_BY, searchChannelBy)
                    .apply();
        }

        public static String getSearchChannelBy(Context context){
            return getSharedPreferences(context)
                    .getString(Key.SEARCH_CHANNEL_BY, null);
        }
    }

    public static class NetPref {
        private static final String NAME = "net";
        private static class Key {
            private static final String LOGIN_STATE = "login_state";
            private static final String OFFLINE_TIME = "offline_time";
        }

        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        public static boolean getLoginState(Context context){
            return getSharedPreferences(context).getBoolean(Key.LOGIN_STATE, false);
        }

        public static void saveLoginState(Context context, boolean loggedIn) {
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.LOGIN_STATE, loggedIn)
                    .apply();
        }

        public static long getOfflineTime(Context context){
            return getSharedPreferences(context).getLong(Key.OFFLINE_TIME, -1);
        }

        public static void saveOfflineTime(Context context, long time) {
            getSharedPreferences(context)
                    .edit()
                    .putLong(Key.OFFLINE_TIME, time)
                    .apply();
        }

    }

    public static class ServerSettingPref{
        public static final String NAME = "server_setting";
        private static class Key {
            public static final String USE_CENTRAL = "use_central";
            public static final String HOST = "host";
            public static final String PORT = "port";
            public static final String DATA_FOLDER = "data_folder";
        }

        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        public static void saveServerSetting(Context context, ServerSetting serverSetting) {
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.USE_CENTRAL, serverSetting.isUseCentral())
                    .putString(Key.HOST, serverSetting.getHost())
                    .putInt(Key.PORT, serverSetting.getPort())
                    .putString(Key.DATA_FOLDER, serverSetting.getDataFolder())
                    .apply();
        }

        public static ServerSetting getServerSetting(Context context){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            boolean useCentral = sharedPreferences.getBoolean(Key.USE_CENTRAL, ServerProperties.DEFAULT_USE_CENTRAL);
            String host = sharedPreferences.getString(Key.HOST, null);
            int port = sharedPreferences.getInt(Key.PORT, -1);
            String dataFolder = sharedPreferences.getString(Key.DATA_FOLDER, null);
            if(host == null || port == -1 || dataFolder == null) {
                dataFolder = ServerSetting.buildDataFolderWithoutSuffix(ServerProperties.DEFAULT_HOST, String.valueOf(ServerProperties.DEFAULT_PORT));
                saveServerSetting(context, new ServerSetting(useCentral, ServerProperties.DEFAULT_HOST, ServerProperties.DEFAULT_PORT, dataFolder, true));
                useCentral = sharedPreferences.getBoolean(Key.USE_CENTRAL, ServerProperties.DEFAULT_USE_CENTRAL);
                host = sharedPreferences.getString(Key.HOST, null);
                port = sharedPreferences.getInt(Key.PORT, -1);
                dataFolder = sharedPreferences.getString(Key.DATA_FOLDER, null);
            }
            return new ServerSetting(useCentral, host, port, dataFolder, false);
        }
    }

    public static class UserInfoPref {
        private static final String NAME_USER_INFO = "user_info";
        private static class Key{
            private static final String ICHAT_ID = "ichat_id";
            private static final String ICHAT_ID_USER = "ichat_id_user";
            private static final String EMAIL = "email";
            private static final String REGISTER_TIME = "register_time";
            private static final String USERNAME = "username";
            private static final String AVATAR_HASH = "avatar_hash";
            private static final String AVATAR_ICHAT_ID = "avatar_ichat_id";
            private static final String AVATAR_EXTENSION = "avatar_extension";
            private static final String AVATAR_TIME = "avatar_time";
            private static final String SEX = "sex";
            private static final String FIRST_REGION_ADCODE = "first_region_adcode";
            private static final String FIRST_REGION_NAME = "first_region_name";
            private static final String SECOND_REGION_ADCODE = "second_region_adcode";
            private static final String SECOND_REGION_NAME = "second_region_name";
            private static final String THIRD_REGION_ADCODE = "third_region_adcode";
            private static final String THIRD_REGION_NAME = "third_region_name";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME_USER_INFO, Context.MODE_PRIVATE);
        }

        @SuppressLint("ApplySharedPref")
        public static void saveCurrentUserInfo(Context context, Self self){
            getSharedPreferences(context)
                    .edit()
                    .putString(Key.ICHAT_ID, self.getIchatId())
                    .putString(Key.ICHAT_ID_USER, self.getIchatIdUser())
                    .putString(Key.EMAIL, self.getEmail())
                    .putLong(Key.REGISTER_TIME, self.getRegisterTime().getTime())
                    .putString(Key.USERNAME, self.getUsername())
                    .putString(Key.AVATAR_HASH, self.getAvatar().getHash())
                    .putString(Key.AVATAR_ICHAT_ID, self.getAvatar().getIchatId())
                    .putString(Key.AVATAR_EXTENSION, self.getAvatar().getExtension())
                    .putLong(Key.AVATAR_TIME, self.getAvatar().getTime().getTime())
                    .putInt(Key.SEX, self.getSex() == null ? -1 : self.getSex())
                    .putInt(Key.FIRST_REGION_ADCODE, self.getFirstRegion() == null ? -1 : self.getFirstRegion().getAdcode())
                    .putString(Key.FIRST_REGION_NAME, self.getFirstRegion() == null ? null : self.getFirstRegion().getName())
                    .putInt(Key.SECOND_REGION_ADCODE, self.getSecondRegion() == null ? -1 : self.getSecondRegion().getAdcode())
                    .putString(Key.SECOND_REGION_NAME, self.getSecondRegion() == null ? null : self.getSecondRegion().getName())
                    .putInt(Key.THIRD_REGION_ADCODE, self.getThirdRegion() == null ? -1 : self.getThirdRegion().getAdcode())
                    .putString(Key.THIRD_REGION_NAME, self.getThirdRegion() == null ? null : self.getThirdRegion().getName())
                    .commit();
        }

        public static Self getCurrentUserInfo(Context context){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            String ichatId = sharedPreferences.getString(Key.ICHAT_ID, null);
            String ichatIdUser = sharedPreferences.getString(Key.ICHAT_ID_USER, null);
            String email = sharedPreferences.getString(Key.EMAIL, null);
            long registerTimeLong = sharedPreferences.getLong(Key.REGISTER_TIME, -1);
            String username = sharedPreferences.getString(Key.USERNAME, null);
            String avatarHash = sharedPreferences.getString(Key.AVATAR_HASH, null);
            String avatarIchatId = sharedPreferences.getString(Key.AVATAR_ICHAT_ID, null);
            String avatarExtension = sharedPreferences.getString(Key.AVATAR_EXTENSION, null);
            long avatarTimeLong = sharedPreferences.getLong(Key.AVATAR_TIME, -1);
            Date registerTime = null;
            Date avatarTime = null;
            if(registerTimeLong != -1){
                registerTime = new Date(registerTimeLong);
            }
            if(avatarTimeLong != -1){
                avatarTime = new Date(avatarTimeLong);
            }
            int sex = sharedPreferences.getInt(Key.SEX, -1);
            int firstRegionAdcode = sharedPreferences.getInt(Key.FIRST_REGION_ADCODE, -1);
            String firstRegionName = sharedPreferences.getString(Key.FIRST_REGION_NAME, null);
            int secondRegionAdcode = sharedPreferences.getInt(Key.SECOND_REGION_ADCODE, -1);
            String secondRegionName = sharedPreferences.getString(Key.SECOND_REGION_NAME, null);
            int thirdRegionAdcode = sharedPreferences.getInt(Key.THIRD_REGION_ADCODE, -1);
            String thirdRegionName = sharedPreferences.getString(Key.THIRD_REGION_NAME, null);
            return new Self(ichatId, ichatIdUser, email, registerTime, username,
                    new Avatar(avatarHash, avatarIchatId, avatarExtension, avatarTime),
                    sex == -1 ? null : sex,
                    (firstRegionAdcode == -1 && firstRegionName == null) ? null : new UserInfo.Region(firstRegionAdcode, firstRegionName),
                    (secondRegionAdcode == -1 && secondRegionName == null) ? null : new UserInfo.Region(secondRegionAdcode, secondRegionName),
                    (thirdRegionAdcode == -1 && thirdRegionName == null) ? null : new UserInfo.Region(thirdRegionAdcode, thirdRegionName));
        }

        @SuppressLint("ApplySharedPref")
        public static void clear(Context context){
            getSharedPreferences(context)
                    .edit()
                    .clear()
                    .commit();
        }
    }

    public static class ServerMessageServicePref {
        private static final String NAME = "server_message_service";
        private static class Key{
            private static final String RUNNING_TIME = "running_time";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        public static void recordRunningTime(Context context, long time){
            getSharedPreferences(context)
                    .edit()
                    .putLong(Key.RUNNING_TIME, time)
                    .apply();
        }

        public static long getRunningTime(Context context){
            return getSharedPreferences(context).getLong(Key.RUNNING_TIME, -1);
        }
    }

    public static class NewContentCount {
        private static final String NAME = "new_content_count";
        private static class Key{
            private static final String CHANNEL_ADDITION_ACTIVITIES = "channel_addition_activities";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        public static void saveChannelAdditionActivities(Context context, int newContentCount){
            getSharedPreferences(context)
                    .edit()
                    .putInt(Key.CHANNEL_ADDITION_ACTIVITIES, newContentCount)
                    .apply();
        }

        public static int getChannelAdditionActivities(Context context){
            return getSharedPreferences(context)
                    .getInt(Key.CHANNEL_ADDITION_ACTIVITIES, 0);
        }
    }

    public static class ApiJson{
        private static final String NAME = "api_json";
        private static class Key{
            private static final String CHANNEL_ADDITION_ACTIVITIES = "channel_addition_activities";
        }
        private static final int MAX_CHANNEL_ADDITION_ACTIVITIES_SIZE = 500;
        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        private static class IndexedApiJson {
            private final int index;
            private final String json;

            public IndexedApiJson() {
                this(0, null);
            }

            public IndexedApiJson(int index, String json) {
                this.index = index;
                this.json = json;
            }

            public int getIndex() {
                return index;
            }

            public String getJson() {
                return json;
            }
        }

        public static void addChannelAdditionActivities(Context context, String json){
            Set<String> paginatedJsonSet = getSharedPreferences(context).getStringSet(Key.CHANNEL_ADDITION_ACTIVITIES, new HashSet<>());
            if(paginatedJsonSet.size() > MAX_CHANNEL_ADDITION_ACTIVITIES_SIZE) return;
            int index = paginatedJsonSet.size();
            IndexedApiJson indexedApiJson = new IndexedApiJson(index, json);
            String paginatedJson = JsonUtil.toJson(indexedApiJson);
            HashSet<String> paginatedJsonSetCopy = new HashSet<>(paginatedJsonSet);
            paginatedJsonSetCopy.add(paginatedJson);
            getSharedPreferences(context)
                    .edit()
                    .putStringSet(Key.CHANNEL_ADDITION_ACTIVITIES, paginatedJsonSetCopy)
                    .apply();
        }

        public static void clearChannelAdditionActivities(Context context){
            getSharedPreferences(context)
                    .edit()
                    .remove(Key.CHANNEL_ADDITION_ACTIVITIES)
                    .apply();
        }

        public static List<String> getChannelAdditionActivities(Context context){
            List<String> result = new ArrayList<>();
            Set<String> paginatedJsonSet = getSharedPreferences(context).getStringSet(Key.CHANNEL_ADDITION_ACTIVITIES, new HashSet<>());
            List<IndexedApiJson> indexedApiJsonList = new ArrayList<>();
            paginatedJsonSet.forEach(paginatedJson -> {
                IndexedApiJson indexedApiJson = JsonUtil.toObject(paginatedJson, IndexedApiJson.class);
                indexedApiJsonList.add(indexedApiJson);
            });
            indexedApiJsonList.sort(Comparator.comparingInt(o -> o.index));
            indexedApiJsonList.forEach(indexedApiJson -> {
                result.add(indexedApiJson.json);
            });
            return result;
        }
    }

}
