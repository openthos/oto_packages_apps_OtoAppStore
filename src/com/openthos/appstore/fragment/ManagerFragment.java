package com.openthos.appstore.fragment;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.ManagerDownloadAdapter;
import com.openthos.appstore.adapter.ManagerUpdateAdapter;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.download.DownloadService;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.view.CustomListView;

import java.util.List;
public class ManagerFragment extends BaseFragment {

    private CustomListView mUpdateList;
    private CustomListView mDownloadList;
    private TextView mUpdateTitle;
    private TextView mDownloadTitle;
    private TextView mStartAll;
    private TextView mUpdateAll;
    private Context mContext;
    private ManagerUpdateAdapter mUpdateAdapter;
    private DownloadManager mDownloadManager;
    private ManagerDownloadAdapter mDownloadAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_manager;
    }

    @Override
    public void setData(Object data) {
    }

    @Override
    public void refresh() {
        initData();
    }

    @Override
    public void initView(View view) {
        mUpdateList = (CustomListView) view.findViewById(R.id.fragment_manager_updateList);
        mDownloadList = (CustomListView) view.findViewById(R.id.fragment_manager_downloadList);
        mUpdateTitle = (TextView) view.findViewById(R.id.fragment_manager_updateTitle);
        mDownloadTitle = (TextView) view.findViewById(R.id.fragment_manager_downloadTitle);
        mStartAll = (TextView) view.findViewById(R.id.fragment_manager_startAll);
        mUpdateAll = (TextView) view.findViewById(R.id.fragment_manager_updateAll);
        mDownloadManager = DownloadService.getDownloadManager();
        mContext = getActivity();
    }

    @Override
    public void initData() {
        List<AppInstallInfo> appInstallInfos = AppUtils.getAppPackageInfo(mContext);
        mUpdateTitle.setText(getNumText(R.string.updates, appInstallInfos.size()));
        mUpdateAdapter = new ManagerUpdateAdapter(mContext);
        mUpdateList.setAdapter(mUpdateAdapter);
        mUpdateAdapter.addDatas(appInstallInfos, true);

        List<TaskInfo> allTask = mDownloadManager.getAllTask();
        mDownloadTitle.setText(getNumText(R.string.downloads, allTask.size()));
        mDownloadAdapter = new ManagerDownloadAdapter(mContext, mDownloadManager);
        mDownloadList.setAdapter(mDownloadAdapter);
        mDownloadAdapter.addData(allTask, true);
    }

    @Override
    public void getHandlerMessage(Message message) {
    }

    private String getNumText(int text, int size) {
        return String.format(getResources().getString(text), size);
    }
}