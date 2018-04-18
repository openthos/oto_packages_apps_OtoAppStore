package org.openthos.appstore.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openthos.appstore.MainActivity;
import org.openthos.appstore.R;
import org.openthos.appstore.app.Constants;
import org.openthos.appstore.app.StoreApplication;
import org.openthos.appstore.bean.AppInstallInfo;
import org.openthos.appstore.bean.AppItemInfo;
import org.openthos.appstore.download.DownloadListener;
import org.openthos.appstore.download.DownloadManager;
import org.openthos.appstore.download.DownloadService;
import org.openthos.appstore.utils.AppUtils;
import org.openthos.appstore.utils.FileHelper;
import org.openthos.appstore.utils.ImageCache;
import org.openthos.appstore.utils.NetUtils;
import org.openthos.appstore.utils.SQLOperator;
import org.openthos.appstore.utils.Tools;
import org.openthos.appstore.view.BannerView;
import org.openthos.appstore.view.CustomRatingBar;

import java.io.File;
import java.util.ArrayList;

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

    public DetailFragment() {
        super();
    }

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
        if (mAppItemInfo.getState() == Constants.APP_DOWNLOAD_FINISHED) {
            File file = FileHelper.getDownloadUrlFile(mAppItemInfo.getDownloadUrl());
            mAppItemInfo.setFilePath(FileHelper.
                getDownloadUrlFile(mAppItemInfo.getDownloadUrl()).getAbsolutePath());
            MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                    obtainMessage(Constants.INSTALL_APK, mAppItemInfo));
            if (!file.exists() || file.length() == 0) {
                setContent(mDownload, R.string.download,
                        R.drawable.shape_button_white_cyan, R.color.button_cyan);
            }

        } else if (mAppItemInfo.getState() == Constants.APP_HAVE_INSTALLED) {
            AppUtils.openApp(getActivity(), mAppItemInfo.getPackageName());
        } else if (NetUtils.isConnected(getActivity())) {
            switch (mAppItemInfo.getState()) {
                case Constants.APP_NOT_INSTALL:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mDownload.setText(getString(R.string.pause));
                    MainActivity.mDownloadService.addTask(mAppItemInfo.getTaskId() + "",
                            StoreApplication.mBaseUrl + "/" + mAppItemInfo.getDownloadUrl(),
                            mAppItemInfo.getAppName(),
                            mAppItemInfo.getPackageName(),
                            mAppItemInfo.getIconUrl());
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mDownload.setText(getString(R.string.continues));
                    MainActivity.mDownloadService.stopTask(mAppItemInfo.getTaskId() + "");
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mDownload.setText(getString(R.string.pause));
                    MainActivity.mDownloadService.startTask(mAppItemInfo.getTaskId() + "");
                    break;
                case Constants.APP_NEED_UPDATE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mDownload.setText(getString(R.string.pause));
                    MainActivity.mDownloadService.addTask(mAppItemInfo.getTaskId() + "",
                            StoreApplication.mBaseUrl + "/" + mAppItemInfo.getDownloadUrl(),
                            mAppItemInfo.getAppName(),
                            mAppItemInfo.getPackageName(),
                            mAppItemInfo.getIconUrl());
                    break;
            }
        } else {
            Tools.toast(getActivity(), getString(R.string.check_net_state));
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
                    setContent(mDownload, R.string.download,
                            R.drawable.shape_button_white_cyan, R.color.button_cyan);
                    break;
                case Constants.APP_HAVE_INSTALLED:
                    mProgressBar.setVisibility(View.GONE);
                    setContent(mDownload, R.string.open,
                            R.drawable.shape_button_white_cyan, R.color.button_cyan);
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(mAppItemInfo.getSqlProgress());
                    setContent(mDownload, R.string.pause, 0, R.color.white);
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(mAppItemInfo.getSqlProgress());
                    setContent(mDownload, R.string.continues, 0, R.color.white);
                    break;
                case Constants.APP_NEED_UPDATE:
                    mProgressBar.setVisibility(View.GONE);
                    setContent(mDownload, R.string.update, R.drawable.shape_button_white_cyan,
                            R.color.button_cyan);
                    break;
                case Constants.APP_DOWNLOAD_FINISHED:
                    mProgressBar.setVisibility(View.GONE);
                    setContent(mDownload, R.string.install,
                            R.drawable.shape_button_white_cyan, R.color.button_cyan);
                    break;
                default:
                    break;
            }
        }
    }

    public void initStateAndProgress() {
        if (mAppItemInfo != null) {
            AppInstallInfo appInstallInfo = mAppInstallMap.get(mAppItemInfo.getPackageName());
            if (appInstallInfo != null) {
                if (appInstallInfo.getVersionCode() < mAppItemInfo.getVersionCode()) {
                    mAppItemInfo.setState(Constants.APP_NEED_UPDATE);
                } else {
                    mAppItemInfo.setState(Constants.APP_HAVE_INSTALLED);
                }
            } else {
                mAppItemInfo.setState(Constants.APP_NOT_INSTALL);
            }

            AppItemInfo downloadInfo = new SQLOperator(getActivity()).
                    getDownloadInfoByPkgName(mAppItemInfo.getPackageName());
            if (downloadInfo != null) {
                long downloadSize = downloadInfo.getDownFileSize();
                long fileSize = downloadInfo.getFileSize();
                if (fileSize == 0) {
                    mAppItemInfo.setProgress(0);
                } else if (downloadSize < fileSize) {
                    mAppItemInfo.setProgress(downloadInfo.getProgress());
                    mAppItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                } else if (downloadSize == fileSize) {
                    switch (mAppItemInfo.getState()) {
                        case Constants.APP_HAVE_INSTALLED:
                            break;
                        default:
                            mAppItemInfo.setProgress(100);
                            mAppItemInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
                            break;
                    }
                }
            }

            ArrayList<AppItemInfo> allTask = mManager.getAllInfo();
            for (int i = 0; i < allTask.size(); i++) {
                AppItemInfo appInfo = allTask.get(i);
                if (mAppItemInfo.getTaskId().equals(appInfo.getTaskId())) {
                    if (appInfo.isOnDownloading()) {
                        mAppItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                        mAppItemInfo.setProgress(appInfo.getProgress());
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
        if (getActivity() != null) {
            btn.setTextColor(getActivity().getResources().getColor(color));
        }
    }

    private class DetailDownloadListener implements DownloadListener {
        @Override
        public void onStart(AppItemInfo downloadInfo) {
            if (mAppItemInfo.getTaskId().equals(downloadInfo.getTaskId())) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(downloadInfo.getProgress());
                mAppItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                setContent(mDownload, R.string.pause, 0, R.color.white);
            }
        }

        @Override
        public void onProgress(AppItemInfo downloadInfo, boolean isSupportFTP) {
            if (mAppItemInfo.getTaskId().equals(downloadInfo.getTaskId())) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(downloadInfo.getProgress());
                mAppItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                mAppItemInfo.setProgress(downloadInfo.getProgress());
                setContent(mDownload, R.string.pause, 0, R.color.white);
            }
        }

        @Override
        public void onStop(AppItemInfo downloadInfo, boolean isSupportFTP) {
            if (mAppItemInfo.getTaskId().equals(downloadInfo.getTaskId())) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(downloadInfo.getProgress());
                mAppItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                setContent(mDownload, R.string.continues, 0, R.color.white);
            }
        }

        @Override
        public void onError(AppItemInfo downloadInfo, String error) {
            mProgressBar.setVisibility(View.GONE);
            mAppItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
        }

        @Override
        public void onSuccess(AppItemInfo downloadInfo) {
            if (mAppItemInfo.getTaskId().equals(downloadInfo.getTaskId())) {
                mProgressBar.setVisibility(View.GONE);
                setContent(mDownload, R.string.finished,
                        R.drawable.shape_button_white_cyan, R.color.button_cyan);
                mAppItemInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
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
