package com.openthos.appstore.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.adapter.ManagerInstallAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.view.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstallFragment extends Fragment {
    private List<AppInstallInfo> mAppInstallInfos;
    private TextView mState;
    private CustomListView mListView;
    private ManagerInstallAdapter mAdapter;

    public InstallFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_classify, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppInstallInfos = new ArrayList<>();
        loadExternalAppPackageInfos();
        mState = (TextView) view.findViewById(R.id.state);
        mState.setVisibility(View.GONE);
        mListView = (CustomListView) view.findViewById(R.id.customlistView);
        mListView.setIsDisableScroll(false);

        mAdapter = new ManagerInstallAdapter(getActivity(), mAppInstallInfos);
        mListView.setAdapter(mAdapter);
        mAdapter.refreshLayout();
    }

    private void loadExternalAppPackageInfos() {
        AppInstallInfo appInfo = null;
        PackageManager packageManager = ((MainActivity) getActivity()).getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            PackageInfo packageInfo = pinfo.get(i);
            if (isDisplayApplication(packageInfo)) {
                appInfo = new AppInstallInfo();
                appInfo.setId(i);
                appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                appInfo.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                appInfo.setPackageName(packageInfo.packageName);
                appInfo.setVersionCode(packageInfo.versionCode);
                appInfo.setVersionName(packageInfo.versionName);
                appInfo.setState(Constants.APP_HAVE_INSTALLED);
                appInfo.setLastUpdateTime(packageInfo.lastUpdateTime);
                mAppInstallInfos.add(appInfo);
            }
        }
    }

    private boolean isDisplayApplication(PackageInfo packageInfo) {
        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }

    public void refresh(){
        mAdapter.refreshLayout();
    }
}
