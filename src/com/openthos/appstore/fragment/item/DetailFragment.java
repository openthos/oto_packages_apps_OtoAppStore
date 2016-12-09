package com.openthos.appstore.fragment.item;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.DetailContentInfo;
import com.openthos.appstore.bean.DetailInfo;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.view.Kanner;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailFragment extends BaseFragment implements View.OnClickListener {
    private ImageView mIcon;
    private Button mDownload;
    private TextView mAppName;
    private TextView mAppCompany;
    private RatingBar mCommentStar;
    private TextView mContent;
    private Kanner mKanner;
    private TextView mPromulgator;
    private TextView mType;
    private TextView mSize;
    private int mFromFragment;

    private String mData;
    private String mNetStr;
    private DetailContentInfo mContentInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initData();

        initFragment();
    }

    private void initFragment() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        CommentFragment commentFragment = new CommentFragment();
        commentFragment.setDatas(Constants.getComment());
        commentFragment.setAll(false);

        AppTypeFragment appTypeFragment = new AppTypeFragment();
//        appTypeFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.fragment_detail_comment, commentFragment);
        transaction.replace(R.id.fragment_detail_morelove, appTypeFragment);

        transaction.commit();
    }

    private void initData() {
        mKanner.setImagesUrl(Constants.getString());
        mKanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(new GetData()).start();
    }

    private void initView(View view) {
        mIcon = (ImageView) view.findViewById(R.id.fragment_detail_icon);
        mDownload = (Button) view.findViewById(R.id.fragment_detail_download);
        mAppName = (TextView) view.findViewById(R.id.fragment_detail_appName);
        mAppCompany = (TextView) view.findViewById(R.id.fragment_detail_appCompany);
        mCommentStar = (RatingBar) view.findViewById(R.id.fragment_detail_commentstar);
        mContent = (TextView) view.findViewById(R.id.fragment_detail_content);
        mKanner = (Kanner) view.findViewById(R.id.fragment_detail_kanner);
        mPromulgator = (TextView) view.findViewById(R.id.fragment_detail_promulgator);
        mType = (TextView) view.findViewById(R.id.fragment_detail_type);
        mSize = (TextView) view.findViewById(R.id.fragment_detail_size);

        mDownload.setOnClickListener(this);
    }

    public void setDatas(String data) {
        if (!TextUtils.isEmpty(data)) {
            mData = data;
        }
    }

    @Override
    public void onClick(View view) {
        if (mContentInfo != null) {
            MainActivity.mBinder.addTask(mContentInfo.getId() + "",
                    Constants.BASEURL + "/" + mContentInfo.getDownloadUrl(),
                    FileHelper.getNameFromUrl(mContentInfo.getDownloadUrl()));
        }
    }

    private class GetData implements Runnable {
        @Override
        public void run() {
            mNetStr = NetUtils.getNetStr(getActivity(), "/detail/" + mData);
            if (!TextUtils.isEmpty(mNetStr)) {
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    loadData();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void loadData() {
        try {
            DetailInfo detailInfo = new DetailInfo(new JSONObject(mNetStr));
            mContentInfo = detailInfo.getDetailContentInfo();
            if (mContentInfo != null) {
                Picasso.with(getActivity()).
                        load(Constants.BASEURL + "/" + mContentInfo.getIconUrl()).into(mIcon);
                mAppName.setText(mContentInfo.getName());
                mAppCompany.setText(mContentInfo.getCompany());
                mCommentStar.setRating(mContentInfo.getStar());
                mContent.setText(mContentInfo.getContent());
                mPromulgator.setText(getActivity().getString(
                        R.string.promulgator) + mContentInfo.getPromulgator());
                mType.setText(getActivity().getString(R.string.type) + mContentInfo.getType());
                mSize.setText(getActivity().getString(R.string.size) + mContentInfo.getFileSize());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}