package com.openthos.appstore.utils;

import android.os.Environment;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by root on 3/6/17.
 */

public class ImageCache {
    private final String mCachePath = Environment.getDownloadCacheDirectory().getPath() + "/appStore";
    private static DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true).build();

    public static void loadImage(ImageView img,String imageUrl){
        ImageLoader.getInstance().displayImage(imageUrl,img,mOptions);
    }
}
