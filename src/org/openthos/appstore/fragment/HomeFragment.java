package org.openthos.appstore.fragment;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import org.openthos.appstore.R;
import org.openthos.appstore.adapter.AppItemLayoutAdapter;
import org.openthos.appstore.app.Constants;
import org.openthos.appstore.bean.AppItemInfo;
import org.openthos.appstore.bean.AppItemLayoutInfo;
import org.openthos.appstore.bean.AppLayout;
import org.openthos.appstore.bean.NetDataListInfo;
import org.openthos.appstore.utils.DataCache;
import org.openthos.appstore.utils.NetUtils;
import org.openthos.appstore.utils.SPUtils;
import org.openthos.appstore.utils.Tools;
import org.openthos.appstore.view.BannerView;
import org.openthos.appstore.view.CustomListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private BannerView mBannerView;
    private CustomListView mListView;
    private AppItemLayoutAdapter mAdapter;
    private List<AppItemLayoutInfo> mDatas;

    public HomeFragment() {
        super();
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
        new Thread(new GetData("/data/home", HOME_DATA_BACK)).start();
        new Thread(new LoadAllDataRunnable()).start();
    }

    public void initView(View view) {
        mBannerView = (BannerView) view.findViewById(R.id.fragment_home_carousel);
        mListView = (CustomListView) view.findViewById(R.id.fragment_home_list);
        mListView.setIsDisableScroll(true);
        mAdapter = new AppItemLayoutAdapter(getActivity(), mAppInstallMap, mDatas);
        mListView.setAdapter(mAdapter);
        mBannerView.setImageUrls(Constants.getString());
    }

    public void initData() {
        localData = DataCache.loadLocalData(getActivity(), "/data/home");
        if (!TextUtils.isEmpty(localData)) {
            mDatas.clear();
            try {
                mDatas.addAll(new AppLayout(new JSONObject(localData),
                        mMainActivity).getAppItemLayoutInfos());
                mAdapter.refreshLayout();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new Thread(new GetData("/data/home", HOME_DATA_BACK)).start();

        List<String> searchData = SPUtils.getAllData(getActivity());
        AppItemInfo appItemInfo = null;
        if (searchData != null) {
            for (int i = 0; i < searchData.size(); i++) {
                try {
                    appItemInfo = Tools.getAppItemInfo(
                        new JSONObject(searchData.get(i)), mMainActivity.mAllAppItemInfos);
                    if (appItemInfo == null) {
                        continue;
                    } else {
                        mMainActivity.mDataSource.add(appItemInfo);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        new Thread(new LoadAllDataRunnable()).start();
    }

    @Override
    public void getHandlerMessage(Message message) {
        if (message.what == HOME_DATA_BACK && message.obj != null) {
            try {
                mDatas.clear();
                mDatas.addAll(new AppLayout(new JSONObject((String) message.obj),
                        mMainActivity).getAppItemLayoutInfos());
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

    private class LoadAllDataRunnable implements Runnable {
        @Override
        public void run() {
            String allData = NetUtils.getNetStr("/all");
            if (!TextUtils.isEmpty(allData)) {
                try {
                    NetDataListInfo netDataInfos = new NetDataListInfo(
                            new JSONObject(allData), mMainActivity);
                    if (netDataInfos != null && netDataInfos.getNetDataInfoList() != null) {
                        List<AppItemInfo> appList = netDataInfos.getNetDataInfoList();
                        mMainActivity.mDataSource.clear();
                        mMainActivity.mDataSource.addAll(appList);
                        SPUtils.saveAllData(mMainActivity, appList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}