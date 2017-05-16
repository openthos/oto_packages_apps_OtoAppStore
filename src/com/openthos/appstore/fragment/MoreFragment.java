package com.openthos.appstore.fragment;

import android.os.Message;
import android.view.View;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppItemAdapter;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.view.CustomGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoreFragment extends BaseFragment {
    private List<AppItemInfo> mDatas;
    private CustomGridView mGridView;
    private AppItemAdapter mAppItemAdapter;

    public MoreFragment() {
        super();
        mDatas = new ArrayList<>();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    public void refresh() {
        initData();
    }

    @Override
    public void initView(View view) {
        mGridView = (CustomGridView) view.findViewById(R.id.fragment_more_grid);
        mAppItemAdapter = new AppItemAdapter(getActivity(), mAppInstallMap, mDatas);
        mGridView.setAdapter(mAppItemAdapter);
    }

    @Override
    public void initData() {
        mAppItemAdapter.refreshLayout();
    }

    @Override
    public void getHandlerMessage(Message message) {
    }

    @Override
    public void setData(Object datas) {
        if (datas != null) {
            mDatas.clear();
            mDatas.addAll((List<AppItemInfo>) datas);
        }
    }
}
