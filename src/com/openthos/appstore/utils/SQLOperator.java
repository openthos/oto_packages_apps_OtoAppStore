package com.openthos.appstore.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.openthos.appstore.bean.AppItemInfo;

import java.util.ArrayList;

public class SQLOperator {
    private SQLiteHelper mDbhelper;
    private SQLiteDatabase mDb;
    private int mDoSaveTimes = 0;

    public SQLOperator(Context context) {
        this.mDbhelper = new SQLiteHelper(context);
    }

    public void saveDownloadInfo(AppItemInfo downloadInfo) {
        ContentValues cv = new ContentValues();
        cv.put("userID", downloadInfo.getUserID());
        cv.put("taskID", downloadInfo.getTaskId());
        cv.put("downLoadSize", downloadInfo.getDownFileSize());
        cv.put("fileName", downloadInfo.getFileName());
        cv.put("filePath", downloadInfo.getFilePath());
        cv.put("fileSize", downloadInfo.getFileSize());
        cv.put("url", downloadInfo.getUrl());
        cv.put("packageName", downloadInfo.getPackageName());
        cv.put("isSuccess", downloadInfo.isSuccess() + "");
        cv.put("iconUrl", downloadInfo.getIconUrl());
        Cursor cursor = null;
        try {
            mDb = mDbhelper.getWritableDatabase();
            cursor = mDb.rawQuery(
                    "SELECT * from " + SQLiteHelper.DOWNLOAD_INFO
                            + " WHERE userID = ? AND taskID = ? ",
                    new String[]{downloadInfo.getUserID(), downloadInfo.getTaskId()});
            if (cursor.moveToNext()) {
                mDb.update(SQLiteHelper.DOWNLOAD_INFO, cv, "userID = ? AND taskID = ? ",
                        new String[]{downloadInfo.getUserID(), downloadInfo.getTaskId()});
            } else {
                mDb.insert(SQLiteHelper.DOWNLOAD_INFO, null, cv);
            }
            cursor.close();
            mDb.close();
        } catch (Exception e) {
            mDoSaveTimes++;
            if (mDoSaveTimes < 5) {
                saveDownloadInfo(downloadInfo);
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

    public AppItemInfo getDownloadInfoByPkgName(String packageName) {
        AppItemInfo downloadinfo = null;
        mDb = mDbhelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + SQLiteHelper.DOWNLOAD_INFO
                        + " WHERE packageName=\"" + packageName + "\"", null);
        while (cursor.moveToNext()) {
            downloadinfo = new AppItemInfo();
            downloadinfo.setDownFileSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadinfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downloadinfo.setTaskId(cursor.getString(cursor.getColumnIndex("packageName")));
            downloadinfo.setPackageName(cursor.getString(cursor.getColumnIndex("packageName")));
            downloadinfo.setUserID(cursor.getString(cursor.getColumnIndex("userID")));
            downloadinfo.setIsSuccess("true".
                            equals(cursor.getString(cursor.getColumnIndex("isSuccess"))));
            downloadinfo.setIconUrl(cursor.getString(cursor.getColumnIndex("iconUrl")));
        }
        cursor.close();
        mDb.close();
        return downloadinfo;
    }

    public ArrayList<AppItemInfo> getAllDownloadInfo() {
        ArrayList<AppItemInfo> downloadinfoList = new ArrayList<AppItemInfo>();
        mDb = mDbhelper.getWritableDatabase();
        Cursor cursor = mDb.rawQuery(
                "SELECT * from " + SQLiteHelper.DOWNLOAD_INFO, null);
        while (cursor.moveToNext()) {
            AppItemInfo downloadinfo = new AppItemInfo();
            downloadinfo.setDownFileSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadinfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downloadinfo.setTaskId(cursor.getString(cursor.getColumnIndex("packageName")));
            downloadinfo.setPackageName(cursor.getString(cursor.getColumnIndex("packageName")));
            downloadinfo.setUserID(cursor.getString(cursor.getColumnIndex("userID")));
            downloadinfo.setIsSuccess("true".
                    equals(cursor.getString(cursor.getColumnIndex("isSuccess"))));
            downloadinfo.setIconUrl(cursor.getString(cursor.getColumnIndex("iconUrl")));
            downloadinfoList.add(downloadinfo);
        }
        cursor.close();
        mDb.close();
        return downloadinfoList;
    }

    public ArrayList<AppItemInfo> getUserDownloadInfo(String userID) {
        ArrayList<AppItemInfo> downloadinfoList = new ArrayList<>();
        mDb = mDbhelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mDb.rawQuery(
                    "SELECT * from " + SQLiteHelper.DOWNLOAD_INFO + " WHERE userID = '" +
                                                                          userID + "'", null);
            while (cursor.moveToNext()) {
                AppItemInfo downloadinfo = new AppItemInfo();
                downloadinfo.setDownFileSize(
                        cursor.getLong(cursor.getColumnIndex("downLoadSize")));
                downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
                downloadinfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
                downloadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                downloadinfo.setTaskId(cursor.getString(cursor.getColumnIndex("packageName")));
                downloadinfo.setPackageName(cursor.getString(cursor.getColumnIndex("packageName")));
                downloadinfo.setUserID(cursor.getString(cursor.getColumnIndex("userID")));
                downloadinfo.setIsSuccess("true".
                        equals(cursor.getString(cursor.getColumnIndex("isSuccess"))));
                downloadinfo.setIconUrl(cursor.getString(cursor.getColumnIndex("iconUrl")));
                downloadinfoList.add(downloadinfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        mDb.close();
        return downloadinfoList;
    }

    public void deleteDownloadInfo(String userID, String taskID) {
        mDb = mDbhelper.getWritableDatabase();
        mDb.delete(SQLiteHelper.DOWNLOAD_INFO, "userID = ? AND taskID = ? ",
                new String[]{userID, taskID});
        mDb.close();
    }
}
