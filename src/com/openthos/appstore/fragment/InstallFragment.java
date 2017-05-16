package com.openthos.appstore.fragment;

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
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.view.CustomListView;

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
        mAppInstallInfos =
             ((ManagerFragment) ((MainActivity) getActivity()).getCurrentFragment()).mInstallInfos;
        mState = (TextView) view.findViewById(R.id.state);
        mState.setVisibility(View.GONE);
        mListView = (CustomListView) view.findViewById(R.id.customlistView);

        mAdapter = new ManagerInstallAdapter(getActivity(), mAppInstallInfos);
        mListView.setAdapter(mAdapter);
        mAdapter.refreshLayout();
    }

    public void refresh(){
        mAdapter.refreshLayout();
    }
}
