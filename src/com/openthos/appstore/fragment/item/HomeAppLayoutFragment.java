package com.openthos.appstore.fragment.item;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppLayoutAdapter;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.bean.HomeDataInfo;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.view.CustomListView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeAppLayoutFragment extends BaseFragment {

    private CustomListView mRecGameListView;
    private CustomListView mRecSoftListView;
    private CustomListView mPraGameListView;
    private CustomListView mPraSoftListView;
    private CustomListView mWelGameListView;
    private CustomListView mWelSoftListView;
    private int mNumColumns;
    private boolean mIsAll;

    private String mRecommend;
    private String mPraise;
    private String mWelcome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_app_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        loadData();
    }

    @Override
    public void refresh() {
        super.refresh();
        loadData();
    }

    private void loadData() {
        try {
            if (mRecommend != null) {
                loadAdapter(mRecommend, mRecGameListView, mRecSoftListView);
            }

            if (mPraise != null) {
                loadAdapter(mPraise, mPraGameListView, mPraSoftListView);
            }

            if (mWelcome != null) {
                loadAdapter(mWelcome, mWelGameListView, mWelSoftListView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView(View view) {
        mRecGameListView = (CustomListView) view.findViewById(
                R.id.fragment_home_app_layout_recommand_game);
        mRecSoftListView = (CustomListView) view.findViewById(
                R.id.fragment_home_app_layout_recommand_software);
        mPraGameListView = (CustomListView) view.findViewById(
                R.id.fragment_home_app_layout_praise_game);
        mPraSoftListView = (CustomListView) view.findViewById(
                R.id.fragment_home_app_layout_praise_software);
        mWelGameListView = (CustomListView) view.findViewById(
                R.id.fragment_home_app_layout_welcome_game);
        mWelSoftListView = (CustomListView) view.findViewById(
                R.id.fragment_home_app_layout_welcome_software);

        Button mPopularLook = ((Button) view.findViewById(R.id.fragment_home_popular_look));
        Button mFavourLook = ((Button) view.findViewById(R.id.fragment_home_favour_look));
        Button mRecommentLook = ((Button) view.findViewById(R.id.fragment_home_recommand_look));
        mPopularLook.setVisibility(View.GONE);
        mFavourLook.setVisibility(View.GONE);
        mRecommentLook.setVisibility(View.GONE);
    }

    public void setDatas(String recommend, String praise, String welcome) {
        mRecommend = recommend;
        mWelcome = welcome;
        mPraise = praise;
    }

    private void loadAdapter(String recommend, CustomListView gameListView,
                             CustomListView softwareListView) throws JSONException {
        HomeDataInfo dataInfo = new HomeDataInfo(new JSONObject(recommend));
        AppLayoutInfo gameInfo = dataInfo.getAppLayoutInfo().getAppLayoutGameInfo();
        AppLayoutInfo softwareInfo = dataInfo.getAppLayoutInfo().getAppLayoutSoftwareInfo();
        AppLayoutAdapter gameAdapter = new AppLayoutAdapter(
                getActivity(), mNumColumns, mIsAll);
        AppLayoutAdapter softwareAdapter = new AppLayoutAdapter(
                getActivity(), mNumColumns, mIsAll);
        softwareListView.setAdapter(softwareAdapter);
        softwareAdapter.addItem(softwareInfo);
        gameListView.setAdapter(gameAdapter);
        gameAdapter.addItem(gameInfo);
    }

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public void setAll(boolean all) {
        mIsAll = all;
    }
}