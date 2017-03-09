package com.openthos.appstore.fragment.item;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppLayoutAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.view.CustomGridView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends BaseFragment {
    private AppLayoutInfo mAppLayoutInfo;
    private CustomGridView mGridView;
    private AppLayoutAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGridView = ((CustomGridView) view.findViewById(R.id.fragment_app_layout_gridview));
        mAdapter = new AppLayoutAdapter(getActivity(), Constants.GRIDVIEW_NUM_COLUMS, true);
        mGridView.setAdapter(mAdapter);

        initData();
    }

    @Override
    public void refresh() {
        super.refresh();
        initData();
    }

    private void initData() {
        mAdapter.addItem(mAppLayoutInfo);
    }

    public void setData(AppLayoutInfo appLayoutInfo) {
        mAppLayoutInfo = appLayoutInfo;
    }
}