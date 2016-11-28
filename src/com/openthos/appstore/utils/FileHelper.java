package com.openthos.appstore.utils;

import android.text.TextUtils;

import com.openthos.appstore.app.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

    public static String getDefaultPath() {
        creatDirFile(Constants.DOWNFILEPATH);
        return Constants.DOWNFILEPATH;
    }

    public static String getTempPath() {
        creatDirFile(Constants.TEMP_FILEPATH);
        return Constants.TEMP_FILEPATH;
    }

    public static String getCachePath() {
        creatDirFile(Constants.CACHE_DATA);
        return Constants.CACHE_DATA;
    }

    public static String getDefaultFile(String flieName) {
        if (flieName == null || "".equals(flieName)) {
            return null;
        }
        return getDefaultPath() + "/" + flieName;
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
    }

    public static void setDefaultPath(String path) {
        Constants.BASE_FILEPATH = path;
    }

    public static String getUserID() {
        return Constants.USER_ID;
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(getDefaultFile(fileName));
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public static String readFile(String fileName) {
        File file = new File(Constants.CACHE_DATA + "/" + fileName);
        if (!file.exists()) {
            return null;
        }
        StringBuilder buffer = new StringBuilder("");
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), "utf-8");
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!buffer.toString().equals("")) {
                    buffer.append("\r\n");
                }
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            return null;
        } finally {
            Tools.closeStream(reader);
        }
    }

    public static boolean writeFile(String fileName, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(getCachePath() + "/" + fileName, append);
            fileWriter.write(content);
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            Tools.closeStream(fileWriter);
        }
    }
}