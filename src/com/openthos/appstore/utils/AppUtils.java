package com.openthos.appstore.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import com.openthos.appstore.bean.SQLAppInstallInfo;
import android.content.Intent;
import android.net.Uri;
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

    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);

        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                PackageInfo packageInfo = pinfo.get(i);
                String pn = packageInfo.packageName;
                Signature[] signatures = packageInfo.signatures;
                int versionCode = packageInfo.versionCode;

                pName.add(pn);
            }
        }
        return pName.contains(packageName);
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
                datas.add(appInfo);
            }
        }
        return datas;
    }

    public static boolean installApk(Context mContext, String saveFileName) {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return false;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
        return true;
    }

    public static void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }
}
