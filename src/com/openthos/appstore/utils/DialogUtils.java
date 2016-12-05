package com.openthos.appstore.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.openthos.appstore.R;

/**
 * Created by forward on 2016/11/13.
 */
public class DialogUtils {
    public void dialogUpdate(Context context, final UpdateManager updateManager) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_function, null, false);
        TextView uninstall = (TextView) inflate.findViewById(R.id.dialog_function_text1);
        uninstall.setVisibility(View.VISIBLE);
        uninstall.setText(R.string.uninstall);
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateManager.uninstall(dialog);
            }
        });
        dialog.setView(inflate);
        dialog.show();
    }

    public void dialogDownload(Context context, final DownloadManager downloadManager) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_function, null, false);
        TextView installs = (TextView) inflate.findViewById(R.id.dialog_function_text1);
        TextView removeTask = (TextView) inflate.findViewById(R.id.dialog_function_text2);
        installs.setVisibility(View.VISIBLE);
        installs.setText(R.string.install);
        installs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadManager.install(dialog);
            }
        });

        removeTask.setVisibility(View.VISIBLE);
        removeTask.setText(R.string.remove_task);
        removeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.removeTask(dialog);
            }
        });
//        dialog.setView(inflate);
        dialog.show();
        dialog.setContentView(inflate);
    }

    public interface UpdateManager {
        void uninstall(AlertDialog dialog);
    }

    public interface DownloadManager {
        void install(AlertDialog dialog);
        void removeTask(AlertDialog dialog);
    }
}
