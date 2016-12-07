package com.openthos.appstore.fragment.item;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppLayoutAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutGridviewInfo;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.view.CustomListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends BaseFragment {
    private String mContent;
    private CustomListView mListView;
    private AppLayoutAdapter mListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initData();
    }

    public void initData() {
        List<String> searchData = SPUtils.getSearchData(getActivity(), mContent);
        List<AppLayoutGridviewInfo> datas = new ArrayList<>();
        if (searchData != null) {
            for (int i = 0; i < searchData.size(); i++) {
                String data = SPUtils.getData(getActivity(),
                        Constants.SP_ALL_DATA, searchData.get(i));
                try {
                    AppLayoutGridviewInfo gridviewInfo =
                            new AppLayoutGridviewInfo(new JSONObject(data));
                    datas.add(gridviewInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        AppLayoutInfo appLayoutInfo = new AppLayoutInfo(datas);
        appLayoutInfo.setType(getActivity().getString(R.string.search));
        mListAdapter.addItem(appLayoutInfo);
    }

    private void initView(View view) {
        mListView = ((CustomListView) view.findViewById(R.id.fragment_search_listview));
        mListAdapter = new AppLayoutAdapter(getActivity(), Constants.GRIDVIEW_NUM_COLUMS, true);
        mListView.setAdapter(mListAdapter);

    }

    public void setDatas(String content) {
        if (content != null) {
            mContent = content;
        }
    }
}