package com.openthos.appstore.utils.sql;

import com.openthos.appstore.app.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileHelper {
    public void newFile(File f) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void newDirFile(File f) {
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static double getSize(List<String> willupload) {
        return (double) getSizeUnitByte(willupload) / (1024 * 1024);
    }

    public static long getSizeUnitByte(List<String> willupload) {
        long allfilesize = 0;
        for (int i = 0; i < willupload.size(); i++) {
            File newfile = new File(willupload.get(i));
            if (newfile.exists() && newfile.isFile()) {
                allfilesize = allfilesize + newfile.length();
            }
        }
        return allfilesize;
    }

    public static String getFileDefaultPath() {
        newDirFile(new File(Constants.DOWNFILEPATH));
        return Constants.DOWNFILEPATH;
    }

    public static String getTempDirPath() {
        newDirFile(new File(Constants.TEMP_FILEPATH));
        return Constants.TEMP_FILEPATH;
    }

    public static String getTempFilePath(String flieName) {
        if (flieName == null || "".equals(flieName)) {
            return null;
        }
        return getTempDirPath() + "/" + flieName;
    }

    public static boolean copyFile(String oldPath, String newPath) {
        boolean iscopy = false;
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                inStream = new FileInputStream(oldPath);
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                iscopy = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return iscopy;
    }

    public static void setUserID(String newUserID) {
        Constants.USER_ID = newUserID;
        Constants.DOWNFILEPATH = Constants.BASE_FILEPATH + "/" + Constants.USER_ID + "/FILETEMP";
        Constants.TEMP_FILEPATH = Constants.BASE_FILEPATH + "/" + Constants.USER_ID + "/TEMPDir";
    }

    public static String getUserID() {
        return Constants.USER_ID;
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(getTempFilePath(fileName));
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }
}
