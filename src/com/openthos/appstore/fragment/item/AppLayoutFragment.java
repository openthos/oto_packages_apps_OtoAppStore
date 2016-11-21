package com.openthos.appstore.fragment.item;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppLayoutAdapter;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.view.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppLayoutFragment extends BaseFragment {

    private CustomListView mListView;
    private AppLayoutAdapter mAdapter;
    private int mNumColumns;
    private boolean mIsAll;
    private List<AppLayoutInfo> mDatas = new ArrayList<>();

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public void setAll(boolean all) {
        mIsAll = all;
    }

    public void setDatas(List<AppLayoutInfo> datas) {
        if (mDatas != null && datas != null) {
            mDatas.clear();
            mDatas = datas;
        }
    }

    public void setDatas(AppLayoutInfo appLayoutInfo) {
        if (mDatas != null && appLayoutInfo != null) {
            mDatas.clear();
            mDatas.add(appLayoutInfo);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_left, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = ((CustomListView) view.findViewById(R.id.fragment_app_layout_listview));
        mAdapter = new AppLayoutAdapter(getActivity(), mNumColumns, mIsAll);
        mListView.setAdapter(mAdapter);

        mAdapter.addDatas(mDatas);
    }
}