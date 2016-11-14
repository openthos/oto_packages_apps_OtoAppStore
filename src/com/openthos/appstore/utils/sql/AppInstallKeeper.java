package com.openthos.appstore.utils.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.openthos.appstore.bean.SQLAppInstallInfo;

import java.util.ArrayList;

public class AppInstallKeeper {
    private SQLiteHelper mDbhelper;
    private SQLiteDatabase mDb;
    private int mDoSaveTimes = 0;

    public AppInstallKeeper(Context context) {
        mDbhelper = new SQLiteHelper(context);
    }

    public void saveAppInstallInfo(SQLAppInstallInfo appInstallInfo) {
        ContentValues cv = new ContentValues();
        cv.put("id", appInstallInfo.getId());
        cv.put("name", appInstallInfo.getId());
        cv.put("packageName", appInstallInfo.getPackageName());
        cv.put("versionName", appInstallInfo.getVersionName());
        cv.put("versionCode", appInstallInfo.getVersionCode());
        Cursor cursor = null;
        try {
            mDb = mDbhelper.getWritableDatabase();
            cursor = mDb.rawQuery("SELECT * from " + SQLiteHelper.APP_INSTALL
                            + " WHERE packageName = ? AND versionCode = ? ",
                    new String[]{appInstallInfo.getPackageName(),
                            appInstallInfo.getVersionCode()});
            if (cursor.moveToNext()) {
                mDb.update(SQLiteHelper.APP_INSTALL, cv,
                        "packageName = ? AND versionCode = ?",
                        new String[]{appInstallInfo.getPackageName(),
                                appInstallInfo.getVersionCode()});
            } else {
                mDb.insert(SQLiteHelper.APP_INSTALL, null, cv);
            }
            cursor.close();
            mDb.close();
        } catch (Exception e) {
            mDoSaveTimes++;
            if (mDoSaveTimes < 5) {
                saveAppInstallInfo(appInstallInfo);
            } else {
                mDoSaveTimes = 0;
            }
            if (cursor != null) {
                cursor.close();
            }
            if (mDb != null) {
                mDb.close();
            }
        }
        mDoSaveTimes = 0;
    }

    public SQLAppInstallInfo getAppInstallInfo(String packageName, String versionCode) {
        SQLAppInstallInfo appInstallInfo = null;
        mDb = mDbhelper.getWritableDatabase();
        Cursor cursor = mDb.rawQuery(
                "SELECT * from " + SQLiteHelper.APP_INSTALL
                        + "WHERE packageName = ? AND versionCode = ? ",
                new String[]{packageName, versionCode});
        if (cursor.moveToNext()) {
            appInstallInfo = new SQLAppInstallInfo();
            appInstallInfo.setId(cursor.getLong(cursor.getColumnIndex("id")));
            appInstallInfo.setPackageName(
                    cursor.getString(cursor.getColumnIndex("packageName")));
            appInstallInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
            appInstallInfo.setVersionCode(
                    cursor.getString(cursor.getColumnIndex("versionCode")));
            appInstallInfo.setVersionName(
                    cursor.getString(cursor.getColumnIndex("versionName")));
        }
        cursor.close();
        mDb.close();
        return appInstallInfo;
    }

    public ArrayList<SQLAppInstallInfo> getAllAppInstallInfo() {
        ArrayList<SQLAppInstallInfo> downloadinfoList =
                new ArrayList<SQLAppInstallInfo>();
        mDb = mDbhelper.getWritableDatabase();
        Cursor cursor = mDb.rawQuery(
                "SELECT * from " + SQLiteHelper.APP_INSTALL, null);
        while (cursor.moveToNext()) {
            SQLAppInstallInfo appInstallInfo = new SQLAppInstallInfo();
            appInstallInfo.setId(cursor.getLong(cursor.getColumnIndex("id")));
            appInstallInfo.setPackageName(
                    cursor.getString(cursor.getColumnIndex("packageName")));
            appInstallInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
            appInstallInfo.setVersionCode(
                    cursor.getString(cursor.getColumnIndex("versionCode")));
            appInstallInfo.setVersionName(
                    cursor.getString(cursor.getColumnIndex("versionName")));
            downloadinfoList.add(appInstallInfo);
        }
        cursor.close();
        mDb.close();
        return downloadinfoList;

    }

    public ArrayList<SQLAppInstallInfo> getUserAppInstallInfo(String packageName) {
        ArrayList<SQLAppInstallInfo> downloadinfoList =
                new ArrayList<SQLAppInstallInfo>();
        mDb = mDbhelper.getWritableDatabase();
        try {
            Cursor cursor = null;
            cursor = mDb.rawQuery(
                    "SELECT * from " + SQLiteHelper.APP_INSTALL +
                            " WHERE packageName = '" + packageName + "'", null);
            while (cursor.moveToNext()) {
                SQLAppInstallInfo appInstallInfo = new SQLAppInstallInfo();
                appInstallInfo.setId(cursor.getLong(cursor.getColumnIndex("id")));
                appInstallInfo.setPackageName(
                        cursor.getString(cursor.getColumnIndex("packageName")));
                appInstallInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
                appInstallInfo.setVersionCode(
                        cursor.getString(cursor.getColumnIndex("versionCode")));
                appInstallInfo.setVersionName(
                        cursor.getString(cursor.getColumnIndex("versionName")));
                downloadinfoList.add(appInstallInfo);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDb.close();
        return downloadinfoList;
    }

    public void deleteAppInstallInfo(String packageName, String versionCode) {
        mDb = mDbhelper.getWritableDatabase();
        mDb.delete(SQLiteHelper.APP_INSTALL, "packageName = ? AND versionCode = ? ",
                new String[]{packageName, versionCode});
        mDb.close();
    }

    public void deleteAllAppInstallInfo() {
        mDb = mDbhelper.getWritableDatabase();
        mDb.delete(SQLiteHelper.APP_INSTALL, null, null);
        mDb.close();
    }
}
