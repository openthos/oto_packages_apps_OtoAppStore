package com.openthos.appstore.fragment;

import android.content.pm.PackageManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.ManagerDownloadAdapter;
import com.openthos.appstore.adapter.ManagerUpdateAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLAppInstallInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.utils.DialogUtils;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.sql.DownloadKeeper;
import com.openthos.appstore.utils.download.DownLoadManager;
import com.openthos.appstore.utils.download.DownLoadService;
import com.openthos.appstore.view.CustomListView;
import com.openthos.appstore.app.StoreApplication;
import java.util.List;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagerFragment extends Fragment
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
    private List<SQLAppInstallInfo> mAppInfo;
    private DownLoadManager mDownLoadManager;

    private final int mUpdaAdapter = 0;
    private final int mDownAdapter = 1;

    public ManagerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDownLoadManager = StoreApplication.getDownLoadManager();

        initView(view);

        if(mDownLoadManager != null) {
            initData();
        }
    }

    private void initData() {
        try {
            mAppInfo = AppUtils.getAppPackageInfo(getActivity());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mUpdateAdapter = new ManagerUpdateAdapter(getActivity(), false, Constants.MANAGER_FRAGMENT);
        mUpdateAdapter.setAll(false);
        mUpdateAdapter.setAppInfo(mAppInfo);
        mUpdatelistview.setAdapter(mUpdateAdapter);
        try {
            mUpdateAdapter.setAppInfo(AppUtils.getAppPackageInfo(getActivity()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mDownloadAdapter = new ManagerDownloadAdapter(getActivity(), mDownLoadManager);
        mDownloadAdapter.setAll(false);
        mDownloadlistview.setAdapter(mDownloadAdapter);
        if (mDownLoadManager != null) {
            mDownloadAdapter.addData(mDownLoadManager.getAllTask());
        }

        mUpdateNum.setText(String.format(getResources().getString(R.string.updates),
                mAppInfo.size()));
        if (mDownLoadManager != null) {
            mDownloadNum.setText(String.format(getResources().getString(R.string.downloads),
                    mDownLoadManager.getAllTask().size()));
        }

        mLaunchUpdate.setOnClickListener(this);
        mLaunchDownload.setOnClickListener(this);
        mStartAll.setOnClickListener(this);
        mUpdateAll.setOnClickListener(this);
        mDownloadlistview.setOnItemClickListener(this);
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
                    foldOrLaunch(mUpdaAdapter,
                                 AppUtils.getAppPackageInfo(getActivity()), mLaunchUpdate);
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
                boolean isSuccess = AppUtils.installApk(getActivity(),
                        mDownLoadManager.getInstallFilepath(fileName, taskID, null));
                if (!isSuccess) {
                    Tools.toast(getActivity(), getActivity().
                            getResources().getString(R.string.this_file_is_not_exist));
                }
                dialog.cancel();
            }

            @Override
            public void removeTask(AlertDialog dialog) {
                new DownloadKeeper(getActivity()).deleteDownLoadInfo(Constants.USER_ID, taskID);
                ArrayList<TaskInfo> allTask = mDownLoadManager.getAllTask();
               // allTask.remove(taskInfo);
                mDownloadAdapter.addData(allTask);
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
            if ((startAll).equals(btnStr)){
                btn.setText(stopAll);
                mDownLoadManager.startAllTask();
            } else {
                btn.setText(startAll);
                mDownLoadManager.stopAllTask();
            }
        } else {
            Tools.toast(getActivity(), getResources().getString(R.string.no_task));
        }
    }

    private void updateAll() {
        Tools.toast(getActivity(), getResources().getString(R.string.manager_fragment_toast));
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
                mUpdateAdapter.setAppInfo(datas);
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
}
