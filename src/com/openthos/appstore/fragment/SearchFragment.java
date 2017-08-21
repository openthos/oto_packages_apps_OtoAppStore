package com.openthos.appstore.fragment;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppItemAdapter;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.view.CustomGridView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearchFragment extends BaseFragment {
    private String mContent;
    private CustomGridView mGridView;
    private AppItemAdapter mAppItemAdapter;
    private List<AppItemInfo> mDatas;

    public SearchFragment() {
        super();
        mDatas = new ArrayList<>();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void refresh() {
        initData();
    }

    @Override
    public void initData() {
        mDatas.clear();
        Map<String, AppItemInfo> map = new HashMap<>();
        if (mMainActivity.mDataSource.size() != 0) {
            for (AppItemInfo appItemInfo : mMainActivity.mDataSource) {
                if (TextUtils.isEmpty(mContent)) {
                    map.put(appItemInfo.getAppName(), appItemInfo);
                } else if (appItemInfo.getAppName().toLowerCase().contains(
                            mContent.toLowerCase().trim())) {
                    map.put(appItemInfo.getAppName(), appItemInfo);
                }
            }
        }
        Iterator<Map.Entry<String, AppItemInfo>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, AppItemInfo> next = iterator.next();
            mDatas.add(next.getValue());
        }
        mAppItemAdapter.refreshLayout();
    }

    @Override
    public void getHandlerMessage(Message message) {
    }

    @Override
    public void initView(View view) {
        mGridView = ((CustomGridView) view.findViewById(R.id.fragment_search_gridview));
        mAppItemAdapter = new AppItemAdapter(getActivity(), mAppInstallMap, mDatas);
        mGridView.setAdapter(mAppItemAdapter);
    }

    @Override
    public void setData(Object content) {
        if (content != null) {
            mContent = (String) content;
        }
    }
}
