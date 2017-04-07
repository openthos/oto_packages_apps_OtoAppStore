package com.openthos.appstore.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppInstallInfo;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {
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

    public static List<AppInstallInfo> getAppPackageInfo(Context context){
        List<AppInstallInfo> datas = new ArrayList<>();
        AppInstallInfo appInfo = null;

        PackageManager packageManager = context.getPackageManager();
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
                datas.add(appInfo);
            }
        }
        return datas;
    }

    public static void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }

    public static void startApk(Context context, String appPackageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }
}
