package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.utils.AppUtils;

import java.util.List;

public class ManagerUpdateAdapter extends BasicAdapter implements View.OnClickListener {

    public ManagerUpdateAdapter(Context context, List<AppInstallInfo> datas) {
        super(context);
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
            holder.appVersion.setText(appInstallInfo.getVersionName());
            holder.appContent.setText(appInstallInfo.getPackageName());
            holder.uninstall.setOnClickListener(this);
            holder.update.setOnClickListener(this);
            holder.update.setText(mContext.getResources().getString(R.string.open));
            holder.uninstall.setTag(appInstallInfo.getPackageName());
            holder.update.setTag(appInstallInfo.getPackageName());
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        String pkgName = (String) view.getTag();
        switch (view.getId()) {
            case R.id.item_update_uninstall:
                AppUtils.uninstallApk(mContext, pkgName);
                break;
            default:
                AppUtils.openApp(mContext, pkgName);
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
        private Button update;
        private Button uninstall;

        public ViewHolder(View view) {
            appIcon = ((ImageView) view.findViewById(R.id.item_update_appIcon));
            appName = (TextView) view.findViewById(R.id.item_update_appName);
            category = (TextView) view.findViewById(R.id.item_update_category);
            appVersion = (TextView) view.findViewById(R.id.item_update_version);
            appContent = (TextView) view.findViewById(R.id.item_update_newFeature);
            update = ((Button) view.findViewById(R.id.item_update_update));
            uninstall = ((Button) view.findViewById(R.id.item_update_uninstall));
        }
    }
}
