package com.openthos.appstore.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.item.AppLayoutFragment;
import com.openthos.appstore.fragment.item.AppTypeFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData(view);

        initFragment();
    }

    private void initFragment() {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        AppLayoutFragment appLayoutFragment = new AppLayoutFragment();
        appLayoutFragment.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        appLayoutFragment.setFromFragment(Constants.GAME_FRAGMENT);
        appLayoutFragment.setAll(false);
        appLayoutFragment.setDatas(Constants.getData());

        AppTypeFragment appTypeFragment = new AppTypeFragment();
        appTypeFragment.setFromFragment(Constants.GAME_FRAGMENT);
        appTypeFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.fragment_game_left, appLayoutFragment);
        transaction.replace(R.id.fragment_game_right, appTypeFragment);

        transaction.commit();
    }

    private void initData(View view) {
    }
}
