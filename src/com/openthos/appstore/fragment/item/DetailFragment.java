package com.openthos.appstore.fragment.item;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.view.Kanner;

public class DetailFragment extends BaseFragment {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        loadData();

        initFragment();
    }

    private void initFragment() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        CommentFragment commentFragment = new CommentFragment();
        commentFragment.setDatas(Constants.getComment());
        commentFragment.setAll(false);

        AppTypeFragment appTypeFragment = new AppTypeFragment();
        appTypeFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.fragment_detail_comment, commentFragment);
        transaction.replace(R.id.fragment_detail_morelove, appTypeFragment);

        transaction.commit();
    }

    private void loadData() {
        mKanner.setImagesUrl(Constants.getString());
        mKanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
            }
        });
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
    }
}
