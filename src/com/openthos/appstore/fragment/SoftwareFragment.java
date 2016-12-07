package com.openthos.appstore.fragment;

public class SoftwareFragment extends BaseClassifyFragment {

    @Override
    public void initTypeData() {
        new Thread(new GetData("/type/software", TYPE_BACK)).start();
    }
}