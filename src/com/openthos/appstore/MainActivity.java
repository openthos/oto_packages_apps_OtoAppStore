package com.openthos.appstore;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.bean.SQLAppInstallInfo;
import com.openthos.appstore.fragment.GameFragment;
import com.openthos.appstore.fragment.HomeFragment;
import com.openthos.appstore.fragment.ManagerFragment;
import com.openthos.appstore.fragment.SoftwareFragment;
import com.openthos.appstore.fragment.item.CommentFragment;
import com.openthos.appstore.fragment.item.DetailFragment;
import com.openthos.appstore.fragment.item.MoreFragment;
import com.openthos.appstore.fragment.item.SearchFragment;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.download.DownLoadService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    public static Handler mHandler;
    public static List<SQLAppInstallInfo> mAppPackageInfo;
    public static DownLoadService.AppStoreBinder binder;
    private RadioGroup mRadioGroup;
    private FragmentManager mManager;
    private long mTime;
    private Fragment mCurrentFragment;
    private List<Integer> mPage;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder != null) {
                binder = (DownLoadService.AppStoreBinder) iBinder;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService(new Intent(this, DownLoadService.class), conn, Context.BIND_AUTO_CREATE);

        initView();

        initData();

        initListener();

        mHandler.sendEmptyMessage(Constants.HOME_FRAGMENT);

//        new DownloadKeeper(this).deleteAllDownLoadInfo();
    }

    private void initData() {
        mManager = getSupportFragmentManager();
        mPage = new ArrayList<>();
        initHandler();
        try {
            mAppPackageInfo = AppUtils.getAppPackageInfo(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        ImageView back = (ImageView) findViewById(R.id.activity_title_back);
        ImageView forward = (ImageView) findViewById(R.id.activity_title_forward);
        ImageView search = (ImageView) findViewById(R.id.activity_title_search);
        forward.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        final EditText content = (EditText) findViewById(R.id.activity_title_content);
        final Intent[] intent = {null};
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPage.size() > 1) {
                    mPage.remove(mPage.size() - 1);
                    mHandler.sendEmptyMessage(mPage.get(mPage.size() - 1));
                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        Message message = mHandler.obtainMessage();
                        message.what = Constants.SEARCH_FRAGMENT;
                        message.obj = contents;
                        mHandler.sendMessage(message);
                    } else {
                        content.setVisibility(View.GONE);
                        Tools.toast(MainActivity.this, getString(R.string.toast_search));
                    }
                }
            }
        });
    }

    private void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.main_radioGroup);
        mRadioGroup.setOnCheckedChangeListener(this);

        if (mRadioGroup != null) {
            int[] drawables = new int[] {
                    R.drawable.select_home_drawable,
                    R.drawable.select_software_drawable,
                    R.drawable.select_game_drawable,
                    R.drawable.select_manager_drawable
            };

            int[] rids = new int[] {
                    R.id.rb_home,
                    R.id.rb_software,
                    R.id.rb_game,
                    R.id.rb_manager
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
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_home:
                mHandler.sendEmptyMessage(Constants.HOME_FRAGMENT);
                break;
            case R.id.rb_software:
                mHandler.sendEmptyMessage(Constants.SOFTWARE_FRAGMENT);
                break;
            case R.id.rb_game:
                mHandler.sendEmptyMessage(Constants.GAME_FRAGMENT);
                break;
            case R.id.rb_manager:
                mHandler.sendEmptyMessage(Constants.MANAGER_FRAGMENT);
                break;
        }
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                FragmentTransaction transaction = mManager.beginTransaction();
//                mCurrentFragment = getCurrentFragment();
//                if (mCurrentFragment != null) {
//                    transaction.hide(mCurrentFragment);
//                }
                int what = msg.what;
                Fragment fragment = mManager.findFragmentByTag(what + "");
                switch (what) {
                    case Constants.HOME_FRAGMENT:
                        if (fragment == null) {
                            fragment = new HomeFragment();
                            addFragment(transaction, fragment, what);
                        }
                        break;
                    case Constants.SOFTWARE_FRAGMENT:
                        if (fragment == null) {
                            fragment = new SoftwareFragment();
                            addFragment(transaction, fragment, what);
                        }
                        break;
                    case Constants.GAME_FRAGMENT:
                        if (fragment == null) {
                            fragment = new GameFragment();
                            addFragment(transaction, fragment, what);
                        }
                        break;
                    case Constants.MANAGER_FRAGMENT:
                        if (fragment == null) {
                            fragment = new ManagerFragment();
                            addFragment(transaction, fragment, what);
                        }
                        break;
                    case Constants.DETAIL_FRAGMENT:
                        if (fragment == null) {
                            fragment = new DetailFragment();
                            addFragment(transaction, fragment, what);
                        }
                        break;
                    case Constants.MORE_FRAGMENT:
                        if (fragment == null) {
                            fragment = new MoreFragment();
                            addFragment(transaction, fragment, what);
                        }

                        if (getData(msg) != null) {
                            ((MoreFragment) fragment).setData((AppLayoutInfo) getData(msg));
                        }
                        break;
                    case Constants.COMMENT_FRAGMENT:
                        if (fragment == null) {
                            fragment = new CommentFragment();
                            addFragment(transaction, fragment, what);
                        }

//                        if (getData(msg) != null) {
                        ((CommentFragment) fragment).setDatas(Constants.getComment());
                        ((CommentFragment) fragment).setAll(true);
//                        }
                        break;
                    case Constants.SEARCH_FRAGMENT:
                        if (fragment == null) {
                            fragment = new SearchFragment();
                            addFragment(transaction, fragment, what);
                        }
                        if (getData(msg) != null) {
                            ((SearchFragment) fragment).setDatas((String) getData(msg));
                        }
                        break;
                }
//                transaction.show(fragment);
                mPage.add(what);
                transaction.commit();
            }
        };
    }

    private void addFragment(FragmentTransaction transaction, Fragment fragment, int what) {
        transaction.replace(R.id.main_fragment_container, fragment, what + "");
    }

    private Fragment getCurrentFragment() {
        List<Fragment> fragments = mManager.getFragments();
        if (fragments != null && fragments.size() != 0) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) {
                    return fragment;
                }
            }
        }
        return null;
    }

    private Object getData(Message msg) {
        if (msg.obj != null) {
            return msg.obj;
        }
        Bundle data = msg.getData();
        if (data == null) {
            return null;
        }
        switch (msg.what) {
            case Constants.MORE_FRAGMENT:
                return (AppLayoutInfo) data.getSerializable(Constants.APP_LAYOUT_INFO);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        stopService(new Intent(this, DownLoadService.class));
    }
}
