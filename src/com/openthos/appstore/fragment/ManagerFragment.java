package com.openthos.appstore.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.ManagerUpdateAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLAppInstallInfo;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.Tools;
//import com.openthos.appstore.utils.download.DownLoadService;
import com.openthos.appstore.view.CustomListView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagerFragment extends Fragment implements View.OnClickListener {

    private TextView mUpdateNum;
    private TextView mDownloadNum;
    private Button mStartAll;
    private Button mUpdateAll;
    private Button mLaunchUpdate;
    private Button mLaunchDownload;
    private CustomListView mUpdatelistview;
    private CustomListView mDownloadlistview;
    private ManagerUpdateAdapter mUpdateAdapter;
//    private ManagerDownloadAdapter mDownloadAdapter;
    private List<SQLAppInstallInfo> mAppInfo;

    public ManagerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_manager, container, false);

        return ret;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initData();
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

//        mDownloadAdapter = new ManagerDownloadAdapter(getActivity(),
//                                      DownLoadService.getDownLoadManager());
//        mDownloadAdapter.setAll(false);
//        mDownloadlistview.setAdapter(mDownloadAdapter);
        mUpdateNum.setText(String.format(getResources().getString(R.string.updates),
                mAppInfo.size()));
        mDownloadNum.setText(String.format(getResources().getString(R.string.downloads),
                Constants.getManagerInfo().size()));

        mLaunchUpdate.setOnClickListener(this);
        mLaunchDownload.setOnClickListener(this);
        mStartAll.setOnClickListener(this);
        mUpdateAll.setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_manager_startAll:
                startAll();
                break;
            case R.id.fragment_manager_updateAll:
                updateAll();
                break;
            case R.id.fragment_manager_launch1://updateFold
                try {
                    foldOrLaunch(mUpdateAdapter,
                                 AppUtils.getAppPackageInfo(getActivity()), mLaunchUpdate);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fragment_manager_launch2://downloadFold
//                foldOrLaunch(mDownloadAdapter, Constants.getManagerInfo(), mLaunchUpdate);
                break;
            default:
                break;
        }
    }

    private void startAll() {
        Tools.toast(getActivity(), getResources().getString(R.string.manager_fragment_toast));
    }

    private void updateAll() {
        Tools.toast(getActivity(), getResources().getString(R.string.manager_fragment_toast));
    }

    /**
     * The logical function of unfolding and folding buttons
     * @param adapter
     * @param datas
     * @param button
     */
    private void foldOrLaunch(ManagerUpdateAdapter adapter,
                              List<SQLAppInstallInfo> datas, Button button) {
        String launch = getResources().getString(R.string.launch);
        String fold = getResources().getString(R.string.fold);
        String str = button.getText().toString();
        if (str.equals(launch)) {
            adapter.setAll(true);
            button.setText(fold);
        } else {
            adapter.setAll(false);
            button.setText(launch);
        }
        adapter.setAppInfo(datas);
    }
}
