package com.openthos.appstore.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.item.AppLayoutFragment;
import com.openthos.appstore.fragment.item.AppTypeFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SoftwareFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_software, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData(view);

        initFragment();
    }

    private void initFragment() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        AppLayoutFragment appLayoutFragment = new AppLayoutFragment();
        appLayoutFragment.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        appLayoutFragment.setAll(false);
        appLayoutFragment.setDatas(Constants.getData());

        AppTypeFragment appTypeFragment = new AppTypeFragment();
        appTypeFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.fragment_software_left, appLayoutFragment);
        transaction.replace(R.id.fragment_software_right, appTypeFragment);
        transaction.commit();
    }

    private void initData(View view) {

    }
}
