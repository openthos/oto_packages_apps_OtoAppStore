package com.openthos.appstore.fragment.item;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends BaseFragment {
    private AppLayoutInfo mAppLayoutInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        initFragment();
    }

    public void setData(AppLayoutInfo appLayoutInfo) {
        mAppLayoutInfo = appLayoutInfo;
    }

    private void initView() {

    }

    private void initFragment() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        AppLayoutFragment appLayoutFragment = new AppLayoutFragment();
        appLayoutFragment.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        appLayoutFragment.setAll(true);
        appLayoutFragment.setDatas(mAppLayoutInfo);

        transaction.replace(R.id.fragment_more_left, appLayoutFragment);

        transaction.commit();
    }
}