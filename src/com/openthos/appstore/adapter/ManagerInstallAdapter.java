package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.openthos.appstore.R;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.download.DownloadService;
import com.openthos.appstore.utils.AppUtils;
import java.util.List;

public class ManagerInstallAdapter extends BasicAdapter implements View.OnClickListener {
    private DownloadManager mManager;

    public ManagerInstallAdapter(Context context, List<AppInstallInfo> datas) {
        super(context);
        mManager = DownloadService.getDownloadManager();
        mDatas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.item_update, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            AppInstallInfo appInstallInfo = (AppInstallInfo) mDatas.get(position);
            holder.appIcon.setImageDrawable(appInstallInfo.getIcon());
            holder.appName.setText(appInstallInfo.getName());
            holder.category.setText("");
            holder.appVersion.setText(
                    appInstallInfo.getVersionName() + "(" + appInstallInfo.getVersionCode() + ")");
            holder.appContent.setText(appInstallInfo.getPackageName());
            holder.uninstall.setOnClickListener(this);
            holder.open.setOnClickListener(this);
            holder.uninstall.setTag(appInstallInfo);
            holder.open.setTag(appInstallInfo);
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        AppInstallInfo appInstallInfo = (AppInstallInfo) view.getTag();
        switch (view.getId()) {
            case R.id.item_update_uninstall:
                AppUtils.uninstallApk(mContext, appInstallInfo.getPackageName());
                break;
            case R.id.item_update_open:
                AppUtils.openApp(mContext, appInstallInfo.getPackageName());
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshLayout() {
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private ImageView appIcon;
        private TextView appName;
        private TextView appVersion;
        private TextView appContent;
        private TextView category;
        private Button open;
        private Button uninstall;

        public ViewHolder(View view) {
            appIcon = ((ImageView) view.findViewById(R.id.item_update_appIcon));
            appName = (TextView) view.findViewById(R.id.item_update_appName);
            category = (TextView) view.findViewById(R.id.item_update_category);
            appVersion = (TextView) view.findViewById(R.id.item_update_version);
            appContent = (TextView) view.findViewById(R.id.item_update_newFeature);
            open = ((Button) view.findViewById(R.id.item_update_open));
            uninstall = ((Button) view.findViewById(R.id.item_update_uninstall));
            open.setVisibility(View.VISIBLE);
            uninstall.setVisibility(View.VISIBLE);
        }
    }
}
