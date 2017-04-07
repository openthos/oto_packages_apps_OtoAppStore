package com.openthos.appstore.utils;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.openthos.appstore.app.StoreApplication;

public class ImageCache {
    private static DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true).build();

    public static void loadImage(ImageView img, String imageUrl) {
        ImageLoader.getInstance().
                displayImage(StoreApplication.mBaseUrl + "/" + imageUrl, img, mOptions);
    }
}
