package com.openthos.appstore.app;

import android.app.Application;
import android.content.Intent;

import com.openthos.appstore.activity.BaseActivity;
//import com.openthos.appstore.utils.download.DownLoadService;
import com.openthos.appstore.utils.sql.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-25.
 */
public class StoreApplication extends Application {
    public static List<BaseActivity> activities;

    @Override
    public void onCreate() {
        super.onCreate();
        activities = new ArrayList<>();
//        startService(new Intent(this, DownLoadService.class));
    }
}
