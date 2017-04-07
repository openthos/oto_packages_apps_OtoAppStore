package com.openthos.appstore.fragment;

import android.os.Message;
import android.view.View;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppItemLayoutAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayout;
import com.openthos.appstore.view.BannerView;
import com.openthos.appstore.view.CustomListView;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends BaseFragment {
    private BannerView mBannerView;
    private CustomListView mListView;
    private AppItemLayoutAdapter mAdapter;

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
        mAdapter = new AppItemLayoutAdapter(getActivity());
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
                AppLayout appLayout = new AppLayout(new JSONObject((String) message.obj));
                mAdapter.addDatas(appLayout.getAppItemLayoutInfos(), false);
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
