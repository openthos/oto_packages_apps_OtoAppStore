package com.openthos.appstore.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLAppInstallInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-31.
 */
public class AppUtils {

    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static Drawable getAPKIcon(Context context, String absPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            applicationInfo.sourceDir = absPath;
            applicationInfo.publicSourceDir = absPath;
            Drawable apkIcon = pm.getApplicationIcon(applicationInfo);
            return apkIcon;
        }
        return null;
    }

    public static List<SQLAppInstallInfo> getAppPackageInfo(Context context) throws
            PackageManager.NameNotFoundException {

        List<SQLAppInstallInfo> datas = new ArrayList<>();
        SQLAppInstallInfo appInfo = null;

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            PackageInfo packageInfo = pinfo.get(i);

            String name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            String packageName = packageInfo.packageName;
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appInfo = new SQLAppInstallInfo();
                appInfo.setId(i);
                appInfo.setIcon(icon);
                appInfo.setName(name);
                appInfo.setPackageName(packageName);
                appInfo.setVersionCode(versionCode + "");
                appInfo.setVersionName(versionName);
                appInfo.setState(Constants.APP_HAVE_INSTALLED);
                datas.add(appInfo);
            }
        }
        return datas;
    }

    public static String installApk(Context mContext, String saveFileName) {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists() || apkfile.length() == 0) {
            return mContext.getString(R.string.this_file_is_not_exist);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        return "";
    }
}
