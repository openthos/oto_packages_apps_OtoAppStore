package org.openthos.appstore.download;

import org.openthos.appstore.bean.AppItemInfo;

public interface DownloadListener {

    public void onStart(AppItemInfo downloadInfo);

    public void onProgress(AppItemInfo downloadInfo, boolean isSupportF);

    public void onStop(AppItemInfo downloadInfo, boolean isSupportFTP);

    public void onError(AppItemInfo downloadInfo, String error);

    public void onSuccess(AppItemInfo downloadInfo);
}
