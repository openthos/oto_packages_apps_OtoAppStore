package com.openthos.appstore.app;

import android.os.Environment;

import com.openthos.appstore.bean.CommentInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-25.
 */
public class Constants {
//    public static String BASEURL = "http://dev.openthos.org/openthos/appstore";
    public static String BASEURL = "http://192.168.0.180/openthos/appstore";

    public static String USER_ID = "admin";
    public static String BASE_FILEPATH = Environment.getExternalStoragePublicDirectory(
                                         Environment.DIRECTORY_DOWNLOADS).toString();
    public static String DOWNFILEPATH = BASE_FILEPATH + "/appdir";
    public static String TEMP_FILEPATH = BASE_FILEPATH + "/tempdir";
    public static String CACHE_DATA = BASE_FILEPATH + "/cache";

    public static final String SP_CACHE_DATA = "cache";
    public static final String SP_ALL_DATA = "all";
    public static final String SP_DOWNLOAD_STATE = "state";

    public static final String APP_LAYOUT_INFO = "applayoutinfo";

    public static final int HOME_FRAGMENT = 0;
    public static final int SOFTWARE_FRAGMENT = 1;
    public static final int GAME_FRAGMENT = 2;
    public static final int MANAGER_FRAGMENT = 3;
    public static final int APP_LAYOUT_FRAGMENT = 4;
    public static final int APP_TYPE_FRAGMENT = 5;
    public static final int DETAIL_FRAGMENT = 6;
    public static final int MORE_FRAGMENT = 7;
    public static final int COMMENT_FRAGMENT = 8;
    public static final int SEARCH_FRAGMENT = 9;
    public static final int TOAST = 10;
    public static final int REFRESH = 11;
    public static final int UPDATE = 12;

    public static final int COMMENT_NUM_FALSE = 4;
    public static final int APP_NUM_FALSE = 8;
    public static final int MANAGER_NUM_FALSE = 3;
    public static final int GRIDVIEW_NUM_COLUMS = 4;
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

    public static final int DELAY_TIME_2 = 2000;
    public static final int DELAY_TIME_3 = 3000;
    public static final int DELAY_TIME_5 = 5000;

    public static final int KB = 1024;
    public static final int MB = 1024 * KB;

    public static final int MAX_PROGRESS = 100;

    public static List<String> getString() {
        List<String> data = new ArrayList<>();
        data.add(BASEURL + "/carousel/1.jpg");
        data.add(BASEURL + "/carousel/2.jpg");
        data.add(BASEURL + "/carousel/3.jpg");
        data.add(BASEURL + "/carousel/4.jpg");
        data.add(BASEURL + "/carousel/5.jpg");
        return data;
    }

    public static List<CommentInfo> getComment() {
        CommentInfo commentInfo = new CommentInfo(1, "gdafgasfsdagsag", 48, "asgsd", "2016.10.24");
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
