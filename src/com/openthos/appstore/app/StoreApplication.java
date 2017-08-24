package com.openthos.appstore.app;

import android.app.Application;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StoreApplication extends Application {
    public static String mBaseUrl = "http://dev.openthos.org/openthos/appstores";
    public static String mUserId = "admin";

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
    }

    public void initImageLoader(){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .diskCacheSize(Constants.DISK_CACHE_SIZE)
                .memoryCacheSize(Constants.MEMORY_CACHE_SIZE)
                .build();
        ImageLoader.getInstance().init(configuration);
    }
}
