package com.openthos.appstore;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.bean.NetDataListInfo;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.download.DownloadService;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.fragment.CommentFragment;
import com.openthos.appstore.fragment.DetailFragment;
import com.openthos.appstore.fragment.GameFragment;
import com.openthos.appstore.fragment.HomeFragment;
import com.openthos.appstore.fragment.ManagerFragment;
import com.openthos.appstore.fragment.MoreFragment;
import com.openthos.appstore.fragment.SearchFragment;
import com.openthos.appstore.fragment.SoftwareFragment;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    public static Handler mHandler;
    public static DownloadService.AppStoreBinder mDownloadService;
    public HashMap<String, AppInstallInfo> mAppInstallMap;
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private RadioButton mManagerButton;
    private RadioButton mSoftwareButton;
    private RadioButton mGameButton;
    private RadioButton mHomeButton;
    private ImageView mBack;
    private ImageView mForward;
    private EditText mSearchText;
    private ImageView mSearchImg;
    private ScrollView mScrollView;
    private BaseFragment mCurrentFragment;
    private List<Integer> mPages;
    private boolean mIsSearch;

    private BroadcastReceiver mAppInstallBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAppInstallMap != null) {
                mAppInstallMap.clear();
                loadAppPackageInfo();
                mHandler.sendEmptyMessage(Constants.REFRESH);
            }
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder != null) {
                mDownloadService = (DownloadService.AppStoreBinder) iBinder;
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
//        initUrl();
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mScrollView = (ScrollView) findViewById(R.id.main_scrollview);
        mBack = (ImageView) findViewById(R.id.activity_title_back);
        mForward = (ImageView) findViewById(R.id.activity_title_forward);
        mSearchImg = (ImageView) findViewById(R.id.activity_title_search);
        mSearchText = (EditText) findViewById(R.id.activity_title_search_text);
        mHomeButton = (RadioButton) findViewById(R.id.rb_home);
        mGameButton = (RadioButton) findViewById(R.id.rb_game);
        mSoftwareButton = (RadioButton) findViewById(R.id.rb_software);
        mManagerButton = (RadioButton) findViewById(R.id.rb_manager);
        int[] drawables = new int[]{
                R.drawable.select_home_drawable,
                R.drawable.select_software_drawable,
                R.drawable.select_game_drawable,
                R.drawable.select_manager_drawable
        };
        int[] rids = new int[]{
                R.id.rb_home,
                R.id.rb_software,
                R.id.rb_game,
                R.id.rb_manager
        };
        Resources res = getResources();
        for (int i = 0; i < rids.length; i++) {
            Button rb = (Button) findViewById(rids[i]);
            rb.setOnClickListener(new HomeItemClick());
            Drawable drawable = res.getDrawable(drawables[i]);
            drawable.setBounds(0, 0, Constants.DRAWABLE_SIZE, Constants.DRAWABLE_SIZE);
            rb.setCompoundDrawablePadding(Constants.DRAWABLE_PADDING);
            rb.setCompoundDrawables(drawable, null, null, null);
        }
    }

    private void initListener() {
        mBack.setOnClickListener(this);
        mForward.setOnClickListener(this);
        mSearchImg.setOnClickListener(this);
        mSearchText.addTextChangedListener(new SearchTextWatcher());
    }

    private void initData() {
        bindService(new Intent(this, DownloadService.class), conn, Context.BIND_AUTO_CREATE);
        registerBroadcastReceiver();
        mManager = getSupportFragmentManager();
        mPages = new ArrayList<>();
        mAppInstallMap = new HashMap<>();
        mIsSearch = true;
        loadAppPackageInfo();
        initHandler();
        updateAllData();
        mHomeButton.performClick();
    }

    private void updateAllData() {
        new Thread() {
            @Override
            public void run() {
                String allData = NetUtils.getNetStr("/all");
                if (!TextUtils.isEmpty(allData)) {
                    try {
                        NetDataListInfo netDataInfos = new NetDataListInfo(new JSONObject(allData));
                        if (netDataInfos != null && netDataInfos.getNetDataInfoList() != null) {
                            List<AppItemInfo> appList = netDataInfos.getNetDataInfoList();
                            SPUtils.clearData(MainActivity.this, Constants.SP_ALL_DATA);
                            for (int i = 0; i < appList.size(); i++) {
                                AppItemInfo appInfo = appList.get(i);
                                SPUtils.saveAllData(MainActivity.this, appInfo);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendEmptyMessage(Constants.REFRESH);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.activity_title_back && mPages.size() > 1) {
            mPages.remove(mPages.size() - 1);
            mHandler.sendEmptyMessage(mPages.get(mPages.size() - 1));
            mPages.remove(mPages.size() - 1);
        }
        if (!TextUtils.isEmpty(mSearchText.getText())) {
            mIsSearch = false;
            mSearchText.clearFocus();
            mSearchText.setText("");
        }
    }

    private Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    private void initUrl() {
        Uri uriQuery = Uri.parse("content://com.otosoft.tools.myprovider/upgradeUrl");
        if (uriQuery != null) {
            Cursor cursor = getContentResolver().query(uriQuery, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                StoreApplication.mBaseUrl =
                        cursor.getString(cursor.getColumnIndex("upgradeUrl")) + "appstore";
                cursor.close();
            } else {
                StoreApplication.mBaseUrl = "http://dev.openthos.org/openthos/appstore";
            }
        }
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case Constants.REFRESH:
                        Fragment currentFragment = getCurrentFragment();
                        if (currentFragment != null) {
                            ((BaseFragment) currentFragment).refresh();
                        }
                        break;
                    case Constants.TOAST:
                        Tools.toast(MainActivity.this, (String) msg.obj);
                        break;
                    case Constants.INSTALL_APK:
                        installApk((String) msg.obj);
                        break;
                    default:
                        showFragment(msg);
                        break;
                }
            }
        };
    }

    private void showFragment(Message msg) {
        mTransaction = mManager.beginTransaction();
        BaseFragment fragment = getFragment(msg);
        if (fragment != null && !fragment.isAdded()) {
            mTransaction.add(R.id.main_fragment_container, fragment, String.valueOf(msg.what));
        }

        if (mCurrentFragment == null) {
            mTransaction.show(fragment);
        } else if (mCurrentFragment != fragment) {
            mTransaction.hide(mCurrentFragment).show(fragment);
        }
        mCurrentFragment = fragment;
        mTransaction.commit();
        mManager.executePendingTransactions();
        if (mPages.size() < 1 || mPages.get(mPages.size() - 1) != msg.what) {
            mPages.add(msg.what);
        }
        fragment.refresh();
        mScrollView.scrollTo(0, 0);
    }

    private BaseFragment getFragment(Message msg) {
        BaseFragment fragment = (BaseFragment) mManager.findFragmentByTag(String.valueOf(msg.what));
        mBack.setVisibility(View.VISIBLE);
        switch (msg.what) {
            case Constants.HOME_FRAGMENT:
                mBack.setVisibility(View.GONE);
                if (fragment == null) {
                    fragment = new HomeFragment(mAppInstallMap);
                }
                break;
            case Constants.SOFTWARE_FRAGMENT:
                mBack.setVisibility(View.GONE);
                mSoftwareButton.setChecked(true);
                if (fragment == null) {
                    fragment = new SoftwareFragment(mAppInstallMap);
                }
                break;
            case Constants.GAME_FRAGMENT:
                mBack.setVisibility(View.GONE);
                mGameButton.setChecked(true);
                if (fragment == null) {
                    fragment = new GameFragment(mAppInstallMap);
                }
                break;
            case Constants.MANAGER_FRAGMENT:
                mBack.setVisibility(View.GONE);
                mManagerButton.setChecked(true);
                if (fragment == null) {
                    fragment = new ManagerFragment(mAppInstallMap);
                }
                break;
            case Constants.DETAIL_FRAGMENT:
                if (fragment == null) {
                    fragment = new DetailFragment(mAppInstallMap);
                }
                fragment.setData(msg.obj);
                break;
            case Constants.MORE_FRAGMENT:
                if (fragment == null) {
                    fragment = new MoreFragment(mAppInstallMap);
                }
                fragment.setData(msg.obj);
                break;
            case Constants.COMMENT_FRAGMENT:
                if (fragment == null) {
                    fragment = new CommentFragment(mAppInstallMap);
                }
                break;
            case Constants.SEARCH_FRAGMENT:
                if (fragment == null) {
                    fragment = new SearchFragment(mAppInstallMap);
                }
                fragment.setData(msg.obj);
                break;
        }
        return fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        stopService(new Intent(this, DownloadService.class));
        unregisterReceiver(mAppInstallBroadCast);
        finish();
    }


    private void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        myIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        myIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        myIntentFilter.addDataScheme("package");
        registerReceiver(mAppInstallBroadCast, myIntentFilter);
    }

    private void loadAppPackageInfo() {
        AppInstallInfo appInfo = null;
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            PackageInfo packageInfo = pinfo.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appInfo = new AppInstallInfo();
                appInfo.setId(i);
                appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                appInfo.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                appInfo.setPackageName(packageInfo.packageName);
                appInfo.setVersionCode(packageInfo.versionCode);
                appInfo.setVersionName(packageInfo.versionName);
                appInfo.setState(Constants.APP_HAVE_INSTALLED);
                mAppInstallMap.put(packageInfo.packageName, appInfo);
            }
        }
    }

    class HomeItemClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
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
            if (!TextUtils.isEmpty(mSearchText.getText())) {
                mIsSearch = false;
                mSearchText.clearFocus();
                mSearchText.setText("");
            }
        }
    }

    class SearchTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mIsSearch) {
                mHandler.sendMessage(
                        mHandler.obtainMessage(Constants.SEARCH_FRAGMENT, editable.toString()));
            }
            mIsSearch = true;
        }
    }

    private void installApk(String apkFilePath) {
        File apkFile = new File(apkFilePath);
        if (!apkFile.exists() || apkFile.length() == 0) {
            Tools.toast(this, getString(R.string.this_file_is_not_exist));
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        }
    }
}
