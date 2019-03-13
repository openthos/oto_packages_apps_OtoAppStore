package org.openthos.appstore;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.openthos.appstore.app.Constants;
import org.openthos.appstore.bean.AppItemInfo;
import org.openthos.appstore.bean.AppInstallInfo;
import org.openthos.appstore.download.DownloadService;
import org.openthos.appstore.fragment.BaseFragment;
import org.openthos.appstore.fragment.CommentFragment;
import org.openthos.appstore.fragment.DetailFragment;
import org.openthos.appstore.fragment.GameFragment;
import org.openthos.appstore.fragment.HomeFragment;
import org.openthos.appstore.fragment.ManagerFragment;
import org.openthos.appstore.fragment.MoreFragment;
import org.openthos.appstore.fragment.SearchFragment;
import org.openthos.appstore.fragment.SoftwareFragment;
import org.openthos.appstore.utils.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    public static Handler mHandler;
    public static DownloadService.AppStoreBinder mDownloadService;
    public HashMap<String, AppInstallInfo> mAllAppMap;
    public List<AppInstallInfo> mAppInstallInfos;
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
    private RelativeLayout mSearchBox;
    private Fragment mCurrentFragment;
    private List<Integer> mPages;
    private boolean mIsSearch;
    private List<Integer> mFragmentFlags = new ArrayList<>();
    public List<AppItemInfo> mDataSource = new ArrayList<>();
    public Map<String, AppItemInfo> mAllAppItemInfos = new HashMap<>();

    private BroadcastReceiver mAppInstallBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAllAppMap != null) {
                mAllAppMap.clear();
                mAppInstallInfos.clear();
                loadAllAppInfos();
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
        if (savedInstanceState != null) {
            savedInstanceState.clear();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        mSearchBox = (RelativeLayout) findViewById(R.id.searchbox);
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
        mAllAppMap = new HashMap<>();
        mAppInstallInfos = new ArrayList<>();
        mIsSearch = true;
        loadAllAppInfos();
        initHandler();
        mHandler.sendEmptyMessage(Constants.HOME_FRAGMENT);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.activity_title_search) {
            return;
        }
        onBackPressed();
        if (!TextUtils.isEmpty(mSearchText.getText())) {
            mIsSearch = false;
            mSearchText.clearFocus();
            mSearchText.setText("");
        }
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
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
                        installApk((AppItemInfo) msg.obj);
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
                mHomeButton.setChecked(true);
                if (fragment == null) {
                    fragment = new HomeFragment();
                }
                if (mFragmentFlags.size() == 0) {
                    mFragmentFlags.add(msg.what);
                } else {
                    mFragmentFlags.set(0, msg.what);
                }
                break;
            case Constants.SOFTWARE_FRAGMENT:
                mBack.setVisibility(View.GONE);
                mSoftwareButton.setChecked(true);
                if (fragment == null) {
                    fragment = new SoftwareFragment();
                }
                mFragmentFlags.set(0, msg.what);
                break;
            case Constants.GAME_FRAGMENT:
                mBack.setVisibility(View.GONE);
                mGameButton.setChecked(true);
                if (fragment == null) {
                    fragment = new GameFragment();
                }
                mFragmentFlags.set(0, msg.what);
                break;
            case Constants.MANAGER_FRAGMENT:
                mBack.setVisibility(View.GONE);
                mManagerButton.setChecked(true);
                if (fragment == null) {
                    fragment = new ManagerFragment();
                }
                mFragmentFlags.set(0, msg.what);
                break;
            case Constants.DETAIL_FRAGMENT:
                if (fragment == null) {
                    fragment = new DetailFragment();
                }
                fragment.setData(msg.obj);
                mFragmentFlags.add(msg.what);
                break;
            case Constants.MORE_FRAGMENT:
                mDataSource.clear();
                mSearchBox.setVisibility(View.VISIBLE);
                if (fragment == null) {
                    fragment = new MoreFragment();
                }
                fragment.setData(msg.obj);
                mFragmentFlags.add(msg.what);
                break;
            case Constants.COMMENT_FRAGMENT:
                if (fragment == null) {
                    fragment = new CommentFragment();
                }
                mFragmentFlags.add(msg.what);
                break;
            case Constants.SEARCH_FRAGMENT:
                if (fragment == null) {
                    fragment = new SearchFragment();
                }
                fragment.setData(msg.obj);
                mFragmentFlags.add(msg.what);
                break;
        }
        return fragment;
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        stopService(new Intent(this, DownloadService.class));
        unregisterReceiver(mAppInstallBroadCast);
