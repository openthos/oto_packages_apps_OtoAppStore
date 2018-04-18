package org.openthos.appstore.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openthos.appstore.MainActivity;
import org.openthos.appstore.R;
import org.openthos.appstore.adapter.ManagerUpdateAdapter;
import org.openthos.appstore.app.Constants;
import org.openthos.appstore.app.StoreApplication;
import org.openthos.appstore.bean.AppInstallInfo;
import org.openthos.appstore.bean.AppItemInfo;
import org.openthos.appstore.download.DownloadManager;
import org.openthos.appstore.download.DownloadService;
import org.openthos.appstore.utils.Tools;
import org.openthos.appstore.view.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateFragment extends Fragment implements View.OnClickListener {
    private List<AppInstallInfo> mAppInstallInfos;
    private List<AppItemInfo> mUpdateDatas;
    private TextView mState;
    private CustomListView mListView;
    private ManagerUpdateAdapter mAdapter;
    private DownloadManager mDownloadManager;

    public UpdateFragment() {
        mUpdateDatas = new ArrayList<>();
        mDownloadManager = DownloadService.getDownloadManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_classify, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppInstallInfos = ((MainActivity) getActivity()).mAppInstallInfos;
        mState = (TextView) view.findViewById(R.id.state);
        mListView = (CustomListView) view.findViewById(R.id.customlistView);
        mListView.setIsDisableScroll(false);

        mAdapter = new ManagerUpdateAdapter(getActivity(), mDownloadManager,
                mAppInstallInfos, mUpdateDatas);
        mListView.setAdapter(mAdapter);
        mAdapter.refreshLayout();

        if (mUpdateDatas.size() > 0) {
            mState.setText(getString(R.string.updateAll));
        } else {
            mState.setText(getString(R.string.not_need_update));
        }
        mState.setOnClickListener(this);
    }

    public void refresh() {
        mAdapter.refreshLayout();
        if (mUpdateDatas.size() > 0) {
            mState.setText(getString(R.string.updateAll));
        } else {
            mState.setText(getString(R.string.not_need_update));
        }
    }

    @Override
    public void onClick(View view) {
        if (mUpdateDatas.size() > 0) {
            for (int i = 0; i < mUpdateDatas.size(); i++) {
                AppItemInfo updateData = mUpdateDatas.get(i);
                switch (updateData.getState()) {
                    case Constants.APP_NEED_UPDATE:
                        MainActivity.mDownloadService.addTask(updateData.getTaskId(),
                                StoreApplication.mBaseUrl + "/" + updateData.getDownloadUrl(),
                                updateData.getAppName(),
                                updateData.getPackageName(),
                                updateData.getIconUrl());
                        break;
                    case Constants.APP_DOWNLOAD_PAUSE:
                        MainActivity.mDownloadService.startTask(updateData.getTaskId());
                        break;
                    case Constants.APP_DOWNLOAD_CONTINUE:
                        break;
                    default:
                        break;
                }
            }
        } else {
            Tools.toast(getActivity(), getString(R.string.no_data_need_update));
        }
        refresh();
    }
}
