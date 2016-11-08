package com.openthos.appstore;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.openthos.appstore.activity.BaseActivity;
import com.openthos.appstore.activity.SearchActivity;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;
import com.openthos.appstore.fragment.GameFragment;
import com.openthos.appstore.fragment.HomeFragment;
import com.openthos.appstore.fragment.ManagerFragment;
import com.openthos.appstore.fragment.SoftwareFragment;
import com.openthos.appstore.utils.ActivityTitileUtils;
import com.openthos.appstore.utils.GetFragment;
import com.openthos.appstore.utils.Tools;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private HomeFragment mHomeFragment;
    private SoftwareFragment mSoftwareFragment;
    private GameFragment mGameFragment;
    private ManagerFragment mManagerFragment;
    private RadioGroup mRadioGroup;
    private Fragment[] mFragments;
    private long mTime;
    private int mFromFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFromFragment = ActivityTitileUtils.checked(this, getIntent());
        initView(mFromFragment);

        loadData();

        initListener();
    }

    private void initListener() {
        ImageView back = (ImageView) findViewById(R.id.activity_title_back);
        ImageView forward = (ImageView) findViewById(R.id.activity_title_forward);
        ImageView search = (ImageView) findViewById(R.id.activity_title_search);
        final EditText content = (EditText) findViewById(R.id.activity_title_content);
        final Intent[] intent = {null};
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - mTime > Constants.DELAY_TIME_2) {
                    Tools.toast(MainActivity.this, getString(R.string.exit_app));
                    mTime = System.currentTimeMillis();
                } else {
                    for (int i = 0; i < StoreApplication.activities.size(); i++) {
                        StoreApplication.activities.get(i).finish();
                    }
                    finish();
                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = StoreApplication.activities.size() - 1;
                if (size > 0) {
                    intent[0] = new Intent(MainActivity.this,
                                             StoreApplication.activities.get(size - 1).getClass());
                    startActivity(intent[0]);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getVisibility() == View.GONE) {
                    content.setVisibility(View.VISIBLE);
                } else {
                    String contents = content.getText().toString();
                    if (!TextUtils.isEmpty(contents)) {
                        intent[0] = new Intent(MainActivity.this, SearchActivity.class);
                        intent[0].putExtra("content", contents);
                        content.setVisibility(View.GONE);
                        startActivity(intent[0]);
                    } else {
                        content.setVisibility(View.GONE);
                        Tools.toast(MainActivity.this, getString(R.string.toast_search));
                    }
                }
            }
        });
    }

    private void loadData() {

    }

    private void initView(int fragmentNum) {
        mRadioGroup = (RadioGroup) findViewById(R.id.main_radioGroup);
        mRadioGroup.setOnCheckedChangeListener(this);

        if (mRadioGroup != null) {
            int[] drawables = new int[] {
                R.drawable.select_home_drawable,
                R.drawable.select_software_drawable,
                R.drawable.select_game_drawable,
                R.drawable.select_manager_drawable,
            };

            int[] rids = new int[] {
                R.id.rb_home,
                R.id.rb_software,
                R.id.rb_game,
                R.id.rb_manager,
            };
            Resources res = getResources();
            for (int i = 0; i < rids.length; i++) {
                RadioButton rb = (RadioButton) mRadioGroup.findViewById(rids[i]);
                Drawable drawable = res.getDrawable(drawables[i]);
                drawable.setBounds(0, 0, Constants.DRAWABLE_SIZE, Constants.DRAWABLE_SIZE);
                rb.setCompoundDrawablePadding(Constants.DRAWABLE_PADDING);
                rb.setCompoundDrawables(drawable, null, null, null);
            }
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        mFragments = new Fragment[Constants.FRAGMENT_COUNT];
        mHomeFragment = new HomeFragment();
        mSoftwareFragment = new SoftwareFragment();
        mGameFragment = new GameFragment();
        mManagerFragment = new ManagerFragment();

        mFragments[Constants.HOME_FRAGMENT] = mHomeFragment;
        mFragments[Constants.SOFTWARE_FRAGMENT] = mSoftwareFragment;
        mFragments[Constants.GAME_FRAGMENT] = mGameFragment;
        mFragments[Constants.MANAGER_FRAGMENT] = mManagerFragment;

        transaction.replace(R.id.main_fragment_container, mFragments[fragmentNum]);

        transaction.commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = null;
        switch (checkedId) {
            case R.id.rb_home:
                fragment = GetFragment.getFragment(Constants.HOME_FRAGMENT);
                break;
            case R.id.rb_software:
                fragment = GetFragment.getFragment(Constants.SOFTWARE_FRAGMENT);
                break;
            case R.id.rb_game:
                fragment = GetFragment.getFragment(Constants.GAME_FRAGMENT);
                break;
            case R.id.rb_manager:
                fragment = GetFragment.getFragment(Constants.MANAGER_FRAGMENT);
                break;
        }
        if (fragment != null) {
            transaction.replace(R.id.main_fragment_container, fragment);
        }
        transaction.commit();
    }
}
