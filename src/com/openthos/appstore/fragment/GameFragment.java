package com.openthos.appstore.fragment;

public class GameFragment extends BaseClassifyFragment {

    @Override
    public void initData() {
        new Thread(new GetData("/data/game", GAME_SOFTWARE_BACK)).start();
    }


    @Override
    public void setData(Object data) {
    }
}