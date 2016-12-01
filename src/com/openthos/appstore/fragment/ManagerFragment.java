package com.openthos.appstore.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.adapter.ManagerDownloadAdapter;
import com.openthos.appstore.adapter.ManagerUpdateAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLAppInstallInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.utils.DialogUtils;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.download.DownLoadService;
import com.openthos.appstore.utils.sql.DownloadKeeper;
import com.openthos.appstore.utils.download.DownLoadManager;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.view.CustomListView;

import java.util.List;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagerFragment extends BaseFragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView mUpdateNum;
    private TextView mDownloadNum;
    private Button mStartAll;
    private Button mUpdateAll;
    private Button mLaunchUpdate;
    private Button mLaunchDownload;
    private CustomListView mUpdatelistview;
    private CustomListView mDownloadlistview;
    private ManagerUpdateAdapter mUpdateAdapter;
    private ManagerDownloadAdapter mDownloadAdapter;
    private DownLoadManager mDownLoadManager;

    private final int mUpdaAdapter = 0;
    private final int mDownAdapter = 1;
    private AppInstallReceiver mAppInstallReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registBroadcast();

        mDownLoadManager = DownLoadService.getDownLoadManager();

        initView(view);

        if (mDownLoadManager != null) {
            initData();
        }
    }

    public void initData() {
        mUpdateAdapter = new ManagerUpdateAdapter(getActivity(), false);
        mUpdateAdapter.setAll(false);
        mUpdateAdapter.addDatas(MainActivity.mAppPackageInfo);
        mUpdatelistview.setAdapter(mUpdateAdapter);

        mDownloadAdapter = new ManagerDownloadAdapter(getActivity(), mDownLoadManager);
        mDownloadAdapter.setAll(false);
        mDownloadlistview.setAdapter(mDownloadAdapter);
        if (mDownLoadManager != null) {
            mDownloadAdapter.addData(mDownLoadManager.getAllTask());
        }

        int mAppPackageSize = MainActivity.mAppPackageInfo.size();
        if (mAppPackageSize > Constants.MANAGER_NUM_FALSE) {
            mLaunchUpdate.setVisibility(View.VISIBLE);
        } else {
            mLaunchUpdate.setVisibility(View.GONE);
        }
        mUpdateNum.setText(getNumText(R.string.updates, mAppPackageSize));

        int mDownloadSize = mDownLoadManager.getAllTask().size();
        if (mDownloadSize > Constants.MANAGER_NUM_FALSE) {
            mLaunchDownload.setVisibility(View.VISIBLE);
        } else {
            mLaunchDownload.setVisibility(View.GONE);
        }
        mDownloadNum.setText(getNumText(R.string.downloads, mDownloadSize));

        mLaunchUpdate.setOnClickListener(this);
        mLaunchDownload.setOnClickListener(this);
        mStartAll.setOnClickListener(this);
        mUpdateAll.setOnClickListener(this);
        mDownloadlistview.setOnItemClickListener(this);
    }

    private String getNumText(int text, int size) {
        return String.format(getResources().getString(text), size);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregistBroadcast();
    }

    private void initView(View view) {
        mUpdateNum = (TextView) view.findViewById(R.id.fragment_manager_updateNum);
        mDownloadNum = (TextView) view.findViewById(R.id.fragment_manager_downloadNum);
        mStartAll = (Button) view.findViewById(R.id.fragment_manager_startAll);
        mUpdateAll = (Button) view.findViewById(R.id.fragment_manager_updateAll);
        mLaunchUpdate = ((Button) view.findViewById(R.id.fragment_manager_launch1));
        mLaunchDownload = ((Button) view.findViewById(R.id.fragment_manager_launch2));
        mUpdatelistview = (CustomListView) view.
                findViewById(R.id.fragment_manager_customListView1);
        mDownloadlistview = (CustomListView) view.
                findViewById(R.id.fragment_manager_customListView2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_manager_startAll:
                startAll(view);
                break;
            case R.id.fragment_manager_updateAll:
                updateAll();
                break;
            case R.id.fragment_manager_launch1://updateFold
                try {
                    MainActivity.mAppPackageInfo = AppUtils.getAppPackageInfo(getActivity());
                    foldOrLaunch(mUpdaAdapter, MainActivity.mAppPackageInfo, mLaunchUpdate);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fragment_manager_launch2://downloadFold
                foldOrLaunch(mDownAdapter, mDownLoadManager.getAllTask(), mLaunchDownload);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final TaskInfo taskInfo = mDownLoadManager.getAllTask().get(position);
        final String fileName = taskInfo.getFileName();
        final String taskID = taskInfo.getTaskID();
        new DialogUtils().dialogDownload(getActivity(), new DialogUtils.DownloadManager() {
            @Override
            public void install(AlertDialog dialog) {
                String result = AppUtils.installApk(getActivity(),
                        mDownLoadManager.getInstallFile(fileName, null));
                if (result != null) {
                    Tools.toast(getActivity(), result);
                }
                dialog.cancel();
            }

            @Override
            public void removeTask(AlertDialog dialog) {
                new DownloadKeeper(getActivity()).deleteDownLoadInfo(Constants.USER_ID, taskID);
                mDownLoadManager.deleteTask(taskID);
                ArrayList<TaskInfo> allTask = mDownLoadManager.getAllTask();
                FileHelper.deleteFile(fileName);
                mDownloadAdapter.addData(allTask);
                mDownloadNum.setText(getNumText(R.string.downloads, allTask.size()));
                dialog.cancel();
            }
        });
    }

    private void startAll(View view) {
        Button btn = (Button) view;
        String btnStr = btn.getText().toString();
        String startAll = getResources().getString(R.string.startAll);
        String stopAll = getResources().getString(R.string.stopAll);
        ArrayList<TaskInfo> allTask = mDownLoadManager.getAllTask();
        if (allTask != null && allTask.size() != 0) {
            if ((startAll).equals(btnStr)) {
                btn.setText(stopAll);
                MainActivity.mBinder.startAllTask();
            } else {
                btn.setText(startAll);
                MainActivity.mBinder.stopAllTask();
            }
        } else {
            Tools.toast(getActivity(), getResources().getString(R.string.no_task));
        }
    }

    private void updateAll() {
        List<SQLAppInstallInfo> data = new ArrayList<>();
        try {
            MainActivity.mAppPackageInfo = AppUtils.getAppPackageInfo(getActivity());
            for (int i = 0; i < MainActivity.mAppPackageInfo.size(); i++) {
                if (MainActivity.mAppPackageInfo.get(i).getState()
                                                           != Constants.APP_HAVE_INSTALLED) {
                    data.add(MainActivity.mAppPackageInfo.get(i));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (data != null && data.size() == 0) {
            Tools.toast(getActivity(), getResources().getString(R.string.no_data_need_update));
        }
    }

    /**
     * The logical function of unfolding and folding buttons
     *
     * @param whichAdapter
     * @param datas
     * @param button
     */
    private void foldOrLaunch(int whichAdapter,
                              List datas, Button button) {
        String launch = getResources().getString(R.string.launch);
        String fold = getResources().getString(R.string.fold);
        String str = button.getText().toString();
        switch (whichAdapter) {
            case mUpdaAdapter:
                if (str.equals(launch)) {
                    mUpdateAdapter.setAll(true);
                    button.setText(fold);
                } else {
                    mUpdateAdapter.setAll(false);
                    button.setText(launch);
                }
                mUpdateAdapter.addDatas(datas);
                break;
            case mDownAdapter:
                if (str.equals(launch)) {
                    mDownloadAdapter.setAll(true);
                    button.setText(fold);
                } else {
                    mDownloadAdapter.setAll(false);
                    button.setText(launch);
                }
                mDownloadAdapter.addData(datas);
                break;
        }
    }

    public class AppInstallReceiver extends BroadcastReceiver {
        public AppInstallReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Tools.printLog("ARV", "log in" + intent.getAction());
            switch (intent.getAction()) {
                case Intent.ACTION_PACKAGE_ADDED:
                case Intent.ACTION_PACKAGE_REMOVED:
                case Intent.ACTION_PACKAGE_REPLACED:
                    try {
                        MainActivity.mAppPackageInfo = AppUtils.getAppPackageInfo(getActivity());
                        initData();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void registBroadcast() {
        mAppInstallReceiver = new AppInstallReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        getActivity().registerReceiver(mAppInstallReceiver, intentFilter);
    }

    private void unregistBroadcast() {
        getActivity().unregisterReceiver(mAppInstallReceiver);
    }

}
