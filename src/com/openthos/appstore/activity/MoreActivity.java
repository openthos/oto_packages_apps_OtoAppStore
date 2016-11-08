package com.openthos.appstore.activity;

import android.content.Intent;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.fragment.item.AppLayoutFragment;
import com.openthos.appstore.utils.ActivityTitileUtils;

public class MoreActivity extends BaseActivity {

    private int mFromFragment;
    private AppLayoutInfo mAppLayoutInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        mFromFragment = ActivityTitileUtils.checked(this, getIntent());
        ActivityTitileUtils.initActivityTitle(this);

        initView();

        loadData();

        initFragment();
    }

    private void loadData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(Constants.BUNDLE);
        if (bundle != null) {
            mAppLayoutInfo = (AppLayoutInfo) bundle.getSerializable(Constants.APP_LAYOUT_INFO);
        }
    }

    private void initView() {

    }

    private void initFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        AppLayoutFragment appLayoutFragment = new AppLayoutFragment();
        appLayoutFragment.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        appLayoutFragment.setAll(true);
        appLayoutFragment.setDatas(mAppLayoutInfo);
        appLayoutFragment.setFromFragment(mFromFragment);

        transaction.replace(R.id.activity_more_left, appLayoutFragment);

        transaction.commit();
    }
}
