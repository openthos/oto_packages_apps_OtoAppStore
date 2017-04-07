package com.openthos.appstore.fragment;

import android.os.Message;
import android.view.View;
import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppItemAdapter;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.view.CustomGridView;

import java.util.List;

public class MoreFragment extends BaseFragment {
    private List<AppItemInfo> mDatas;
    private CustomGridView mGridView;
    private AppItemAdapter mAppItemAdapter;

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
        mAppItemAdapter = new AppItemAdapter(getActivity());
        mGridView.setAdapter(mAppItemAdapter);
    }

    @Override
    public void initData() {
        if (mDatas != null && mDatas.size() > 0) {
            mAppItemAdapter.addDatas(mDatas);
        }
    }

    @Override
    public void getHandlerMessage(Message message) {
    }

    @Override
    public void setData(Object datas) {
        if (datas != null) {
            mDatas = (List<AppItemInfo>) datas;
        }
    }
}
