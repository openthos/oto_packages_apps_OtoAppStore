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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.DataInfo;
import com.openthos.appstore.bean.AppLayoutGridviewInfo;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.bean.SQLAppInstallInfo;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.fragment.GameFragment;
import com.openthos.appstore.fragment.HomeFragment;
import com.openthos.appstore.fragment.ManagerFragment;
import com.openthos.appstore.fragment.SoftwareFragment;
import com.openthos.appstore.fragment.item.CommentFragment;
import com.openthos.appstore.fragment.item.DetailFragment;
import com.openthos.appstore.fragment.item.MoreFragment;
import com.openthos.appstore.fragment.item.SearchFragment;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.download.DownLoadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    private static final String HANDLER_WHAT = "what";
    public static Handler mHandler;
    public static List<SQLAppInstallInfo> mAppPackageInfo;
    public static DownLoadService.AppStoreBinder mBinder;
    //    public static Map<String, Integer> mDownloadStateMap;
    private RadioGroup mRadioGroup;
    private FragmentManager mManager;
    private Fragment mCurrentFragment;
    private ArrayList<Integer> mPage;
    private int mWhat = Constants.MANAGER_FRAGMENT;
    private Map<Integer, Fragment> mFragments;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder != null) {
                mBinder = (DownLoadService.AppStoreBinder) iBinder;
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

        init();

        mHandler.sendEmptyMessage(Constants.HOME_FRAGMENT);

        saveAllData();

//        new DownloadKeeper(this).deleteAllDownLoadInfo();
    }

    private void init() {
        bindService(new Intent(this, DownLoadService.class), conn, Context.BIND_AUTO_CREATE);

        initView();

        initData();

        initListener();
    }

    private void initData() {
        mManager = getSupportFragmentManager();
        mPage = new ArrayList<>();
        mFragments = new HashMap<>();
//        mDownloadStateMap = new HashMap<>();
        try {
            mAppPackageInfo = AppUtils.getAppPackageInfo(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        initHandler();
    }

    private void initListener() {
        ImageView back = (ImageView) findViewById(R.id.activity_title_back);
        ImageView forward = (ImageView) findViewById(R.id.activity_title_forward);
        forward.setVisibility(View.GONE);
        final EditText content = (EditText) findViewById(R.id.activity_title_content);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString())) {
                    Message message = mHandler.obtainMessage();
                    message.what = Constants.SEARCH_FRAGMENT;
                    message.obj = editable.toString();
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void checked() {
        if (mPage.size() > 1) {
            mPage.remove(mPage.size() - 1);
            Integer what = mPage.get(mPage.size() - 1);
            Tools.printLog("MAc", "get" + what);
            if (what == Constants.SEARCH_FRAGMENT) {
                checked();
            } else {
                mHandler.sendEmptyMessage(what);
            }
        }
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
        FragmentTransaction transaction = mManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        Fragment fragment = null;
        switch (checkedId) {
            case R.id.rb_home:
                fragment = mFragments.get(Constants.HOME_FRAGMENT);
                if (fragment == null) {
                    fragment = new HomeFragment();
                    mFragments.put(Constants.HOME_FRAGMENT, fragment);
                }
                addFragment(transaction, fragment, Constants.HOME_FRAGMENT);
                break;
            case R.id.rb_software:
                fragment = mFragments.get(Constants.SOFTWARE_FRAGMENT);
                if (fragment == null) {
                    fragment = new SoftwareFragment();
                    mFragments.put(Constants.SOFTWARE_FRAGMENT, fragment);
                }
                addFragment(transaction, fragment, Constants.SOFTWARE_FRAGMENT);
                break;
            case R.id.rb_game:
                fragment = mFragments.get(Constants.GAME_FRAGMENT);
                if (fragment == null) {
                    fragment = new GameFragment();
                    mFragments.put(Constants.GAME_FRAGMENT, fragment);
                }
                addFragment(transaction, fragment, Constants.GAME_FRAGMENT);
                break;
            case R.id.rb_manager:
                fragment = mFragments.get(Constants.MANAGER_FRAGMENT);
                if (fragment == null) {
                    fragment = new ManagerFragment();
                    mFragments.put(Constants.MANAGER_FRAGMENT, fragment);
                }
                addFragment(transaction, fragment, Constants.MANAGER_FRAGMENT);
                break;
        }
        transaction.commit();
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
                Fragment fragment = null;
                switch (msg.what) {
                    case Constants.HOME_FRAGMENT:
                        fragment = mFragments.get(Constants.HOME_FRAGMENT);
                        if (fragment == null) {
                            fragment = new HomeFragment();
                            mFragments.put(Constants.HOME_FRAGMENT, fragment);
                        }
                        addFragment(transaction, fragment, msg.what);
                        break;
                    case Constants.SOFTWARE_FRAGMENT:
                        fragment = mFragments.get(Constants.SOFTWARE_FRAGMENT);
                        if (fragment == null) {
                            fragment = new SoftwareFragment();
                            mFragments.put(Constants.SOFTWARE_FRAGMENT, fragment);
                        }
                        addFragment(transaction, fragment, msg.what);
                        break;
                    case Constants.GAME_FRAGMENT:
                        fragment = mFragments.get(Constants.GAME_FRAGMENT);
                        if (fragment == null) {
                            fragment = new GameFragment();
                            mFragments.put(Constants.GAME_FRAGMENT, fragment);
                        }
                        addFragment(transaction, fragment, msg.what);
                        break;
                    case Constants.MANAGER_FRAGMENT:
                        fragment = mFragments.get(Constants.MANAGER_FRAGMENT);
                        if (fragment == null) {
                            fragment = new ManagerFragment();
                            mFragments.put(Constants.MANAGER_FRAGMENT, fragment);
                        }
                        addFragment(transaction, fragment, msg.what);
                        break;
                    case Constants.DETAIL_FRAGMENT:
                        fragment = mFragments.get(Constants.DETAIL_FRAGMENT);
                        if (fragment == null) {
                            fragment = new DetailFragment();
                            mFragments.put(Constants.DETAIL_FRAGMENT, fragment);
                        }
                        addFragment(transaction, fragment, msg.what);
                        if (getData(msg) != null) {
                            ((DetailFragment) fragment).setDatas((String) getData(msg));
                        }
                        break;
                    case Constants.MORE_FRAGMENT:
                        fragment = mFragments.get(Constants.MORE_FRAGMENT);
                        if (fragment == null) {
                            fragment = new MoreFragment();
                            mFragments.put(Constants.MORE_FRAGMENT, fragment);
                        }
                        addFragment(transaction, fragment, msg.what);

                        if (getData(msg) != null) {
                            ((MoreFragment) fragment).setData((AppLayoutInfo) getData(msg));
                        }
                        break;
                    case Constants.COMMENT_FRAGMENT:
                        fragment = mFragments.get(Constants.COMMENT_FRAGMENT);
                        if (fragment == null) {
                            fragment = new CommentFragment();
                            mFragments.put(Constants.COMMENT_FRAGMENT, fragment);
                        }
                        addFragment(transaction, fragment, msg.what);

                        ((CommentFragment) fragment).setDatas(Constants.getComment());
                        ((CommentFragment) fragment).setAll(true);
                        break;
                    case Constants.SEARCH_FRAGMENT:
                        fragment = mFragments.get(Constants.SEARCH_FRAGMENT);
                        if (fragment == null) {
                            fragment = new SearchFragment();
                            mFragments.put(Constants.SEARCH_FRAGMENT, fragment);
                        }
                        addFragment(transaction, fragment, msg.what);
                        if (getData(msg) != null) {
                            ((SearchFragment) fragment).setDatas((String) getData(msg));
                        }
                        break;
                    case Constants.TOAST:
                        Tools.toast(MainActivity.this, (String) msg.obj);
                        break;
                    case Constants.REFRESH:
                        Fragment currentFragment = getCurrentFragment();
                        if (currentFragment != null) {
                            BaseFragment baseFragment = (BaseFragment) currentFragment;
                            baseFragment.refresh();
                        }
                        break;
                }
                if (msg.what != Constants.TOAST && msg.what != Constants.REFRESH) {
                    transaction.commit();
                    mWhat = msg.what;
                }
            }
        };
    }

    private void addFragment(FragmentTransaction transaction, Fragment fragment, int what) {
        Tools.printLog("MAc", "add" + what);
        if (mPage.size() > 1) {
            if (mPage.get(mPage.size() - 1) != what) {
                mPage.add(what);
            }
        } else {
            mPage.add(what);
        }
        Fragment currentFragment = getCurrentFragment();
        if (!fragment.isAdded()) {
            transaction.add(R.id.main_fragment_container, fragment, what + "");
        }
        if (currentFragment != null) {
            transaction.hide(currentFragment).show(fragment);
        } else {
            transaction.show(fragment);
        }
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
        finish();
    }

    private void saveAllData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String allData = NetUtils.getNetStr(MainActivity.this, "/all");
                if (!TextUtils.isEmpty(allData)) {
                    try {
                        DataInfo dataInfo = new DataInfo(new JSONObject(allData));
                        if (dataInfo != null && dataInfo.getAppList() != null) {
                            List<AppLayoutGridviewInfo> appList = dataInfo.getAppList();
                            for (int i = 0; i < appList.size(); i++) {
                                AppLayoutGridviewInfo appInfo = appList.get(i);
                                SPUtils.saveAllData(MainActivity.this, appInfo);
                            }
                        }
                        Tools.printLog("SA", dataInfo.getAppList().size() + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Constants.BASEURL = "http://dev.openthos.org/openthos/appstore";
                    allData = NetUtils.getNetStr(MainActivity.this, "/all");
                    if (!TextUtils.isEmpty(allData)) {
                        saveAllData();
                    }
                }
            }
        }).start();
    }
}