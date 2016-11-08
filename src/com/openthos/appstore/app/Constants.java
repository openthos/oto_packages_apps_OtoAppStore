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
    public static final String BASEURL = "";
    public static String USER_ID = "admin";
    public static String BASE_FILEPATH = Environment.getExternalStorageDirectory().toString() +
            "/opthos";
    public static String DOWNFILEPATH = BASE_FILEPATH + "/" + USER_ID + "/FILETEMP";
    public static String TEMP_FILEPATH = BASE_FILEPATH + "/" + USER_ID + "/TEMPDir";
    public static final String[] WRONG_CHARS = {
            "/", "\\", "*", "?", "<", ">", "\"", "|"};

    public static final String FROM_FRAGMENT = "fromfragment";
    public static final String BUNDLE = "bundle";
    public static final String APP_LAYOUT_INFO = "applayoutinfo";


    public static final int HOME_FRAGMENT = 0;
    public static final int SOFTWARE_FRAGMENT = 1;
    public static final int GAME_FRAGMENT = 2;
    public static final int MANAGER_FRAGMENT = 3;
    public static final int APP_LAYOUT_FRAGMENT = 4;
    public static final int APP_TYPE_FRAGMENT = 5;

    public static final int MANAGER_UPDATE = 11;
    public static final int MANAGER_DOWNLOAD = 12;

    public static final int COMMENT_NUM_FALSE = 4;
    public static final int APP_NUM_FALSE = 6;
    public static final int MANAGER_NUM_FALSE = 3;
    public static final int GRIDVIEW_NUM_COLUMS = 3;
    public static final int FRAGMENT_COUNT = 4;
    public static final int DRAWABLE_PADDING = 10;
    public static final int DRAWABLE_SIZE = 20;
    public static final int HEIGHT_MASK = 0x3fffffff;

    public static final int INSTALL_BUTTON_NOT_INSTALL = 0;
    public static final int INSTALL_BUTTON_HAVE_INSTALLED = 1;
    public static final int INSTALL_BUTTON_CONTINUE = 2;
    public static final int INSTALL_BUTTON_PAUSE = 3;

    public static final int DELAY_TIME_2 = 2000;
    public static final int DELAY_TIME_3 = 3000;
    public static final int DELAY_TIME_5 = 5000;

    public static List<AppLayoutInfo> getData() {
        AppLayoutGridviewInfo appLayoutGridviewInfo = new AppLayoutGridviewInfo(1,
                "http://pic9.nipic.com/20100906/1295091_134639124058_2.jpg",
                "news", "news", 0);
        List<AppLayoutGridviewInfo> data = new ArrayList<>();
        data.add(appLayoutGridviewInfo);
        data.add(appLayoutGridviewInfo);
        data.add(appLayoutGridviewInfo);
        data.add(appLayoutGridviewInfo);
        data.add(appLayoutGridviewInfo);
        data.add(appLayoutGridviewInfo);
        data.add(appLayoutGridviewInfo);
        data.add(appLayoutGridviewInfo);
        data.add(appLayoutGridviewInfo);
        List<AppLayoutInfo> datas = new ArrayList<>();

        AppLayoutInfo appLayoutInfo = new AppLayoutInfo(4, "gemae", data);
        datas.add(appLayoutInfo);
        AppLayoutInfo appLayoutInfo1 = new AppLayoutInfo(4, "software", data);
        datas.add(appLayoutInfo1);
        return datas;
    }

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

    public static int[] getImages() {
        return new int[]{R.mipmap.back, R.mipmap.undown, R.mipmap.down};
    }

    public static List<CommentInfo> getComment() {
        // The hard code number is dummy data.
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

    public static List<ManagerInfo> getManagerInfo() {
        ManagerInfo managerInfo = new ManagerInfo(1,
                                     "http://www.99danji.com/upload/20128/2012081537367909.jpg",
                                     "afdsf", "12", "fhjaskfh");
        List<ManagerInfo> datas = new ArrayList<>();
        datas.add(managerInfo);
        datas.add(managerInfo);
        datas.add(managerInfo);
        datas.add(managerInfo);
        datas.add(managerInfo);
        datas.add(managerInfo);

        return datas;
