package com.openthos.appstore.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.item.AppLayoutFragment;
import com.openthos.appstore.fragment.item.AppTypeFragment;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.bean.GameInfo;
import com.openthos.appstore.utils.NetUtils;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends BaseFragment {

    public static final int STATE_CODE_SEND_DATA = 0;
    private List<AppLayoutInfo> mListAppLayoutInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
    }

    private void initFragment(List<AppLayoutInfo> listAppLayoutInfo) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        AppLayoutFragment appLayoutFragment = new AppLayoutFragment();
        appLayoutFragment.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        appLayoutFragment.setAll(true);
        appLayoutFragment.setDatas(listAppLayoutInfo);

        AppTypeFragment appTypeFragment = new AppTypeFragment();
        appTypeFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.fragment_game_left, appLayoutFragment);
        transaction.replace(R.id.fragment_game_right, appTypeFragment);

        transaction.commit();
    }

    private void initData() {
        new Thread(new GetData()).start();
    }

    class GetData implements Runnable {
        @Override
        public void run() {
            try {
                String netStr = NetUtils.getNetStr(getActivity(), "/list/1");
                if (!TextUtils.isEmpty(netStr)) {
                    mListAppLayoutInfo = getListData(netStr);
                    mHandler.sendEmptyMessage(STATE_CODE_SEND_DATA);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_CODE_SEND_DATA:
                    initFragment(mListAppLayoutInfo);
                    break;
            }
            return false;
        }
    });

    public List<AppLayoutInfo> getListData(String urlData) throws JSONException {
        List<AppLayoutInfo> listData = new ArrayList<>();
        GameInfo gameInfo = new GameInfo(new JSONObject(urlData));
        AppLayoutInfo appLayoutInfoData = gameInfo.getGameLayoutInfo().getAppLayoutInfo();
        listData.add(appLayoutInfoData);
        return listData;
    }
}