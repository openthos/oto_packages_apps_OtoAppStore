package com.openthos.appstore.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.adapter.ManagerDownloadAdapter;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.download.DownloadService;
import com.openthos.appstore.view.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment implements View.OnClickListener {
    private List<TaskInfo> mTaskInfos;
    private List<AppInstallInfo> mAppInstallInfos;
    private TextView mState;
    private CustomListView mListView;
    private ManagerDownloadAdapter mAdapter;
    private DownloadManager mDownloadManager;

    public DownloadFragment() {
        mTaskInfos = new ArrayList<>();
        mDownloadManager = DownloadService.getDownloadManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAppInstallInfos =
             ((ManagerFragment) ((MainActivity) getActivity()).getCurrentFragment()).mInstallInfos;
        return inflater.inflate(R.layout.fragment_manager_classify, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mState = (TextView) view.findViewById(R.id.state);
        mListView = (CustomListView) view.findViewById(R.id.customlistView);
        mAdapter = new ManagerDownloadAdapter(getActivity(), mDownloadManager,
                mAppInstallInfos, mTaskInfos);
        mListView.setAdapter(mAdapter);
        mTaskInfos.addAll(mDownloadManager.getAllTask());
        mAdapter.refreshLayout();

        mState.setOnClickListener(this);
        if (mTaskInfos.size() > 0) {
            mState.setText(getString(R.string.startAll));
        } else {
            mState.setText(getString(R.string.no_task));
        }
    }

    public void refresh() {
        mAdapter.refreshLayout();
        if (mTaskInfos.size() == 0) {
            mState.setText(getString(R.string.no_task));
        } else if (!mState.getText().toString().equals(getString(R.string.stopAll))) {
            mState.setText(getString(R.string.startAll));
        }
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        if (btn.getText().equals(getString(R.string.startAll))) {
            btn.setText(getString(R.string.stopAll));
            mDownloadManager.startAllTask();
        } else {
            btn.setText(getString(R.string.startAll));
            mDownloadManager.stopAllTask();
        }
        refresh();
    }
}
