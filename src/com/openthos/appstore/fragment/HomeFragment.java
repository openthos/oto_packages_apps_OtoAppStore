package com.openthos.appstore.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppLayoutAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.bean.DataInfo;
import com.openthos.appstore.fragment.item.HomeAppLayoutFragment;
import com.openthos.appstore.fragment.item.AppTypeFragment;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.view.CustomListView;
import com.openthos.appstore.view.Kanner;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    private ImageView mBack;
    private ImageView mForward;
    private Kanner mKanner;

    private String mRecommend;
    private String mPraise;
    private String mWelcome;
    private String mFrequent;
    private CustomListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);

        initData();

        initListener();
    }

    @Override
    public void refresh() {
        super.refresh();
        initData();
    }

    private void initData() {
        new Thread(new GetData()).start();
    }

    private void initFragment(String recommend, String praise, String welcome, String frequent) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        HomeAppLayoutFragment homeAppLayoutFragment = new HomeAppLayoutFragment();
        homeAppLayoutFragment.setAll(false);
        homeAppLayoutFragment.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        homeAppLayoutFragment.setDatas(recommend, praise, welcome);
        transaction.replace(R.id.fragment_home_left, homeAppLayoutFragment);

        transaction.commit();

        AppLayoutAdapter appLayoutAdapter = new AppLayoutAdapter(getActivity(), 1, true);
        mListView.setAdapter(appLayoutAdapter);
        try {
            DataInfo dataInfo = new DataInfo(new JSONObject(frequent));
            AppLayoutInfo appLayoutInfo = new AppLayoutInfo(dataInfo.getAppList());
            appLayoutInfo.setType(getActivity().getString(R.string.frequent_used));
            appLayoutAdapter.addItem(appLayoutInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView(View view) {
        mListView = ((CustomListView) view.findViewById(R.id.fragment_home_listview));
        mKanner = ((Kanner) view.findViewById(R.id.fragment_home_kanner));
        mForward = (ImageView) view.findViewById(R.id.fragment_home_forward);
        mBack = (ImageView) view.findViewById(R.id.fragment_home_back);
        mBack.setVisibility(View.GONE);
        mForward.setVisibility(View.GONE);
    }

    private void initListener() {
        mKanner.setImagesUrl(Constants.getString());
        mKanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mKanner.getCurrentItem();
                if (currentItem > 1) {
                    mKanner.setCurrentItem(currentItem - 1);
                } else {
                    mKanner.setCurrentItem(1);
                }
            }
        });

        mForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mKanner.getCurrentItem();
                if (currentItem < mKanner.getImageViewsSize()) {
                    mKanner.setCurrentItem(currentItem + 1);
                } else {
                    mKanner.setCurrentItem(mKanner.getImageViewsSize());
                }
            }
        });
    }

    class GetData implements Runnable {
        @Override
        public void run() {
            String recommandUrl = "/list/recommend";
            String praiseUrl = "/list/praise";
            String welcomeUrl = "/list/welcome";
            String frequentUrl = "/home/frequent";

            mRecommend = SPUtils.getData(getActivity(),
                    Constants.SP_CACHE_DATA, "recommend" + StoreApplication.DATE_FORMAT);
            mPraise = SPUtils.getData(getActivity(),
                    Constants.SP_CACHE_DATA, "praise" + StoreApplication.DATE_FORMAT);
            mWelcome = SPUtils.getData(getActivity(),
                    Constants.SP_CACHE_DATA, "welcome" + StoreApplication.DATE_FORMAT);
            mFrequent = SPUtils.getData(getActivity(),
                    Constants.SP_CACHE_DATA, "frequent" + StoreApplication.DATE_FORMAT);
            if (mRecommend == null || mPraise == null || mWelcome == null || mFrequent == null) {
                mRecommend = NetUtils.getNetStr(getActivity(), recommandUrl);
                mPraise = NetUtils.getNetStr(getActivity(), welcomeUrl);
                mWelcome = NetUtils.getNetStr(getActivity(), praiseUrl);
                mFrequent = NetUtils.getNetStr(getActivity(), frequentUrl);
                if (mRecommend != null &&
                        mPraise != null && mWelcome != null && mFrequent != null) {
                    mHandler.sendEmptyMessage(0);
                    SPUtils.saveData(getActivity(), Constants.SP_CACHE_DATA,
                            "recommend" + StoreApplication.DATE_FORMAT, mRecommend);
                    SPUtils.saveData(getActivity(), Constants.SP_CACHE_DATA,
                            "praise" + StoreApplication.DATE_FORMAT, mPraise);
                    SPUtils.saveData(getActivity(), Constants.SP_CACHE_DATA,
                            "welcome" + StoreApplication.DATE_FORMAT, mWelcome);
                    SPUtils.saveData(getActivity(), Constants.SP_CACHE_DATA,
                            "frequent" + StoreApplication.DATE_FORMAT, mFrequent);
                }
            } else {
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initFragment(mRecommend, mPraise, mWelcome, mFrequent);
                    break;
            }
            return false;
        }
    });
}
