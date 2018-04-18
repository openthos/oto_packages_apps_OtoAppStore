package org.openthos.appstore.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DOWNLOAD_INFO = "downloadinfo";

    public SQLiteHelper(Context context) {
        super(context, "openthos", null, 4);
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
                + "downLoadSize VARCHAR, "
                + "isSuccess VARCHAR , "
                + "packageName VARCHAR, "
                + "iconUrl VARCHAR "
                + ")";
        db.execSQL(downloadsql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            onCreate(db);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
