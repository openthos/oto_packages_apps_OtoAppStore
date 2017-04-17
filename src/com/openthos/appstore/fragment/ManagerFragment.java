package com.openthos.appstore.fragment;

import android.annotation.SuppressLint;
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
import com.openthos.appstore.view.CustomListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
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
    private List<AppInstallInfo> mAppInstallInfos;
    private HashMap<String, AppInstallInfo> mAppInstallMap;

    @SuppressLint("ValidFragment")
    public ManagerFragment(HashMap<String, AppInstallInfo> appInstallMap) {
        mAppInstallMap = appInstallMap;
    }

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
        mAppInstallInfos = new ArrayList<>();
    }

    @Override
    public void initData() {
        mAppInstallInfos.clear();
        for (Map.Entry<String, AppInstallInfo> entry : mAppInstallMap.entrySet()) {
            mAppInstallInfos.add(entry.getValue());
        }
        mUpdateTitle.setText(getNumText(R.string.updates, mAppInstallInfos.size()));
        mUpdateAdapter = new ManagerUpdateAdapter(mContext);
        mUpdateList.setAdapter(mUpdateAdapter);
        mUpdateAdapter.addDatas(mAppInstallInfos, true);

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
