package com.openthos.appstore.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.item.CommentFragment;
import com.openthos.appstore.utils.ActivityTitileUtils;

public class CommentActivity extends BaseActivity {

    private int mFromFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        int mFromFragment = ActivityTitileUtils.checked(this, getIntent());
        ActivityTitileUtils.initActivityTitle(this);

        initView();

        loadData();

        initFragment();
    }

    private void initView() {

    }

    private void initFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        CommentFragment commentFragment = new CommentFragment();
        commentFragment.setDatas(Constants.getComment());
        commentFragment.setAll(true);
        commentFragment.setFromFragment(mFromFragment);

        transaction.replace(R.id.activity_comment_frameLayout, commentFragment);

        transaction.commit();
    }

    private void loadData() {

    }
}