//        finish();
        mCurrentFragment = null;
        super.onDestroy();
    }


    private void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        myIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        myIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        myIntentFilter.addDataScheme("package");
        registerReceiver(mAppInstallBroadCast, myIntentFilter);
    }

    private void loadAllAppInfos() {
        AppInstallInfo appInfo = null;
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> allAppList = packageManager.queryIntentActivities(intent, 0);
        PackageInfo packageInfo = null;
        String packageName;
        for (int i = 0; i < allAppList.size(); i++) {
            packageName = allAppList.get(i).activityInfo.packageName;
            try {
                packageInfo = packageManager.getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            appInfo = new AppInstallInfo();
            appInfo.setId(i);
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            appInfo.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            appInfo.setPackageName(packageInfo.packageName);
            appInfo.setVersionCode(packageInfo.versionCode);
            appInfo.setVersionName(packageInfo.versionName);
            appInfo.setState(Constants.APP_HAVE_INSTALLED);
            appInfo.setLastUpdateTime(packageInfo.lastUpdateTime);
            mAppInstallInfos.add(appInfo);
            mAllAppMap.put(packageInfo.packageName, appInfo);
        }
    }

    class HomeItemClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mDataSource.clear();
            switch (view.getId()) {
                case R.id.rb_home:
                    mSearchBox.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(Constants.HOME_FRAGMENT);
                    break;
                case R.id.rb_software:
                    mSearchBox.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(Constants.SOFTWARE_FRAGMENT);
                    break;
                case R.id.rb_game:
                    mSearchBox.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(Constants.GAME_FRAGMENT);
                    break;
                case R.id.rb_manager:
                    mSearchBox.setVisibility(View.INVISIBLE);
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

    private void installApk(final AppItemInfo appInfo) {
        File apkFile = new File(appInfo.getFilePath());
        if (!apkFile.exists() || apkFile.length() == 0) {
            mDownloadService.startTask(appInfo.getTaskId());
        } else {
            boolean isSameSignature = true;
            try {
                PackageInfo infoFromApk = getPackageManager().getPackageArchiveInfo(
                        apkFile.getAbsolutePath(), PackageManager.GET_SIGNATURES);
                PackageInfo infoFromPM = getPackageManager().getPackageInfo(
                        appInfo.getPackageName(), PackageManager.GET_SIGNATURES);
                Signature[] sigsFromApk = infoFromApk.signatures;
                Signature[] sigsFromPM = infoFromPM.signatures;
                isSameSignature = sigsFromApk[0].toCharsString().equals(sigsFromPM[0].toCharsString());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (isSameSignature) {
                PackageManager packageManager = getPackageManager();
                android.app.PackageInstallObserver PIO = new android.app.PackageInstallObserver() {
                    @Override
                    public void onPackageInstalled(String basePackageName, int returnCode,
                            String msg, Bundle extras) {
                        int tmpRes = 0;
                        if (returnCode != 1) {
                            tmpRes = R.string.failed_to_install;
                        } else {
                            tmpRes = R.string.success_to_install;
                        }
			final int res = tmpRes;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, appInfo.getPackageName()
                                        + getString(res), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                };
                packageManager.installPackage(Uri.fromFile(apkFile), PIO,
                        PackageManager.INSTALL_REPLACE_EXISTING, null);
                //Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //if (android.os.Build.VERSION.SDK_INT >= 24) {
                //    intent.setDataAndType(FileProvider.getUriForFile(this,
                //            "org.openthos.support.fileprovider", apkFile),
                //            "application/vnd.android.package-archive");
                //} else {
                //    intent.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                //            "application/vnd.android.package-archive");
                //}
                //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //startActivity(intent);
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_warning));
            builder.setMessage(getString(R.string.dialog_warning_uninstall));
            builder.setPositiveButton(getString(R.string.continues), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            BufferedReader in = null;
                            try {
                                Process pro = Runtime.getRuntime().exec(new String[]{"su", "-c",
                                        "pm uninstall --user 0 " + appInfo.getPackageName()});
                                in = new BufferedReader(
                                        new InputStreamReader(pro.getInputStream()));
                                String line;
                                while ((line = in.readLine()) != null) {
                                }
                                installApk(appInfo);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    }

    @Override
    public void onBackPressed() {
        if (mFragmentFlags.size() > 1) {
            Integer mesWhat = mFragmentFlags.get(mFragmentFlags.size() - 2);
            Fragment fragment = mManager.findFragmentByTag(String.valueOf(mesWhat));
            switch (mesWhat) {
                case Constants.HOME_FRAGMENT:
                    mBack.setVisibility(View.GONE);
                    mHomeButton.setChecked(true);
                    break;
                case Constants.SOFTWARE_FRAGMENT:
                    mBack.setVisibility(View.GONE);
                    mSoftwareButton.setChecked(true);
                    break;
                case Constants.GAME_FRAGMENT:
                    mBack.setVisibility(View.GONE);
                    mGameButton.setChecked(true);
                    break;
                case Constants.MANAGER_FRAGMENT:
                    mBack.setVisibility(View.GONE);
                    mManagerButton.setChecked(true);
                    break;
            }
            mManager.beginTransaction().hide(mCurrentFragment).show(fragment).commit();
            mCurrentFragment = fragment;
            mFragmentFlags.remove(mFragmentFlags.size() - 1);
        } else {
            super.onBackPressed();
        }
    }
}
