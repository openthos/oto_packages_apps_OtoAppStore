package com.openthos.appstore.utils;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.activity.BaseActivity;
import com.openthos.appstore.activity.SearchActivity;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;

/**
 * Created by luojunhuan on 16-10-28.
 */
public class ActivityTitileUtils {

    public static void initActivityTitle(final BaseActivity activity) {
        final Intent[] intent = {null};

        RadioGroup radioGroup = (RadioGroup) activity.findViewById(R.id.main_radioGroup);
        final int[] fromFragment = {0};
        ImageView back = (ImageView) activity.findViewById(R.id.activity_title_back);
        ImageView forward = (ImageView) activity.findViewById(R.id.activity_title_forward);
        ImageView search = (ImageView) activity.findViewById(R.id.activity_title_search);
        final EditText content = (EditText) activity.findViewById(R.id.activity_title_content);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreApplication.activities.add(activity);
                activity.finish();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = StoreApplication.activities.size() - 1;
                if (size > 0) {
                    intent[0] = new Intent(activity,
                            StoreApplication.activities.get(size - 1).getClass());
                    activity.startActivity(intent[0]);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getVisibility() == View.GONE) {
                    content.setVisibility(View.VISIBLE);
                } else {
                    String contents = content.getText().toString();
                    if (!TextUtils.isEmpty(contents)) {
                        intent[0] = new Intent(activity, SearchActivity.class);
                        intent[0].putExtra("content", contents);
                        content.setVisibility(View.GONE);
                        activity.startActivity(intent[0]);
                    } else {
                        content.setVisibility(View.GONE);
                        Tools.toast(activity, activity.getString(R.string.toast_search));
                    }
                }
            }
        });

        Tools.printLog("activity1", activity.getLocalClassName());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                intent[0] = new Intent(activity, MainActivity.class);
                switch (checkedId) {
                    case R.id.rb_home:
                        fromFragment[0] = Constants.HOME_FRAGMENT;
                        intent[0].putExtra(Constants.FROM_FRAGMENT, fromFragment[0]);
                        Toast.makeText(activity, fromFragment[0] + "",
                                Toast.LENGTH_SHORT).show();
                        activity.startActivity(intent[0]);
                        break;
                    case R.id.rb_software:
                        fromFragment[0] = Constants.SOFTWARE_FRAGMENT;
                        intent[0].putExtra(Constants.FROM_FRAGMENT, fromFragment[0]);
                        Toast.makeText(activity, fromFragment[0] + "", Toast.LENGTH_SHORT).show();
                        activity.startActivity(intent[0]);
                        break;
                    case R.id.rb_game:
                        fromFragment[0] = Constants.GAME_FRAGMENT;
                        intent[0].putExtra(Constants.FROM_FRAGMENT, fromFragment[0]);
                        Toast.makeText(activity, fromFragment[0] + "",
                                Toast.LENGTH_SHORT).show();
                        activity.startActivity(intent[0]);
                        break;
                    case R.id.rb_manager:
                        fromFragment[0] = Constants.MANAGER_FRAGMENT;
                        intent[0].putExtra(Constants.FROM_FRAGMENT, fromFragment[0]);
                        Toast.makeText(activity, fromFragment[0] + "",
                                Toast.LENGTH_SHORT).show();
                        activity.startActivity(intent[0]);
                        break;
                }
            }
        });

        if (radioGroup != null) {
            int[] drawables = new int[] {
                    R.drawable.select_home_drawable,
                    R.drawable.select_software_drawable,
                    R.drawable.select_game_drawable,
                    R.drawable.select_manager_drawable,
            };

            int[] rids = new int[] {
                    R.id.rb_home,
                    R.id.rb_software,
                    R.id.rb_game,
                    R.id.rb_manager,
            };
            Resources res = activity.getResources();
            for (int i = 0; i < rids.length; i++) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(rids[i]);
                Drawable drawable = res.getDrawable(drawables[i]);
                drawable.setBounds(0, 0, 20, 20);
                rb.setCompoundDrawablePadding(10);
                rb.setCompoundDrawables(drawable, null, null, null);
            }
        }
    }

    public static int checked(BaseActivity activity, Intent intent) {
        int fromFragment = intent.getIntExtra(Constants.FROM_FRAGMENT,
                Constants.HOME_FRAGMENT);
        RadioButton home = (RadioButton) activity.findViewById(R.id.rb_home);
        RadioButton software = (RadioButton) activity.findViewById(R.id.rb_software);
        RadioButton game = (RadioButton) activity.findViewById(R.id.rb_game);
        RadioButton manager = (RadioButton) activity.findViewById(R.id.rb_manager);
        RadioButton[] button = new RadioButton[] {home, software, game, manager};
        for (int i = 0; i < button.length; i++) {
            if (i == fromFragment) {
                button[i].setChecked(true);
            } else {
                button[i].setChecked(false);
            }
        }
        return fromFragment;
    }
}
