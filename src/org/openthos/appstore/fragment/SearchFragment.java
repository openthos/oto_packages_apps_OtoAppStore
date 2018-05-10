package org.openthos.appstore.fragment;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.openthos.appstore.R;
import org.openthos.appstore.adapter.AppItemAdapter;
import org.openthos.appstore.bean.AppItemInfo;
import org.openthos.appstore.view.CustomGridView;
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
        if (mMainActivity.mDataSource.size() != 0) {
            for (AppItemInfo appItemInfo : mMainActivity.mDataSource) {
                if (TextUtils.isEmpty(mContent)) {
                    if (!mDatas.contains(appItemInfo)) {
                        mDatas.add(appItemInfo);
                    }
                } else if (appItemInfo.getAppName().toLowerCase().contains(
                        mContent.toLowerCase().trim()) && !mDatas.contains(appItemInfo)) {
                    mDatas.add(appItemInfo);
                }


            }
        }
        mAppItemAdapter.refreshLayout();
    }

    @Override
    public void getHandlerMessage(Message message) {
    }

    @Override
    public void initView(View view) {
        mGridView = ((CustomGridView) view.findViewById(R.id.fragment_search_gridview));
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
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
