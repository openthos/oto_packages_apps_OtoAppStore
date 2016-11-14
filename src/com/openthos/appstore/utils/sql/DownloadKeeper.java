package com.openthos.appstore.utils.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.openthos.appstore.bean.SQLDownLoadInfo;
import java.util.ArrayList;

public class DownloadKeeper {
    private SQLiteHelper mDbhelper;
    private SQLiteDatabase mDb;
    private int mDoSaveTimes = 0;

    public DownloadKeeper(Context context) {
        this.mDbhelper = new SQLiteHelper(context);
    }

    public void saveDownLoadInfo(SQLDownLoadInfo downloadInfo) {
        ContentValues cv = new ContentValues();
        cv.put("userID", downloadInfo.getUserID());
        cv.put("taskID", downloadInfo.getTaskID());
        cv.put("downLoadSize", downloadInfo.getDownloadSize());
        cv.put("fileName", downloadInfo.getFileName());
        cv.put("filePath", downloadInfo.getFilePath());
        cv.put("fileSize", downloadInfo.getFileSize());
        cv.put("url", downloadInfo.getUrl());
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
                saveDownLoadInfo(downloadInfo);
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

    public SQLDownLoadInfo getDownLoadInfo(String userID, String taskID) {
        SQLDownLoadInfo downloadinfo = null;
        mDb = mDbhelper.getWritableDatabase();
        Cursor cursor = mDb.rawQuery(
                "SELECT * from " + SQLiteHelper.DOWNLOAD_INFO
                        + "WHERE userID = ? AND taskID = ? ", new String[]{userID, taskID});
        if (cursor.moveToNext()) {
            downloadinfo = new SQLDownLoadInfo();
            downloadinfo.setDownloadSize(
                    cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadinfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downloadinfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
            downloadinfo.setUserID(cursor.getString(cursor.getColumnIndex("userID")));
        }
        cursor.close();
        mDb.close();
        return downloadinfo;
    }

    public ArrayList<SQLDownLoadInfo> getAllDownLoadInfo() {
        ArrayList<SQLDownLoadInfo> downloadinfoList = new ArrayList<SQLDownLoadInfo>();
        mDb = mDbhelper.getWritableDatabase();
        Cursor cursor = mDb.rawQuery(
                "SELECT * from " + SQLiteHelper.DOWNLOAD_INFO, null);
        while (cursor.moveToNext()) {
            SQLDownLoadInfo downloadinfo = new SQLDownLoadInfo();
            downloadinfo.setDownloadSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadinfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downloadinfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
            downloadinfo.setUserID(cursor.getString(cursor.getColumnIndex("userID")));
            downloadinfoList.add(downloadinfo);
        }
        cursor.close();
        mDb.close();
        return downloadinfoList;
    }

    public ArrayList<SQLDownLoadInfo> getUserDownLoadInfo(String userID) {
        ArrayList<SQLDownLoadInfo> downloadinfoList = new ArrayList<SQLDownLoadInfo>();
        mDb = mDbhelper.getWritableDatabase();
        try {
            Cursor cursor = null;
            cursor = mDb.rawQuery(
                    "SELECT * from " + SQLiteHelper.DOWNLOAD_INFO + " WHERE userID = '" +
                                                                          userID + "'", null);
            while (cursor.moveToNext()) {
                SQLDownLoadInfo downloadinfo = new SQLDownLoadInfo();
                downloadinfo.setDownloadSize(
                        cursor.getLong(cursor.getColumnIndex("downLoadSize")));
                downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
                downloadinfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
                downloadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                downloadinfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
                downloadinfoList.add(downloadinfo);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDb.close();
        return downloadinfoList;
    }

    public void deleteDownLoadInfo(String userID, String taskID) {
        mDb = mDbhelper.getWritableDatabase();
        mDb.delete(SQLiteHelper.DOWNLOAD_INFO, "userID = ? AND taskID = ? ",
                new String[]{userID, taskID});
        mDb.close();
    }

    public void deleteAllDownLoadInfo() {
        mDb = mDbhelper.getWritableDatabase();
        mDb.delete(SQLiteHelper.DOWNLOAD_INFO, null, null);
        mDb.close();
    }
}
