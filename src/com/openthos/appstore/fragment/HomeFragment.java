package com.openthos.appstore.fragment;

import android.os.Message;
import android.view.View;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppItemLayoutAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemLayoutInfo;
import com.openthos.appstore.bean.AppLayout;
import com.openthos.appstore.view.BannerView;
import com.openthos.appstore.view.CustomListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private BannerView mBannerView;
    private CustomListView mListView;
    private AppItemLayoutAdapter mAdapter;
    private List<AppItemLayoutInfo> mDatas;

    public HomeFragment(HashMap<String, AppInstallInfo> appInstallMap) {
        super(appInstallMap);
        mDatas = new ArrayList<>();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void setData(Object data) {
    }

    @Override
    public void refresh() {
        initData();
    }

    public void initView(View view) {
        mBannerView = (BannerView) view.findViewById(R.id.fragment_home_carousel);
        mListView = (CustomListView) view.findViewById(R.id.fragment_home_list);
        mAdapter = new AppItemLayoutAdapter(getActivity(), mAppInstallMap, mDatas);
        mListView.setAdapter(mAdapter);
    }

    public void initData() {
        mBannerView.setImageUrls(Constants.getString());
        new Thread(new GetData("/data/home", HOME_DATA_BACK)).start();
    }


    @Override
    public void getHandlerMessage(Message message) {
        if (message.what == HOME_DATA_BACK && message.obj != null) {
            try {
                mDatas.clear();
                mDatas.addAll(
                        new AppLayout(new JSONObject((String) message.obj)).getAppItemLayoutInfos());
                mAdapter.refreshLayout();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mBannerView != null) {
            if (hidden) {
                mBannerView.removeCallbacksAndMessages();
            } else {
                mBannerView.startPlay();
            }
        }
    }
}
