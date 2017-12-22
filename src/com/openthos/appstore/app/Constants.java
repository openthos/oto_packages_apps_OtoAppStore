package com.openthos.appstore.app;

import android.os.Environment;

import com.openthos.appstore.bean.BannerUrl;
import com.openthos.appstore.bean.CommentInfo;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final String BASE_FILEPATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).toString();
    public static final String SP_CACHE_DATA = "cache";
    public static final String SP_ALL_DATA = "all";
    public static final String SP_DOWNLOAD_STATE = "state";
    public static final int HOME_FRAGMENT = 0;
    public static final int SOFTWARE_FRAGMENT = 1;
    public static final int GAME_FRAGMENT = 2;
    public static final int MANAGER_FRAGMENT = 3;
    public static final int DETAIL_FRAGMENT = 4;
    public static final int MORE_FRAGMENT = 5;
    public static final int COMMENT_FRAGMENT = 6;
    public static final int SEARCH_FRAGMENT = 7;
    public static final int INSTALL_APK = 9;
    public static final int TOAST = 10;
    public static final int REFRESH = 11;

    public static final int COMMENT_NUM_FALSE = 4;
    public static final int MANAGER_NUM_FALSE = 3;
    public static final int DRAWABLE_PADDING = 10;
    public static final int DRAWABLE_SIZE = 20;
    public static final int HEIGHT_MASK = 0x3fffffff;

    public static final int APP_NOT_EXIST = -1;
    public static final int APP_NOT_INSTALL = 0;
    public static final int APP_HAVE_INSTALLED = 1;
    public static final int APP_DOWNLOAD_CONTINUE = 2;
    public static final int APP_DOWNLOAD_PAUSE = 3;
    public static final int APP_NEED_UPDATE = 4;
    public static final int APP_DOWNLOAD_FINISHED = 5;

    public static final int TIME_THREE_SECONDS = 3000;
    public static final int TIME_FIVE_SECONDS = 5000;
    public static final int TIME_TEN_SECONDS = 10000;

    public static final int KB = 1024;
    public static final int MB = 1024 * KB;

    public static final int MAX_PROGRESS = 100;
    public static final int DISK_CACHE_SIZE = 50 * 1020 * 1024;
    public static final int MEMORY_CACHE_SIZE = 2 * 1020 * 1024;

    public static List<BannerUrl> getString() {
        List<BannerUrl> data = new ArrayList<>();
        data.add(new BannerUrl(1,"carousel/1.jpg"));
        data.add(new BannerUrl(2,"carousel/2.jpg"));
        data.add(new BannerUrl(3,"carousel/3.jpg"));
        data.add(new BannerUrl(4,"carousel/4.jpg"));
        data.add(new BannerUrl(5,"carousel/5.jpg"));
        return data;
    }

    public static List<CommentInfo> getComment() {
        CommentInfo commentInfo = new CommentInfo(1, "gdafgasfsdagsag", 4, "asgsd", "2016.10.24");
        List<CommentInfo> data = new ArrayList<>();
        data.add(commentInfo);
        data.add(commentInfo);
        data.add(commentInfo);
        data.add(commentInfo);
        data.add(commentInfo);
        data.add(commentInfo);
        return data;
    }
}
