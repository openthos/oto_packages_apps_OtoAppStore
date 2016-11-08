package com.openthos.appstore.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.item.AppTypeFragment;
import com.openthos.appstore.fragment.item.CommentFragment;
import com.openthos.appstore.utils.ActivityTitileUtils;
import com.openthos.appstore.view.Kanner;

public class DetailActivity extends BaseActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mFromFragment = ActivityTitileUtils.checked(this, getIntent());
        ActivityTitileUtils.initActivityTitle(this);

        initView();

        loadData();

        initFragment();
    }

    private void initFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        CommentFragment commentFragment = new CommentFragment();
        commentFragment.setDatas(Constants.getComment());
        commentFragment.setAll(false);
        commentFragment.setFromFragment(mFromFragment);

        AppTypeFragment appTypeFragment = new AppTypeFragment();
        appTypeFragment.setFromFragment(mFromFragment);
        appTypeFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.activity_detail_comment, commentFragment);
        transaction.replace(R.id.activity_detail_morelove, appTypeFragment);

        transaction.commit();
    }

    private void loadData() {
        mKanner.setImagesUrl(Constants.getString());
        mKanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(DetailActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        mIcon = (ImageView) findViewById(R.id.activity_detail_icon);
        mDownload = (Button) findViewById(R.id.activity_detail_download);
        mAppName = (TextView) findViewById(R.id.activity_detail_appName);
        mAppCompany = (TextView) findViewById(R.id.activity_detail_appCompany);
        mCommentStar = (RatingBar) findViewById(R.id.activity_detail_commentstar);
        mContent = (TextView) findViewById(R.id.activity_detail_content);
        mKanner = (Kanner) findViewById(R.id.activity_detail_kanner);
        mPromulgator = (TextView) findViewById(R.id.activity_detail_promulgator);
        mType = (TextView) findViewById(R.id.activity_detail_type);
        mSize = (TextView) findViewById(R.id.activity_detail_size);
    }
}
