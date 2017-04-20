package com.openthos.appstore.fragment;

import com.openthos.appstore.bean.AppInstallInfo;

import java.util.HashMap;

public class SoftwareFragment extends BaseClassifyFragment {

    public SoftwareFragment(HashMap<String, AppInstallInfo> appInstallMap) {
        super(appInstallMap);
    }

    @Override
    public void initData() {
        new Thread(new GetData("/data/software", GAME_SOFTWARE_BACK)).start();
    }

    @Override
    public void setData(Object data) {
    }
}