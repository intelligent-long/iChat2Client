package com.longx.intelligent.android.ichat2.da.database;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.database.manager.ChannelAdditionActivityDatabaseManager;

/**
 * Created by LONG on 2024/1/30 at 5:30 AM.
 */
public class DatabaseInitiator {
    public static void initAll(Context context){
        ChannelAdditionActivityDatabaseManager.init(context);
    }
}
