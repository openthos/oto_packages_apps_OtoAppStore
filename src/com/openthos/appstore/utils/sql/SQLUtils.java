package com.openthos.appstore.utils.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by luojunhuan on 16-11-2.
 */
public class SQLUtils {
    Context mContext;
    SQLiteHelper mDbhelper;
    public SQLiteDatabase mSQLiteDatabase;

    public SQLUtils(Context context) {
        mContext = context;
    }

    public void opendb(Context context) {
        mDbhelper = new SQLiteHelper(context);
        mSQLiteDatabase = mDbhelper.getWritableDatabase();
    }

    public void closedb(Context context) {
        if (mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase.close();
        }
    }

    public void insert(String table_name, ContentValues values) {
        opendb(mContext);
        mSQLiteDatabase.insert(table_name, null, values);
        closedb(mContext);
    }

    public int updatatable(String table_name, ContentValues values, int ID) {
        opendb(mContext);
        return mSQLiteDatabase.update(table_name, values, " Type_ID = ? ",
                new String[]{String.valueOf(ID)});
    }

    public void delete(String table_name) {
        opendb(mContext);
        try {
            mSQLiteDatabase.delete(table_name, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closedb(mContext);
        }
    }
}