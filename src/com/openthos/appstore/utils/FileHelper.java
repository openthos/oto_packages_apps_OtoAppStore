package com.openthos.appstore.utils;

import com.openthos.appstore.app.Constants;

import java.io.File;
import java.io.IOException;

public class FileHelper {
    private static File getFile(String filePath) {
        return new File(filePath);
    }

    public static boolean creatFile(String filePath) {
        String dirpath = filePath.substring(0, filePath.lastIndexOf("/"));
        if (dirpath != null) {
            creatDirFile(dirpath);
            if (!getFile(filePath).exists()) {
                try {
                    getFile(filePath).createNewFile();
                    return true;
                } catch (IOException e) {
                    Tools.printLog("FH", "FH " + filePath + " " + e.toString());
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public static void creatDirFile(String dirPath) {
        if (!getFile(dirPath).exists()) {
            getFile(dirPath).mkdirs();
        }
    }

    public static String getDefaultPath() {
        creatDirFile(Constants.DOWNFILEPATH);
        return Constants.DOWNFILEPATH;
    }

    public static String getTempPath() {
        creatDirFile(Constants.TEMP_FILEPATH);
        return Constants.TEMP_FILEPATH;
    }

    public static String getDefaultFile(String flieName) {
        if (flieName == null || "".equals(flieName)) {
            return null;
        }
        creatFile(getDefaultPath() + "/" + flieName);
        return getDefaultPath() + "/" + flieName;
    }

    public static String getTempFile(String flieName) {
        if (flieName == null || "".equals(flieName)) {
            return null;
        }
        creatFile(getTempPath() + "/" + flieName);
        return getTempPath() + "/" + flieName;
    }

    public static String getDefaultFileFromUrl(String url) {
        return getDefaultFile(getNameFromUrl(url));
    }

    public static String getNameFromUrl(String downloadUrl) {
        if (downloadUrl == null) {
            return null;
        }
        return downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length());
    }

    public static void setUserID(String newUserID) {
        Constants.USER_ID = newUserID;
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(getDefaultFile(fileName));
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }
}