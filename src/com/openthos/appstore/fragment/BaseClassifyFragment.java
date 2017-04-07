package com.openthos.appstore.fragment;

import android.os.Message;
import android.view.View;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppItemLayoutAdapter;
import com.openthos.appstore.bean.AppLayout;
import com.openthos.appstore.view.CustomListView;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseClassifyFragment extends BaseFragment {
    private CustomListView mListView;
    private AppItemLayoutAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_classify;
    }

    @Override
    public void refresh() {
        initData();
    }

    @Override
    public void initView(View view) {
        mListView = (CustomListView) view.findViewById(R.id.fragment_classify_listview);
        mAdapter = new AppItemLayoutAdapter(getActivity());
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void getHandlerMessage(Message message) {
        if (message.what == GAME_SOFTWARE_BACK && message.obj != null) {
            try {
                AppLayout appLayout = new AppLayout(new JSONObject((String) message.obj));
                mAdapter.addDatas(appLayout.getAppItemLayoutInfos(), false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
