package com.openthos.appstore.fragment.item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppTypeAdapter;
import com.openthos.appstore.bean.AppTypeInfo;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.view.CustomListView;

import java.util.ArrayList;
import java.util.List;

public class AppTypeFragment extends BaseFragment {

    private CustomListView mListView;
    private List<AppTypeInfo> mDatas = new ArrayList<>();

    public void setDatas(List<AppTypeInfo> datas) {
        mDatas = datas;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_right, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = ((CustomListView) view.findViewById(R.id.fragment_app_type_listview));
        AppTypeAdapter adapter = new AppTypeAdapter(getActivity());
        mListView.setAdapter(adapter);
        adapter.addDatas(mDatas);
    }
}