package com.openthos.appstore.bean;

public class TaskInfo {
    private boolean isOnDownloading;
    private String packageName;
    private String taskID;
    private String fileName;
    private long fileSize = 0;
    private long downFileSize = 0;
    private long speech;

    public boolean isOnDownloading() {
        return isOnDownloading;
    }

    public void setOnDownloading(boolean isOnDownloading) {
        this.isOnDownloading = isOnDownloading;
    }

    public int getProgress() {
        if (fileSize == 0) {
            return 0;
        } else {
            return ((int) (100 * downFileSize / fileSize));
        }
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        String type = null;
        if (fileName != null) {
            String name = fileName.toLowerCase();
            if (name.contains(".")) {
                type = name.substring(name.lastIndexOf("."), name.length());
            }
        }
        return type;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDownFileSize() {
        return downFileSize;
    }

    public void setDownFileSize(long downFileSize) {
        this.downFileSize = downFileSize;
    }

    public long getSpeech() {
        return speech;
    }

    public void setSpeech(long speech) {
        this.speech = speech;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
