package com.openthos.appstore.utils.download;

import com.openthos.appstore.bean.SQLDownLoadInfo;

public interface DownLoadListener {

    public void onStart(SQLDownLoadInfo sqlDownLoadInfo);

    public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint);

    public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint);

    public void onError(SQLDownLoadInfo sqlDownLoadInfo);

    public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo);
}
