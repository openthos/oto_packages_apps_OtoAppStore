package com.openthos.appstore.fragment;

import android.text.TextUtils;

import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.bean.AppItemLayoutInfo;
import com.openthos.appstore.bean.AppLayout;
import com.openthos.appstore.utils.DataCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GameFragment extends BaseClassifyFragment {

    public GameFragment() {
        super();
    }

    @Override
    public void initData() {
        localData = DataCache.loadLocalData(getActivity(), "/data/game");
        if (!TextUtils.isEmpty(localData)) {
            mDatas.clear();
            try {
                mDatas.addAll(
                        new AppLayout(new JSONObject(localData)).getAppItemLayoutInfos());
                mAdapter.refreshLayout();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new Thread(new GetData("/data/game", GAME_SOFTWARE_BACK)).start();

        for (AppItemLayoutInfo appItemLayoutInfo : mDatas) {
            List<AppItemInfo> appItemInfoList = appItemLayoutInfo.getAppItemInfoList();
            mMainActivity.mDataSource.addAll(appItemInfoList);
        }
    }


    @Override
    public void setData(Object data) {
    }
}
