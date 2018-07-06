package org.openthos.appstore.fragment;

import android.text.TextUtils;

import org.openthos.appstore.bean.AppItemInfo;
import org.openthos.appstore.bean.AppItemLayoutInfo;
import org.openthos.appstore.bean.AppLayout;
import org.openthos.appstore.utils.DataCache;

import org.json.JSONException;
import org.json.JSONObject;

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
                mDatas.addAll(new AppLayout(new JSONObject(localData),
                        mMainActivity).getAppItemLayoutInfos());
                mAdapter.refreshLayout();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new Thread(new GetData("/data/game", GAME_SOFTWARE_BACK)).start();
    }

    @Override
    public void refresh() {
        for (AppItemLayoutInfo appItemLayoutInfo : mDatas) {
            List<AppItemInfo> appItemInfoList = appItemLayoutInfo.getAppItemInfoList();
            mMainActivity.mDataSource.addAll(appItemInfoList);
        }
        new Thread(new GetData("/data/game", GAME_SOFTWARE_BACK)).start();
    }

    @Override
    public void setData(Object data) {
    }
}
