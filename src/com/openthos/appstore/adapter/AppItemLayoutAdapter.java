package com.openthos.appstore.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.bean.AppItemLayoutInfo;
import com.openthos.appstore.download.DownloadListener;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.download.DownloadService;
import com.openthos.appstore.utils.SQLOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppItemLayoutAdapter extends BasicAdapter implements View.OnClickListener {
    private HashMap<String, AppInstallInfo> mAppInstallMap;
    private DownloadManager mManager;
    private RecyclerViewScrollListener mRecyclerViewScrollListener;

    public AppItemLayoutAdapter(Context context, HashMap<String, AppInstallInfo> appInstallMap,
                                List<AppItemLayoutInfo> datas) {
        super(context);
        mDatas = datas;
        mAppInstallMap = appInstallMap;
        mManager = DownloadService.getDownloadManager();
        mManager.setAllTaskListener(new LayoutDownloadListener());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.app_item_layout, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        if (mDatas != null && mDatas.size() != 0) {
            AppItemLayoutInfo appItemLayoutInfo = (AppItemLayoutInfo) mDatas.get(position);
            holder.type.setText(appItemLayoutInfo.getType());
            RecyclerItemAdapter recyclerItemAdapter = new RecyclerItemAdapter(mContext,
                    appItemLayoutInfo.getAppItemInfoList());
            LinearLayoutManager layout = new LinearLayoutManager(mContext);
            layout.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.recyclerView.setLayoutManager(layout);
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setAdapter(recyclerItemAdapter);
            holder.imageView.setTag(holder.recyclerView);
            holder.recyclerView.setTag(holder.imageView);
            if (mRecyclerViewScrollListener == null) {
                mRecyclerViewScrollListener = new RecyclerViewScrollListener();
            }
            holder.recyclerView.setOnScrollListener(mRecyclerViewScrollListener);
            recyclerItemAdapter.refreshLayout();
            holder.whole.setOnClickListener(this);
            holder.whole.setTag(appItemLayoutInfo.getAppItemInfoList());
            holder.imageView.setOnClickListener(this);
        }
        return convertView;
    }

    @Override
    public void refreshLayout() {
        for (int i = 0; i < mDatas.size(); i++) {
            AppItemLayoutInfo appItemLayoutInfo = (AppItemLayoutInfo) mDatas.get(i);
            for (int j = 0; j < appItemLayoutInfo.getAppItemInfoList().size(); j++) {
                initStateAndProgress(appItemLayoutInfo.getAppItemInfoList().get(j));
            }
        }
        notifyDataSetChanged();
    }

    private void initStateAndProgress(AppItemInfo appItemInfo) {
        if (appItemInfo != null) {
            AppInstallInfo appInstallInfo = mAppInstallMap.get(appItemInfo.getPackageName());
            if (appInstallInfo != null) {
                if (appInstallInfo.getVersionCode() < appItemInfo.getVersionCode()) {
                    appItemInfo.setState(Constants.APP_NEED_UPDATE);
                } else {
                    appItemInfo.setState(Constants.APP_HAVE_INSTALLED);
                }
            } else {
                appItemInfo.setState(Constants.APP_NOT_INSTALL);
            }

            AppItemInfo downloadInfo = new SQLOperator(mContext).
                    getDownloadInfoByPkgName(appItemInfo.getPackageName());
            if (downloadInfo != null) {
                long downloadSize = downloadInfo.getDownFileSize();
                long fileSize = downloadInfo.getFileSize();
                if (fileSize == 0) {
                    appItemInfo.setProgress(0);
                } else if (downloadSize < fileSize) {
                    appItemInfo.setProgress(downloadInfo.getProgress());
                    appItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                } else if (downloadSize == fileSize) {
                    switch (appItemInfo.getState()) {
                        case Constants.APP_HAVE_INSTALLED:
                            break;
                        default:
                            appItemInfo.setProgress(100);
                            appItemInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
                            break;
                    }
                }
            }

            ArrayList<AppItemInfo> allTask = mManager.getAllInfo();
            for (int i = 0; i < allTask.size(); i++) {
                AppItemInfo appInfo = allTask.get(i);
                if (appItemInfo.getTaskId().equals(appInfo.getTaskId())) {
                    if (appInfo.isOnDownloading()) {
                        appItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                        appItemInfo.setProgress(appInfo.getProgress());
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.app_item_layout_whole:
                MainActivity.mHandler.sendMessage(MainActivity.mHandler
                                         .obtainMessage(Constants.MORE_FRAGMENT, view.getTag()));
                break;
            case R.id.more:
                Object tag = view.getTag();
                RecyclerView recyclerView = (RecyclerView) tag;
                int childWidth = recyclerView.getChildAt(0).getMeasuredWidth();
                recyclerView.scrollBy(childWidth, 0);
                break;
        }
    }

    private class ViewHolder {
        private TextView type;
        private TextView whole;
        private RecyclerView recyclerView;
        private ImageView imageView;

        public ViewHolder(View view) {
            type = (TextView) view.findViewById(R.id.app_item_layout_type);
            whole = (TextView) view.findViewById(R.id.app_item_layout_whole);
            recyclerView = (RecyclerView) view.findViewById(R.id.app_item_layout_recycler);
            imageView = (ImageView) view.findViewById(R.id.more);
        }
    }

    private class LayoutDownloadListener implements DownloadListener {
        @Override
        public void onStart(AppItemInfo downloadInfo) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemLayoutInfo appItemLayoutInfo = ((List<AppItemLayoutInfo>) mDatas).get(i);
                for (int j = 0; j < appItemLayoutInfo.getAppItemInfoList().size(); j++) {
                    AppItemInfo appItemInfo = appItemLayoutInfo.getAppItemInfoList().get(j);
                    if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                        appItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onProgress(AppItemInfo downloadInfo, boolean isSupportFTP) {
            // disable refresh UI in download state
//            notifyDataSetChanged();
        }

        @Override
        public void onStop(AppItemInfo downloadInfo, boolean isSupportFTP) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemLayoutInfo appItemLayoutInfo = ((List<AppItemLayoutInfo>) mDatas).get(i);
                for (int j = 0; j < appItemLayoutInfo.getAppItemInfoList().size(); j++) {
                    AppItemInfo appItemInfo = appItemLayoutInfo.getAppItemInfoList().get(j);
                    if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                        appItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onError(AppItemInfo downloadInfo, String error) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemLayoutInfo appItemLayoutInfo = ((List<AppItemLayoutInfo>) mDatas).get(i);
                for (int j = 0; j < appItemLayoutInfo.getAppItemInfoList().size(); j++) {
                    AppItemInfo appItemInfo = appItemLayoutInfo.getAppItemInfoList().get(j);
                    if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                        appItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onSuccess(AppItemInfo downloadInfo) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemLayoutInfo appItemLayoutInfo = ((List<AppItemLayoutInfo>) mDatas).get(i);
                for (int j = 0; j < appItemLayoutInfo.getAppItemInfoList().size(); j++) {
                    AppItemInfo appItemInfo = appItemLayoutInfo.getAppItemInfoList().get(j);
                    if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                        appItemInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        private int horizontalScrollDistance = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                View firstVisibleChild = recyclerView.getChildAt(0);
                int childWidth = firstVisibleChild.getMeasuredWidth();
                int remain = horizontalScrollDistance % childWidth;
                int childPosition = recyclerView.getChildPosition(firstVisibleChild);
                int halfWidth = childWidth / 2;
                if (Math.abs(remain) >= halfWidth) {
                    if (remain > 0) {
                        recyclerView.scrollBy(childWidth - remain, 0);
                    } else {
                        recyclerView.scrollToPosition(childPosition);
                    }
                } else {
                    if (remain > 0) {
                        recyclerView.scrollToPosition(childPosition);
                    } else {
                        recyclerView.scrollBy(-remain, 0);
                    }
                }
                horizontalScrollDistance = 0;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if ( recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                horizontalScrollDistance += dx;
            }
        }
    }
}
