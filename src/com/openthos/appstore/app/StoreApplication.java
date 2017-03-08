package com.openthos.appstore.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

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
        initImageLoader();
    }

    public void initImageLoader(){
        StorageUtils.getOwnCacheDirectory(getApplicationContext(),"appStore/cache");
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .build();
        ImageLoader.getInstance().init(configuration);
    }
}