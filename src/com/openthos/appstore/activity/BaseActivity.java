package com.openthos.appstore.activity;

import android.app.Activity;
import android.os.Bundle;

import com.openthos.appstore.R;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
