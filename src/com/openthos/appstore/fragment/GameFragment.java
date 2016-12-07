package com.openthos.appstore.fragment;

public class GameFragment extends BaseClassifyFragment{

    @Override
    public void initTypeData() {
        new Thread(new GetData("/type/game", TYPE_BACK)).start();
    }
}