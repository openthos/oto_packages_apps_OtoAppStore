package com.openthos.appstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openthos.appstore.R;
import com.openthos.appstore.activity.DetailActivity;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutGridviewInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.utils.download.DownLoadManager;
import com.openthos.appstore.utils.download.DownLoadService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import com.openthos.appstore.app.StoreApplication;

/**
 * Created by luojunhuan on 16-10-26.
 */
public class AppLayoutGridviewAdapter extends BasicAdapter implements View.OnClickListener {
    private DownLoadManager mManager;

    public AppLayoutGridviewAdapter(Context context, int fromFragment, boolean isAll) {
        super(context, isAll);
        mFromFragment = fromFragment;
        mDatas = new ArrayList<>();
        mManager = StoreApplication.getDownLoadManager();
        if (mManager != null) {
            mManager.changeUser(Constants.USER_ID);
            mManager.setSupportBreakpoint(false);
        }
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? -1 : ((AppLayoutGridviewInfo) mDatas.get(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.app_layout_gridview, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            AppLayoutGridviewInfo appLayoutGridviewInfo =
                                      (AppLayoutGridviewInfo) mDatas.get(position);
            Picasso.with(mContext).load(appLayoutGridviewInfo.getIconUrl()).into(holder.icon);
            Picasso.with(mContext).load(Constants.BASEURL + "/" +
                                            appLayoutGridviewInfo.getIconUrl()).into(holder.icon);
            holder.name.setText(appLayoutGridviewInfo.getName());
            holder.type.setText(appLayoutGridviewInfo.getType());
            switch (appLayoutGridviewInfo.getState()) {
                case Constants.INSTALL_BUTTON_NOT_INSTALL:
                    setContent(holder.install, R.string.not_install,
                                          R.drawable.shape_button_white_cyan, R.color.button_cyan);
                    break;
                case Constants.INSTALL_BUTTON_HAVE_INSTALLED:
                    setContent(holder.install, R.string.have_installed,
                                          R.drawable.shape_button_gray, R.color.button_gray);
                    break;
                case Constants.INSTALL_BUTTON_CONTINUE:
                    setContent(holder.install, R.string.continues,
                                          R.drawable.shape_button_white_cyan, R.color.button_cyan);
                    break;
                case Constants.INSTALL_BUTTON_PAUSE:
                    setContent(holder.install, R.string.pause, R.drawable.shape_button_white_cyan,
                                          R.color.button_cyan);
                    break;
                default:
                    break;
            }

            holder.install.setOnClickListener(this);
            holder.icon.setOnClickListener(this);
            holder.name.setOnClickListener(this);
            holder.type.setOnClickListener(this);
            holder.install.setTag(position);
            holder.icon.setTag(position);
            holder.name.setTag(position);
            holder.type.setTag(position);
        }

        return convertView;
    }

    private void setContent(Button btn, int text, int background, int color) {
        btn.setText(text);
        btn.setBackgroundResource(background);
        btn.setTextColor(mContext.getResources().getColor(color));
    }

    @Override
    public void onClick(View v) {
        int possition = (int) v.getTag();
        AppLayoutGridviewInfo appInfo = (AppLayoutGridviewInfo) mDatas.get(possition);

        switch (v.getId()) {
            case R.id.app_layout_gridview_install:
                Button install = (Button) v;
                String btnStr = install.getText().toString();

                String continues = mContext.getResources().getString(R.string.continues);
                String pause = mContext.getResources().getString(R.string.pause);
                String installs = mContext.getResources().getString(R.string.install);
                if (btnStr.equals(continues)) {
                    install.setText(pause);
                    mManager.stopTask(appInfo.getId() + "");
                } else if (btnStr.equals(pause)) {
                    install.setText(continues);
                    mManager.startTask(appInfo.getId() + "");
                } else if (btnStr.equals(installs)) {
                    install.setText(pause);
                    mManager.addTask(appInfo.getId() + "", Constants.BASEURL + "/" +appInfo.getDownload(),
                            appInfo.getName(), null);
                } else {

                }

                break;

            default:
                Toast.makeText(mContext, possition + "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(Constants.FROM_FRAGMENT, mFromFragment);
                Bundle bundle = new Bundle();
                //TODO
                mContext.startActivity(intent);
                break;
        }
    }

    class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView type;
        private Button install;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.app_layout_gridview_icon);
            name = (TextView) view.findViewById(R.id.app_layout_gridview_name);
            type = (TextView) view.findViewById(R.id.app_layout_gridview_type);
            install = (Button) view.findViewById(R.id.app_layout_gridview_install);
        }
    }

    public void addDatas(List<AppLayoutGridviewInfo> datas) {
        this.mDatas.clear();
        if (mIsAll) {
            mDatas.addAll(datas);
        } else {
            int len = datas == null ? 0 : (datas.size() > Constants.APP_NUM_FALSE
                                               ? Constants.APP_NUM_FALSE : datas.size());
            for (int i = 0; i < len; i++) {
                mDatas.add(datas.get(i));
            }
        }
        notifyDataSetChanged();
    }
}
