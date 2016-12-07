package com.openthos.appstore.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.openthos.appstore.R;
import com.openthos.appstore.adapter.AppLayoutAdapter;
import com.openthos.appstore.adapter.AppTypeAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.bean.AppTypeInfo;
import com.openthos.appstore.bean.DataInfo;
import com.openthos.appstore.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ljh on 16-12-7.
 */
public abstract class BaseClassifyFragment extends BaseFragment
        implements AppTypeAdapter.OnItemClickListener {
    public final int LAYOUT_BACK = 0;
    public final int TYPE_BACK = 1;

    public ListView mLayoutListView;
    public ListView mTypeListView;
    public String mNetStr;
    public AppLayoutAdapter mLayoutAdapter;
    public AppTypeAdapter mTypeAdapter;
    public String mType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initTypeData();
    }

    public abstract void initTypeData();

    public void initView(View view) {
        mLayoutListView = (ListView) view.findViewById(R.id.fragment_game_left);
        mTypeListView = (ListView) view.findViewById(R.id.fragment_game_right);
        mLayoutAdapter = new AppLayoutAdapter(getActivity(), Constants.GRIDVIEW_NUM_COLUMS, true);
        mTypeAdapter = new AppTypeAdapter(getActivity());
        mLayoutListView.setAdapter(mLayoutAdapter);
        mTypeListView.setAdapter(mTypeAdapter);
        mTypeAdapter.setOnItemClickListener(this);
    }

    public void initLayoutData(String url) {
        new Thread(new GetData(url, LAYOUT_BACK)).start();
    }

    @Override
    public void OnItemClick(long id, String type) {
        mType = type;
        initLayoutData("/type/" + id);
    }

    public class GetData implements Runnable {
        private String mUrl;
        private int mFrom;

        public GetData(String url, int from) {
            mUrl = url;
            mFrom = from;
        }

        @Override
        public void run() {
            mNetStr = NetUtils.getNetStr(getActivity(), mUrl);
            if (!TextUtils.isEmpty(mNetStr)) {
                switch (mFrom) {
                    case LAYOUT_BACK:
                        mHandler.sendEmptyMessage(LAYOUT_BACK);
                        break;
                    case TYPE_BACK:
                        mHandler.sendEmptyMessage(TYPE_BACK);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LAYOUT_BACK:
                    loadLayout();
                    break;
                case TYPE_BACK:
                    loadType();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public void loadType() {
        try {
            AppTypeInfo appTypeInfo = new AppTypeInfo(new JSONObject(mNetStr));
            mTypeAdapter.addDatas(appTypeInfo);
            mType = appTypeInfo.getList().get(0).getType();
            initLayoutData("/type/" + appTypeInfo.getList().get(0).getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadLayout() {
        try {
            DataInfo dataInfo = new DataInfo(new JSONObject(mNetStr));
            AppLayoutInfo appLayoutInfo = new AppLayoutInfo(dataInfo.getAppList());
            appLayoutInfo.setType(mType);
            mLayoutAdapter.addItem(appLayoutInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}