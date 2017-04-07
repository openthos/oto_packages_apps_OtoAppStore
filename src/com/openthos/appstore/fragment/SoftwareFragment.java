package com.openthos.appstore.fragment;

public class SoftwareFragment extends BaseClassifyFragment {

    @Override
    public void initData() {
        new Thread(new GetData("/data/software", GAME_SOFTWARE_BACK)).start();
    }

    @Override
    public void setData(Object data) {
    }
}