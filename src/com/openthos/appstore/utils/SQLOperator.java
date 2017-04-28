package com.openthos.appstore.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.openthos.appstore.bean.DownloadInfo;

import java.util.ArrayList;

public class SQLOperator {
    private SQLiteHelper mDbhelper;
    private SQLiteDatabase mDb;
    private int mDoSaveTimes = 0;

    public SQLOperator(Context context) {
        this.mDbhelper = new SQLiteHelper(context);
    }

    public void saveDownloadInfo(DownloadInfo downloadInfo) {
        ContentValues cv = new ContentValues();
        cv.put("userID", downloadInfo.getUserID());
        cv.put("taskID", downloadInfo.getTaskID());
        cv.put("downLoadSize", downloadInfo.getDownloadSize());
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
                    new String[]{downloadInfo.getUserID(), downloadInfo.getTaskID()});
            if (cursor.moveToNext()) {
                mDb.update(SQLiteHelper.DOWNLOAD_INFO, cv, "userID = ? AND taskID = ? ",
                        new String[]{downloadInfo.getUserID(), downloadInfo.getTaskID()});
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

    public DownloadInfo getDownloadInfoByPkgName(String packageName) {
        DownloadInfo downloadinfo = null;
        mDb = mDbhelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + SQLiteHelper.DOWNLOAD_INFO
                        + " WHERE packageName=\"" + packageName + "\"", null);
        while (cursor.moveToNext()) {
            downloadinfo = new DownloadInfo();
            downloadinfo.setDownloadSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadinfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downloadinfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
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

    public ArrayList<DownloadInfo> getAllDownloadInfo() {
        ArrayList<DownloadInfo> downloadinfoList = new ArrayList<DownloadInfo>();
        mDb = mDbhelper.getWritableDatabase();
        Cursor cursor = mDb.rawQuery(
                "SELECT * from " + SQLiteHelper.DOWNLOAD_INFO, null);
        while (cursor.moveToNext()) {
            DownloadInfo downloadinfo = new DownloadInfo();
            downloadinfo.setDownloadSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadinfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downloadinfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
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

    public ArrayList<DownloadInfo> getUserDownloadInfo(String userID) {
        ArrayList<DownloadInfo> downloadinfoList = new ArrayList<DownloadInfo>();
        mDb = mDbhelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mDb.rawQuery(
                    "SELECT * from " + SQLiteHelper.DOWNLOAD_INFO + " WHERE userID = '" +
                                                                          userID + "'", null);
            while (cursor.moveToNext()) {
                DownloadInfo downloadinfo = new DownloadInfo();
                downloadinfo.setDownloadSize(
                        cursor.getLong(cursor.getColumnIndex("downLoadSize")));
                downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
                downloadinfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
                downloadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                downloadinfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
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
