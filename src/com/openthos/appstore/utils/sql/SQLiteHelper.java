package com.openthos.appstore.utils.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String mDatabasename = "openthos";
    private static CursorFactory mFactory = null;
    private static final int mVersion = 1;
    public static final String DOWNLOAD_INFO = "downloadinfo";
    public static final String APP_INSTALL = "appinstall";

    public SQLiteHelper(Context context) {
        super(context, mDatabasename, mFactory, mVersion);
    }

    public SQLiteHelper(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String downloadsql = "CREATE TABLE IF NOT EXISTS " + DOWNLOAD_INFO + " ("
                + "id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
                + "userID VARCHAR, "
                + "taskID VARCHAR, "
                + "url VARCHAR, "
                + "filePath VARCHAR, "
                + "fileName VARCHAR, "
                + "fileSize VARCHAR, "
                + "downLoadSize VARCHAR "
                + ")";
        db.execSQL(downloadsql);

        String appinstall = "CREATE TABLE IF NOT EXISTS " + APP_INSTALL + " ("
                + "id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
                + "name VARCHAR, "
                + "packageName VARCHAR, "
                + "versionName VARCHAR, "
                + "versionCode VARCHAR "
                + ")";
        db.execSQL(appinstall);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
