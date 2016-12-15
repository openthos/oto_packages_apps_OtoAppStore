package com.openthos.appstore.fragment.item;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.DetailContentInfo;
import com.openthos.appstore.bean.DetailInfo;
import com.openthos.appstore.bean.SQLDownLoadInfo;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.download.DownLoadListener;
import com.openthos.appstore.utils.download.DownLoadManager;
import com.openthos.appstore.utils.download.DownLoadService;
import com.openthos.appstore.view.Kanner;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class DetailFragment extends BaseFragment implements View.OnClickListener {
    private ImageView mIcon;
    private Button mDownload;
    private TextView mAppName;
    private TextView mAppCompany;
    private RatingBar mCommentStar;
    private TextView mContent;
    private Kanner mKanner;
    private TextView mPromulgator;
    private TextView mType;
    private TextView mSize;
    private int mFromFragment;

    private String mData;
    private int mState;
    private String mNetStr;
    private DetailContentInfo mContentInfo;
    private ProgressBar mProgressBar;

    private DownLoadManager mManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mManager = DownLoadService.getDownLoadManager();
        mManager.setAllTaskListener(new DetailDownLoadListener());

        initView(view);

        initData();

        initFragment();
    }

    private void initFragment() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        CommentFragment commentFragment = new CommentFragment();
        commentFragment.setDatas(Constants.getComment());
        commentFragment.setAll(false);

        AppTypeFragment appTypeFragment = new AppTypeFragment();
