package com.openthos.appstore.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.utils.DataCache;

import java.util.HashMap;

public abstract class BaseFragment extends Fragment {
    public static final int GAME_SOFTWARE_BACK = 0;
    public static final int HOME_DATA_BACK = 1;
    public HashMap<String, AppInstallInfo> mAppInstallMap;
    public String localData;
    public MainActivity mMainActivity;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainActivity = ((MainActivity) getActivity());
        mAppInstallMap = mMainActivity.mAppInstallMap;
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            getHandlerMessage(message);
            return false;
        }
    });

    class GetData implements Runnable {
        private String url;
        private int type;

        public GetData(String url, int type) {
            this.url = url;
            this.type = type;
        }

        @Override
        public void run() {
            String data = DataCache.loadNetData(getActivity(), url);
            if (TextUtils.isEmpty(localData) || !localData.equals(data)) {
                mHandler.sendMessage(mHandler.obtainMessage(type, data));
            }
        }
    }

    public abstract int getLayoutId();

    public abstract void setData(Object data);

    public abstract void refresh();

    public abstract void initView(View view);

    public abstract void initData();

    public abstract void getHandlerMessage(Message message);
}
