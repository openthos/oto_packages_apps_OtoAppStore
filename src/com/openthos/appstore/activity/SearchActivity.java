package com.openthos.appstore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.openthos.appstore.R;
import com.openthos.appstore.utils.ActivityTitileUtils;
import com.openthos.appstore.view.CustomListView;

public class SearchActivity extends BaseActivity {

    private EditText mText;
    private CustomListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActivityTitileUtils.checked(this, getIntent());
        ActivityTitileUtils.initActivityTitle(this);

        initView();

        initData();
    }

    private void initView() {
        mText = ((EditText) findViewById(R.id.activity_search_text));
        mListView = ((CustomListView) findViewById(R.id.activity_search_listview));
        ImageView search = (ImageView) findViewById(R.id.activity_title_search);
        search.setVisibility(View.GONE);
    }

    private void initData() {
        Intent intent = getIntent();
        String content = intent.getStringExtra("content");
        mText.setText(content);
    }
}
