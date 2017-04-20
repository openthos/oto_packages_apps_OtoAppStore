package com.openthos.appstore.utils;

import android.text.TextUtils;

import com.openthos.appstore.app.Constants;

import java.io.File;
import java.io.IOException;

public class FileHelper {
    public static File getFile(String filePath){
        return new File(filePath);
    }

    public static void creatDirFile(String dirPath) {
        if (!TextUtils.isEmpty(dirPath)) {
            creatDirFile(getFile(dirPath));
        }
    }

    public static void creatDirFile(File dirFile) {
        if (dirFile != null && !dirFile.exists()) {
            dirFile.mkdirs();
        }
    }

    public static void creatFile(String filePath) throws IOException {
        if (TextUtils.isEmpty(filePath)) {
            creatFile(getFile(filePath));
        }
    }

    public static void creatFile(File file) throws IOException {
        if (file != null && !file.exists()) {
            creatDirFile(file.getParentFile());
            file.createNewFile();
        }
    }

    public static String getDownloadDir() {
        return Constants.BASE_FILEPATH + "/app";
    }

    public static String getDownloadUrlPath(String downloadUrl) {
        return getDownloadDir() + "/" + getNameFromUrl(downloadUrl);
    }

    public static File getDownloadUrlFile(String downloadUrl) {
        return getFile(getDownloadUrlPath(downloadUrl));
    }

    public static String getDownloadTempPath(String fileName) {
        return getDownloadDir() + "/.temp/" + fileName + ".temp";
    }

    public static File getDownloadTempFile(String fileName) {
        return getFile(getDownloadTempPath(fileName));
    }

    public static String getNameFromUrl(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return null;
        }
        return downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length());
    }

    public static void deleteFile(String filePath) {
        deleteFile(getFile(filePath));
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}
