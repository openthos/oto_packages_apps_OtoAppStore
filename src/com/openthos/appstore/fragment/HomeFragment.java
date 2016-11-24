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
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.item.HomeAppLayoutFragment;
import com.openthos.appstore.fragment.item.AppTypeFragment;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.view.Kanner;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    private ImageView mBack;
    private ImageView mForward;
    private Kanner mKanner;

    private String recommend;
    private String praise;
    private String welcome;

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

    private void initData() {
        new Thread(new GetData()).start();
    }

    private void initFragment(String recommend, String praise, String welcome) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        HomeAppLayoutFragment homeAppLayoutFragment = new HomeAppLayoutFragment();
        homeAppLayoutFragment.setAll(false);
        homeAppLayoutFragment.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        homeAppLayoutFragment.setDatas(recommend, praise, welcome);
        transaction.replace(R.id.fragment_home_left, homeAppLayoutFragment);

        AppTypeFragment itemRightFragment = new AppTypeFragment();
        itemRightFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.fragment_home_right, itemRightFragment);

        transaction.commit();
    }

    private void initView(View view) {
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
                Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
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
            String recommandUrl = Constants.BASEURL + "/list/recommend";
            String praiseUrl = Constants.BASEURL + "/list/praise";
            String welcomeUrl = Constants.BASEURL + "/list/welcome";
            String format = new SimpleDateFormat("yyyyMMdd").
                    format(new Date(System.currentTimeMillis()));
            recommend = FileHelper.readFile(AppUtils.getAppName(recommandUrl) + format);
            praise = FileHelper.readFile(AppUtils.getAppName(praiseUrl)  + format);
            welcome = FileHelper.readFile(AppUtils.getAppName(welcomeUrl)  + format);
            if (recommend == null || praise == null || welcome == null) {
                recommend = NetUtils.getNetStr(recommandUrl);
                praise = NetUtils.getNetStr(welcomeUrl);
                welcome = NetUtils.getNetStr(praiseUrl);
            }

            mHandler.sendEmptyMessage(0);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initFragment(recommend, praise, welcome);
                    break;
            }
            return false;
        }
    });
}