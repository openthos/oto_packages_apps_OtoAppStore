package com.openthos.appstore.fragment.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppTypeAdapter;
import com.openthos.appstore.bean.AppTypeInfo;
import com.openthos.appstore.view.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppTypeFragment extends Fragment {

    private CustomListView mListView;
    private int mFromFragment;
    private List<AppTypeInfo> mDatas = new ArrayList<>();

    public AppTypeFragment() {
    }

    public void setFromFragment(int fromFragment) {
        mFromFragment = fromFragment;
    }

    public void setDatas(List<AppTypeInfo> datas) {
        mDatas = datas;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_right, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = ((CustomListView) view.findViewById(R.id.fragment_app_type_listview));
        AppTypeAdapter adapter = new AppTypeAdapter(getActivity(), mFromFragment);
        mListView.setAdapter(adapter);
        adapter.addDatas(mDatas);
    }
}