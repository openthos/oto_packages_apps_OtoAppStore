package com.openthos.appstore.utils;

import android.support.v4.app.Fragment;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.GameFragment;
import com.openthos.appstore.fragment.HomeFragment;
import com.openthos.appstore.fragment.ManagerFragment;
import com.openthos.appstore.fragment.SoftwareFragment;
import com.openthos.appstore.fragment.item.AppLayoutFragment;
import com.openthos.appstore.fragment.item.AppTypeFragment;

/**
 * Created by luojunhuan on 16-10-27.
 */
public class GetFragment {
    public static Fragment getFragment(int fragment) {
        switch (fragment) {
            case Constants.HOME_FRAGMENT:
                return new HomeFragment();
            case Constants.SOFTWARE_FRAGMENT:
                return new SoftwareFragment();
            case Constants.GAME_FRAGMENT:
                return new GameFragment();
            case Constants.MANAGER_FRAGMENT:
                return new ManagerFragment();
            default:
                return null;
        }
    }
}