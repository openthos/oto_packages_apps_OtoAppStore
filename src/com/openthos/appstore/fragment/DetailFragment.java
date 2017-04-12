package com.openthos.appstore.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.bean.DownloadInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.download.DownloadListener;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.download.DownloadService;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.ImageCache;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.SQLOperator;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.view.BannerView;
import com.openthos.appstore.view.CustomRatingBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends BaseFragment implements View.OnClickListener {
    private ImageView mIcon;
    private Button mDownload;
    private TextView mAppName;
    private CustomRatingBar mRatingBar;
    private TextView mContent;
    private TextView mPromulgator;
    private TextView mType;
    private TextView mSize;
    private BannerView mBannerView;
    private ProgressBar mProgressBar;
    private TextView mStarNum;
    private AppItemInfo mAppItemInfo;
    private DownloadManager mManager;
    private String mBtText;
    private String mContinues;
    private String mPause;
    private String mInstalls;
    private String mUpdate;
    private String mFinished;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mManager = DownloadService.getDownloadManager();
        mManager.setAllTaskListener(new DetailDownloadListener());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_detail;
    }

    private void initFragment() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        CommentFragment commentFragment = new CommentFragment();
        commentFragment.setDatas(Constants.getComment());
        commentFragment.setAll(false);
        transaction.replace(R.id.fragment_detail_comment, commentFragment);
        transaction.commit();
    }

    @Override
    public void initData() {
        mContinues = getActivity().getResources().getString(R.string.continues);
        mPause = getActivity().getResources().getString(R.string.pause);
        mInstalls = getActivity().getResources().getString(R.string.install);
        mUpdate = getActivity().getResources().getString(R.string.update);
        mFinished = getActivity().getResources().getString(R.string.finished);

        mBannerView.setImageUrls(Constants.getString());
        loadData();
        initFragment();
    }

    @Override
    public void getHandlerMessage(Message message) {
    }

    @Override
    public void initView(View view) {
        mIcon = (ImageView) view.findViewById(R.id.fragment_detail_appIcon);
        mDownload = (Button) view.findViewById(R.id.fragment_detail_download);
        mAppName = (TextView) view.findViewById(R.id.fragment_detail_appName);
        mRatingBar = (CustomRatingBar) view.findViewById(R.id.fragment_detail_ratingBar);
        mStarNum = (TextView) view.findViewById(R.id.fragment_detail_starNum);
        mContent = (TextView) view.findViewById(R.id.fragment_detail_instruction);
        mBannerView = (BannerView) view.findViewById(R.id.fragment_detail_carsousel);
        mPromulgator = (TextView) view.findViewById(R.id.fragment_detail_promulgator);
        mType = (TextView) view.findViewById(R.id.fragment_detail_type);
        mSize = (TextView) view.findViewById(R.id.fragment_detail_size);
        mProgressBar = ((ProgressBar) view.findViewById(R.id.fragment_detail_progressbar));

        mDownload.setOnClickListener(this);
    }

    @Override
    public void setData(Object data) {
        if (data != null) {
            mAppItemInfo = (AppItemInfo) data;
        }
    }

    @Override
    public void onClick(View view) {
        if (mAppItemInfo != null) {
            mBtText = mDownload.getText().toString();
            if (mBtText.equals(mContinues)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownload.setText(mPause);
                SPUtils.saveDownloadState(getActivity(),
                        mAppItemInfo.getPackageName(), Constants.APP_DOWNLOAD_CONTINUE);
                MainActivity.mDownloadService.startTask(mAppItemInfo.getTaskId() + "");
            } else if (mBtText.equals(mPause)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownload.setText(mContinues);
                SPUtils.saveDownloadState(getActivity(),
                        mAppItemInfo.getPackageName(), Constants.APP_DOWNLOAD_PAUSE);
                MainActivity.mDownloadService.stopTask(mAppItemInfo.getTaskId() + "");
            } else if (mBtText.equals(mInstalls)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownload.setText(mPause);
                SPUtils.saveDownloadState(getActivity(),
                        mAppItemInfo.getPackageName(), Constants.APP_DOWNLOAD_CONTINUE);
                MainActivity.mDownloadService.addTask(mAppItemInfo.getTaskId() + "",
                        StoreApplication.mBaseUrl + "/" + mAppItemInfo.getDownloadUrl(),
                        FileHelper.getNameFromUrl(mAppItemInfo.getDownloadUrl()),
                        mAppItemInfo.getPackageName(),
                        mAppItemInfo.getIconUrl());
            } else if (mBtText.equals(mUpdate)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownload.setText(mContinues);
                SPUtils.saveDownloadState(getActivity(),
                        mAppItemInfo.getPackageName(), Constants.APP_DOWNLOAD_CONTINUE);
                MainActivity.mDownloadService.addTask(mAppItemInfo.getTaskId() + "",
                        StoreApplication.mBaseUrl + "/" + mAppItemInfo.getDownloadUrl(),
                        FileHelper.getNameFromUrl(mAppItemInfo.getDownloadUrl()),
                        mAppItemInfo.getPackageName(),
                        mAppItemInfo.getIconUrl());
            } else if (mBtText.equals(mFinished)) {
                File file = FileHelper.getDownloadUrlFile(mAppItemInfo.getDownloadUrl());
                MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                        obtainMessage(Constants.INSTALL_APK, file.getAbsolutePath()));
                if (!file.exists() || file.length() == 0) {
                    SPUtils.saveDownloadState(getActivity(),
                            mAppItemInfo.getPackageName(), Constants.APP_NOT_INSTALL);
                    setContent(mDownload, R.string.install,
                            R.drawable.shape_button_white_cyan, R.color.button_cyan);
                }
            }
        }
    }

    @Override
    public void refresh() {
        initData();
    }

    private void loadData() {
        if (mAppItemInfo != null) {
            ImageCache.loadImage(mIcon, mAppItemInfo.getIconUrl());
            mAppName.setText(mAppItemInfo.getAppName());
            mRatingBar.setRating(mAppItemInfo.getStar());
            mStarNum.setText(mAppItemInfo.getStar() + "");
            mContent.setText(mAppItemInfo.getDescrible());
            mPromulgator.setText(getActivity().getString(
                    R.string.promulgator) + mAppItemInfo.getCompany());
            mType.setText(getActivity().getString(R.string.type) + mAppItemInfo.getType());
            mSize.setText(getActivity().getString(R.string.size) +
                                        Tools.transformFileSize(mAppItemInfo.getFileSize()));
            initStateAndProgress();
            switch (mAppItemInfo.getState()) {
                case Constants.APP_NOT_INSTALL:
                    mProgressBar.setVisibility(View.GONE);
                    setContent(mDownload, R.string.install,
                            R.drawable.shape_button_white_cyan, R.color.button_cyan);
                    break;
                case Constants.APP_HAVE_INSTALLED:
                    mProgressBar.setVisibility(View.GONE);
                    setContent(mDownload, R.string.have_installed,
                            R.drawable.shape_button_white_gray, R.color.button_gray);
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(mAppItemInfo.getProgress());
                    setContent(mDownload, R.string.pause, 0, R.color.button_cyan);
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(mAppItemInfo.getProgress());
                    setContent(mDownload, R.string.continues, 0, R.color.button_cyan);
                    break;
                case Constants.APP_NEED_UPDATE:
                    mProgressBar.setVisibility(View.GONE);
                    setContent(mDownload, R.string.update, R.drawable.shape_button_white_cyan,
                            R.color.button_cyan);
                    break;
                case Constants.APP_DOWNLOAD_FINISHED:
                    mProgressBar.setVisibility(View.GONE);
                    setContent(mDownload, R.string.finished,
                            R.drawable.shape_button_white_cyan, R.color.button_cyan);
                    break;
                default:
                    break;
            }
        }
    }

    public void initStateAndProgress() {
        if (mAppItemInfo != null) {
            List<AppInstallInfo> mAppPackageInfo = MainActivity.mAppPackageInfo;
            for (int i = 0; i < mAppPackageInfo.size(); i++) {
                AppInstallInfo appInstallInfo = mAppPackageInfo.get(i);
                if (appInstallInfo.getPackageName().equals(mAppItemInfo.getPackageName())) {
                    if (appInstallInfo.getVersionCode() < mAppItemInfo.getVersionCode()) {
                        mAppItemInfo.setState(Constants.APP_NEED_UPDATE);
                    } else {
                        mAppItemInfo.setState(Constants.APP_HAVE_INSTALLED);
                    }
                } else {
                    mAppItemInfo.setState(Constants.APP_NOT_INSTALL);
                }
            }

            DownloadInfo downloadInfo = new SQLOperator(getActivity()).
                    getDownloadInfoByPkgName(mAppItemInfo.getPackageName());
            if (downloadInfo != null) {
                long downloadSize = downloadInfo.getDownloadSize();
                long fileSize = downloadInfo.getFileSize();
                if (fileSize == 0) {
                    mAppItemInfo.setProgress(0);
                } else if (downloadSize < fileSize) {
                    mAppItemInfo.setProgress(downloadInfo.getProgress());
                    mAppItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                } else if (downloadSize == fileSize) {
                    switch (mAppItemInfo.getState()) {
                        case Constants.APP_HAVE_INSTALLED:
                        case Constants.APP_NEED_UPDATE:
                            break;
                        default:
                            mAppItemInfo.setProgress(100);
                            mAppItemInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
                            break;
                    }
                }
            }

            ArrayList<TaskInfo> allTask = mManager.getAllTask();
            for (int i = 0; i < allTask.size(); i++) {
                TaskInfo taskInfo = allTask.get(i);
                if (mAppItemInfo.getTaskId().equals(taskInfo.getTaskID())) {
                    if (taskInfo.isOnDownloading()) {
                        mAppItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                        mAppItemInfo.setProgress(taskInfo.getProgress());
                    }
                }
            }
        }
    }

    private void setContent(Button btn, int text, int background, int color) {
        btn.setText(text);
        if (background != 0) {
            btn.setBackgroundResource(background);
        } else {
            btn.setBackground(null);
        }
        btn.setTextColor(getActivity().getResources().getColor(color));
    }

    private class DetailDownloadListener implements DownloadListener {
        @Override
        public void onStart(DownloadInfo downloadInfo) {
            if (mAppItemInfo.getTaskId().equals(downloadInfo.getTaskID())) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(downloadInfo.getProgress());
                setContent(mDownload, R.string.pause, 0, R.color.button_cyan);
            }
        }

        @Override
        public void onProgress(DownloadInfo downloadInfo, boolean isSupportFTP) {
            if (mAppItemInfo.getTaskId().equals(downloadInfo.getTaskID())) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(downloadInfo.getProgress());
                setContent(mDownload, R.string.pause, 0, R.color.button_cyan);
            }
        }

        @Override
        public void onStop(DownloadInfo downloadInfo, boolean isSupportFTP) {
            if (mAppItemInfo.getTaskId().equals(downloadInfo.getTaskID())) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(downloadInfo.getProgress());
                setContent(mDownload, R.string.continues, 0, R.color.button_cyan);
            }
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String error) {
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(DownloadInfo downloadInfo) {
            if (mAppItemInfo.getTaskId().equals(downloadInfo.getTaskID())) {
                mProgressBar.setVisibility(View.GONE);
                setContent(mDownload, R.string.finished,
                        R.drawable.shape_button_white_cyan, R.color.button_cyan);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mBannerView != null) {
            if (hidden) {
                mBannerView.removeCallbacksAndMessages();
            } else {
                mBannerView.startPlay();
            }
        }
    }
}
