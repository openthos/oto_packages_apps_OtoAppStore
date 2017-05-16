package com.openthos.appstore.fragment;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.RadioButton;

import com.openthos.appstore.R;
import com.openthos.appstore.bean.AppInstallInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerFragment extends BaseFragment implements View.OnClickListener {
    public List<AppInstallInfo> mInstallInfos;
    private RadioButton mUpdate;
    private RadioButton mDownload;
    private RadioButton mInstalled;
    private FragmentManager mFragmentManager;
    private UpdateFragment mUpdateFragment;
    private DownloadFragment mDownloadFragment;
    private InstallFragment mInstallFragment;
    private Fragment mCurrentFragment;

    public ManagerFragment() {
        super();
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
        initAppInstallInfos();
        if (mCurrentFragment instanceof UpdateFragment) {
            mUpdateFragment.refresh();
        } else if (mCurrentFragment instanceof DownloadFragment) {
            mDownloadFragment.refresh();
        } else {
            mInstallFragment.refresh();
        }
    }

    @Override
    public void initView(View view) {
        mUpdate = (RadioButton) view.findViewById(R.id.update);
        mDownload = (RadioButton) view.findViewById(R.id.download);
        mInstalled = (RadioButton) view.findViewById(R.id.have_installed);

        mUpdate.setOnClickListener(this);
        mDownload.setOnClickListener(this);
        mInstalled.setOnClickListener(this);

        mInstallInfos = new ArrayList<>();
        mFragmentManager = getChildFragmentManager();
    }

    @Override
    public void initData() {
        initAppInstallInfos();
        mUpdateFragment = new UpdateFragment();
        mDownloadFragment = new DownloadFragment();
        mInstallFragment = new InstallFragment();
        mFragmentManager.beginTransaction().
                add(R.id.manager_contain, mDownloadFragment).hide(mDownloadFragment).commit();
        mFragmentManager.beginTransaction().
                add(R.id.manager_contain, mInstallFragment).hide(mInstallFragment).commit();
        mFragmentManager.beginTransaction().
                add(R.id.manager_contain, mUpdateFragment).show(mUpdateFragment).commit();
        mCurrentFragment = mUpdateFragment;
    }

    @Override
    public void getHandlerMessage(Message message) {
    }

    @Override
    public void onClick(View view) {
        if (mCurrentFragment != null) {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).commit();
        }
        initAppInstallInfos();
        switch (view.getId()) {
            case R.id.update:
                mUpdate.setChecked(true);
                mFragmentManager.beginTransaction().show(mUpdateFragment).commit();
                mCurrentFragment = mUpdateFragment;
                mUpdateFragment.refresh();
                break;
            case R.id.download:
                mDownload.setChecked(true);
                mFragmentManager.beginTransaction().show(mDownloadFragment).commit();
                mCurrentFragment = mDownloadFragment;
                mDownloadFragment.refresh();
                break;
            case R.id.have_installed:
                mInstalled.setChecked(true);
                mFragmentManager.beginTransaction().show(mInstallFragment).commit();
                mCurrentFragment = mInstallFragment;
                mInstallFragment.refresh();
                break;
        }
    }

    private void initAppInstallInfos() {
        mInstallInfos.clear();
        for (Map.Entry<String, AppInstallInfo> entry : mAppInstallMap.entrySet()) {
            mInstallInfos.add(entry.getValue());
        }

        Collections.sort(mInstallInfos, new Comparator<AppInstallInfo>() {
            @Override
            public int compare(AppInstallInfo info0, AppInstallInfo info1) {
                if (info1.getLastUpdateTime() >= info0.getLastUpdateTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
}
