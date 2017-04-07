package com.openthos.appstore.fragment;

import android.os.Message;
import android.view.View;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppItemAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.view.CustomGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment {
    private String mContent;
    private CustomGridView mGridView;
    private AppItemAdapter mAppItemAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void refresh() {
        initData();
    }

    @Override
    public void initData() {
        List<String> searchData = SPUtils.getSearchData(getActivity(), mContent);
        List<AppItemInfo> datas = new ArrayList<>();
        if (searchData != null) {
            for (int i = 0; i < searchData.size(); i++) {
                String data = SPUtils.getData(getActivity(),
                        Constants.SP_ALL_DATA, searchData.get(i));
                try {
                    AppItemInfo appItemInfo =
                            new AppItemInfo(new JSONObject(data));
                    datas.add(appItemInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        mAppItemAdapter.addDatas(datas);
    }

    @Override
    public void getHandlerMessage(Message message) {
    }

    @Override
    public void initView(View view) {
        mGridView = ((CustomGridView) view.findViewById(R.id.fragment_search_gridview));
        mAppItemAdapter = new AppItemAdapter(getActivity());
        mGridView.setAdapter(mAppItemAdapter);
    }

    @Override
    public void setData(Object content) {
        if (content != null) {
            mContent = (String) content;
        }
    }
}