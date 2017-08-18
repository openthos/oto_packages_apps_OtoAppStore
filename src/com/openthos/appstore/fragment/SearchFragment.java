package com.openthos.appstore.fragment;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppItemAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.bean.AppItemLayoutInfo;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.view.CustomGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            if (TextUtils.isEmpty(mContent)) {
                mDatas.addAll(mMainActivity.mDataSource);
            } else {
                for (AppItemInfo appItemInfo : mMainActivity.mDataSource) {
                    if (appItemInfo.getAppName().toLowerCase().contains(
                            mContent.toLowerCase().trim()) && !mDatas.contains(appItemInfo)) {
                        mDatas.add(appItemInfo);
                    }
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
