package com.openthos.appstore.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.item.AppLayoutFragment;
import com.openthos.appstore.fragment.item.AppTypeFragment;
import com.openthos.appstore.bean.SoftwareInfo;
import com.openthos.appstore.bean.SoftwareLayoutInfo;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.bean.AppLayoutInfo;
import android.os.Handler;
import android.os.Message;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SoftwareFragment extends BaseFragment {

    private static final int STATE_CODE_DATA = 0;
    public List<AppLayoutInfo> mListAppLayoutInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_software, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();

        initFragment(mListAppLayoutInfo);
    }

    private void initFragment(List<AppLayoutInfo> appLayoutInfo) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        AppLayoutFragment appLayoutFragment = new AppLayoutFragment();
        appLayoutFragment.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        appLayoutFragment.setAll(true);
        appLayoutFragment.setDatas(appLayoutInfo);

        AppTypeFragment appTypeFragment = new AppTypeFragment();
        appTypeFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.fragment_software_left, appLayoutFragment);
        transaction.replace(R.id.fragment_software_right, appTypeFragment);
        transaction.commit();
    }

    private void initData() {
        new Thread(new GetData()).start();
    }

    class GetData implements Runnable {
        @Override
        public void run() {
            try {
                mListAppLayoutInfo = getListData(NetUtils.getNetStr(Constants.BASEURL + "/list/2"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(STATE_CODE_DATA);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_CODE_DATA:
                    initFragment(mListAppLayoutInfo);
                    break;
            }
            return false;
        }
    });

    public List<AppLayoutInfo> getListData(String urlData) throws JSONException {
        List<AppLayoutInfo> listData = new ArrayList<>();
        SoftwareInfo softwareInfo = new SoftwareInfo(new JSONObject(urlData));
        AppLayoutInfo appLayoutInfoData = softwareInfo.getSoftwareLayoutInfo().getAppLayoutInfo();
        listData.add(appLayoutInfoData);
        return listData;
    }
}
