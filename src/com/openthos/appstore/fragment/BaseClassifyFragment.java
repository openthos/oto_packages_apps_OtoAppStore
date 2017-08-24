package com.openthos.appstore.fragment;

import android.os.Message;
import android.view.View;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppItemLayoutAdapter;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemLayoutInfo;
import com.openthos.appstore.bean.AppLayout;
import com.openthos.appstore.view.CustomListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseClassifyFragment extends BaseFragment {
    private CustomListView mListView;
    public AppItemLayoutAdapter mAdapter;
    public List<AppItemLayoutInfo> mDatas;

    public BaseClassifyFragment() {
        super();
        mDatas = new ArrayList<>();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_classify;
    }

    @Override
    public void refresh() {
        initData();
    }

    @Override
    public void initView(View view) {
        mListView = (CustomListView) view.findViewById(R.id.fragment_classify_listview);
        mListView.setIsDisableScroll(false);
        mAdapter = new AppItemLayoutAdapter(getActivity(), mAppInstallMap, mDatas);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void getHandlerMessage(Message message) {
        if (message.what == GAME_SOFTWARE_BACK && message.obj != null) {
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
}
