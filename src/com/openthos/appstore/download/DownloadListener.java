package com.openthos.appstore.download;

import com.openthos.appstore.bean.DownloadInfo;

public interface DownloadListener {

    public void onStart(DownloadInfo downloadInfo);

    public void onProgress(DownloadInfo downloadInfo, boolean isSupportF);

    public void onStop(DownloadInfo downloadInfo, boolean isSupportFTP);

    public void onError(DownloadInfo downloadInfo, String error);

    public void onSuccess(DownloadInfo downloadInfo);
}
