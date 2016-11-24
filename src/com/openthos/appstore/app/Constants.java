package com.openthos.appstore.app;

import android.os.Environment;

import com.openthos.appstore.R;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.bean.CommentInfo;
import com.openthos.appstore.bean.AppLayoutGridviewInfo;
import com.openthos.appstore.bean.AppTypeInfo;
import com.openthos.appstore.bean.AppTypeListviewInfo;
import com.openthos.appstore.bean.ManagerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-25.
 */
public class Constants {
    public static final String BASEURL = "http://dev.openthos.org/openthos/appstore";
    public static String USER_ID = "admin";
    public static String BASE_FILEPATH = Environment.getExternalStorageDirectory().
            toString() + "/OASDownloads";
    public static String DOWNFILEPATH = BASE_FILEPATH + "/" + USER_ID + "/FILETEMP";
    public static String TEMP_FILEPATH = BASE_FILEPATH + "/" + USER_ID + "/TEMPDir";
    public static String CACHE_DATA = BASE_FILEPATH + "/" + USER_ID + "/CACHEDATA";

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

    public static final int COMMENT_NUM_FALSE = 4;
    public static final int APP_NUM_FALSE = 6;
    public static final int MANAGER_NUM_FALSE = 3;
    public static final int GRIDVIEW_NUM_COLUMS = 3;
    public static final int DRAWABLE_PADDING = 10;
    public static final int DRAWABLE_SIZE = 20;
    public static final int HEIGHT_MASK = 0x3fffffff;

    public static final int APP_NOT_INSTALL = 0;
    public static final int APP_HAVE_INSTALLED = 1;
    public static final int APP_DOWNLOAD_CONTINUE = 2;
    public static final int APP_DOWNLOAD_PAUSE = 3;
    public static final int APP_NEED_UPDATE = 4;
    public static final int APP_DOWNLOAD_FINISHED = 5;

    public static final int DELAY_TIME_2 = 2000;
    public static final int DELAY_TIME_3 = 3000;
    public static final int DELAY_TIME_5 = 5000;

    public static List<AppTypeInfo> getDataItemRightInfo() {
        List<AppTypeInfo> data = new ArrayList<>();
        List<AppTypeListviewInfo> datas = new ArrayList<>();

        AppTypeListviewInfo appTypeListviewInfo = new AppTypeListviewInfo(3, "net");
        AppTypeListviewInfo appTypeListviewInfo1 = new AppTypeListviewInfo(3, "study");
        AppTypeListviewInfo appTypeListviewInfo2 = new AppTypeListviewInfo(3, "business");

        datas.add(appTypeListviewInfo);
        datas.add(appTypeListviewInfo2);
        datas.add(appTypeListviewInfo1);
        datas.add(appTypeListviewInfo);
        datas.add(appTypeListviewInfo2);
        datas.add(appTypeListviewInfo1);

        AppTypeInfo appTypeInfo = new AppTypeInfo(3, "more", datas);
        data.add(appTypeInfo);
        data.add(appTypeInfo);
        return data;
    }

    public static List<String> getString() {
        List<String> data = new ArrayList<>();
        data.add("http://pic38.nipic.com/20140218/10830427_105310632133_2.jpg");
        data.add("http://pic9.nipic.com/20100906/1295091_134639124058_2.jpg");
        data.add("http://pic27.nipic.com/20130326/1330653_182644630158_2.jpg");
        data.add("http://pic38.nipic.com/20140218/10830427_105310632133_2.jpg");
        data.add("http://img5.hao123.com/data/1_d070d2b49698a7c71809451a25124da7_510");
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