//        appTypeFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.fragment_detail_comment, commentFragment);
        transaction.replace(R.id.fragment_detail_morelove, appTypeFragment);

        transaction.commit();
    }

    private void initData() {
        mKanner.setImagesUrl(Constants.getString());
        mKanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(new GetData()).start();
    }

    private void initView(View view) {
        mIcon = (ImageView) view.findViewById(R.id.fragment_detail_icon);
        mDownload = (Button) view.findViewById(R.id.fragment_detail_download);
        mAppName = (TextView) view.findViewById(R.id.fragment_detail_appName);
        mAppCompany = (TextView) view.findViewById(R.id.fragment_detail_appCompany);
        mCommentStar = (RatingBar) view.findViewById(R.id.fragment_detail_commentstar);
        mContent = (TextView) view.findViewById(R.id.fragment_detail_content);
        mKanner = (Kanner) view.findViewById(R.id.fragment_detail_kanner);
        mPromulgator = (TextView) view.findViewById(R.id.fragment_detail_promulgator);
        mType = (TextView) view.findViewById(R.id.fragment_detail_type);
        mSize = (TextView) view.findViewById(R.id.fragment_detail_size);
        mProgressBar = ((ProgressBar) view.findViewById(R.id.fragment_detail_progressbar));

        mDownload.setOnClickListener(this);
    }

    public void setDatas(String data) {
        if (!TextUtils.isEmpty(data)) {
            String[] split = data.split(" ");
            if (split.length == 2) {
                mData = split[0];
                try {
                    mState = Integer.parseInt(split[1]);
                } catch (NumberFormatException e) {
                    mState = Constants.APP_NOT_INSTALL;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        Context mContext = DetailFragment.this.getActivity();

        if (mContentInfo != null) {
            String btnStr = mDownload.getText().toString();

            String continues = mContext.getResources().getString(R.string.continues);
            String pause = mContext.getResources().getString(R.string.pause);
            String installs = mContext.getResources().getString(R.string.install);
            String update = mContext.getResources().getString(R.string.update);
            String finished = mContext.getResources().getString(R.string.finished);

            if (btnStr.equals(continues)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownload.setText(pause);
                SPUtils.saveDownloadState(
                        mContext, mContentInfo.getPackageName(), Constants.APP_DOWNLOAD_PAUSE);
                MainActivity.mBinder.stopTask(mContentInfo.getId() + "");
            } else if (btnStr.equals(pause)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownload.setText(continues);
                SPUtils.saveDownloadState(
                        mContext, mContentInfo.getPackageName(), Constants.APP_DOWNLOAD_CONTINUE);
                MainActivity.mBinder.startTask(mContentInfo.getId() + "");
            } else if (btnStr.equals(installs)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownload.setText(continues);
                SPUtils.saveDownloadState(
                        mContext, mContentInfo.getPackageName(), Constants.APP_DOWNLOAD_CONTINUE);
                MainActivity.mBinder.addTask(mContentInfo.getId() + "", Constants.BASEURL + "/" +
                                mContentInfo.getDownloadUrl(),
                        FileHelper.getNameFromUrl(mContentInfo.getDownloadUrl()),
                        mContentInfo.getPackageName());
            } else if (btnStr.equals(update)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownload.setText(continues);
                SPUtils.saveDownloadState(
                        mContext, mContentInfo.getPackageName(), Constants.APP_DOWNLOAD_CONTINUE);
                MainActivity.mBinder.addTask(mContentInfo.getId() + "", Constants.BASEURL + "/" +
                                mContentInfo.getDownloadUrl(),
                        FileHelper.getNameFromUrl(mContentInfo.getDownloadUrl()),
                        mContentInfo.getPackageName());
            } else if (btnStr.equals(finished)) {
                File file =
                        new File(FileHelper.getDefaultFileFromUrl(mContentInfo.getDownloadUrl()));
                if (file.exists() && file.length() != 0) {
                    AppUtils.installApk(mContext, file.getAbsolutePath());
                } else {
                    SPUtils.saveDownloadState(
                            mContext, mContentInfo.getPackageName(), Constants.APP_NOT_INSTALL);
                    Message message = MainActivity.mHandler.obtainMessage();
                    message.what = Constants.TOAST;
                    message.obj = mContext.getString(R.string.this_file_is_not_exist);
                    MainActivity.mHandler.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void refresh() {
        loadData();
    }

    private class GetData implements Runnable {
        @Override
        public void run() {
            mNetStr = NetUtils.getNetStr(getActivity(), "/detail/" + mData);
            if (!TextUtils.isEmpty(mNetStr)) {
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    loadData();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void loadData() {
        try {
            DetailInfo detailInfo = new DetailInfo(new JSONObject(mNetStr));
            mContentInfo = detailInfo.getDetailContentInfo();
            if (mContentInfo != null) {
                mContentInfo.setState(mState);
                Picasso.with(getActivity()).
                        load(Constants.BASEURL + "/" + mContentInfo.getIconUrl()).into(mIcon);
                mAppName.setText(mContentInfo.getName());
                mAppCompany.setText(mContentInfo.getCompany());
                mCommentStar.setRating(mContentInfo.getStar());
                mContent.setText(mContentInfo.getContent());
                mPromulgator.setText(getActivity().getString(
                        R.string.promulgator) + mContentInfo.getPromulgator());
                mType.setText(getActivity().getString(R.string.type) + mContentInfo.getType());
                mSize.setText(getActivity().getString(R.string.size) + mContentInfo.getFileSize());
                mContentInfo.setState(SPUtils.getDownloadState(getActivity(),
                        mContentInfo.getPackageName() + ""));
                switch (mContentInfo.getState()) {
                    case Constants.APP_NOT_INSTALL:
                        setContent(mDownload, R.string.install,
                                R.drawable.shape_button_white_cyan, R.color.button_cyan);
                        break;
                    case Constants.APP_HAVE_INSTALLED:
                        setContent(mDownload, R.string.have_installed,
                                R.drawable.shape_button_white_gray, R.color.button_gray);
                        break;
                    case Constants.APP_DOWNLOAD_CONTINUE:
                        mProgressBar.setVisibility(View.VISIBLE);
                        setContent(mDownload, R.string.continues, 0, R.color.button_cyan);
//                            R.drawable.shape_button_white_cyan, R.color.button_cyan);
                        break;
                    case Constants.APP_DOWNLOAD_PAUSE:
                        mProgressBar.setVisibility(View.VISIBLE);
                        setContent(mDownload, R.string.pause, 0, R.color.button_cyan);
                        break;
                    case Constants.APP_NEED_UPDATE:
                        setContent(mDownload, R.string.update, R.drawable.shape_button_white_cyan,
                                R.color.button_cyan);
                        break;
                    case Constants.APP_DOWNLOAD_FINISHED:
                        setContent(mDownload, R.string.finished,
                                R.drawable.shape_button_white_cyan, R.color.button_cyan);
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    private class DetailDownLoadListener implements DownLoadListener {
        @Override
        public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
            mProgressBar.setVisibility(View.VISIBLE);
            setContent(mDownload, R.string.continues, 0, R.color.button_cyan);
        }

        @Override
        public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            int progress = 0;
            if (sqlDownLoadInfo.getFileSize() != 0) {
                progress = (int) (100 *
                        sqlDownLoadInfo.getDownloadSize() / sqlDownLoadInfo.getFileSize());
            }
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(progress);
            setContent(mDownload, R.string.continues, 0, R.color.button_cyan);
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            mProgressBar.setVisibility(View.VISIBLE);
            setContent(mDownload, R.string.pause, 0, R.color.button_cyan);
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo, String error) {

        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            mProgressBar.setVisibility(View.GONE);
            setContent(mDownload, R.string.finished,
                    R.drawable.shape_button_white_cyan, R.color.button_cyan);
        }
    }
}
