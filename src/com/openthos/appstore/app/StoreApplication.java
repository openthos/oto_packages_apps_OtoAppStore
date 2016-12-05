package com.openthos.appstore.app;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by luojunhuan on 16-10-25.
 */
public class StoreApplication extends Application {
    public static final String DATE_FORMAT =
            new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
