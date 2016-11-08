package com.openthos.appstore.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.fragment.item.AppLayoutFragment;
import com.openthos.appstore.fragment.item.AppTypeFragment;
import com.openthos.appstore.view.Kanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ImageView mBack;
    private ImageView mForward;
    private Kanner mKanner;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_home, container, false);

        return ret;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initFragment();

        initData();
    }

    private void initFragment() {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        AppLayoutFragment appLayoutFragment = new AppLayoutFragment();
        appLayoutFragment.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        appLayoutFragment.setFromFragment(Constants.HOME_FRAGMENT);
        appLayoutFragment.setAll(false);
        appLayoutFragment.setDatas(Constants.getData());

        AppLayoutFragment appLayoutFragments = new AppLayoutFragment();
        appLayoutFragments.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        appLayoutFragments.setFromFragment(Constants.HOME_FRAGMENT);
        appLayoutFragments.setAll(false);
        appLayoutFragments.setDatas(Constants.getData());

        AppLayoutFragment appLayoutFragmentss = new AppLayoutFragment();
        appLayoutFragmentss.setNumColumns(Constants.GRIDVIEW_NUM_COLUMS);
        appLayoutFragmentss.setFromFragment(Constants.HOME_FRAGMENT);
        appLayoutFragmentss.setAll(false);
        appLayoutFragmentss.setDatas(Constants.getData());

        AppTypeFragment itemRightFragment = new AppTypeFragment();
        itemRightFragment.setFromFragment(Constants.HOME_FRAGMENT);
        itemRightFragment.setDatas(Constants.getDataItemRightInfo());

        transaction.replace(R.id.fragment_home_recommand, appLayoutFragment);
        transaction.replace(R.id.fragment_home_popular, appLayoutFragments);
        transaction.replace(R.id.fragment_home_favour, appLayoutFragmentss);
        transaction.replace(R.id.fragment_home_right, itemRightFragment);

        transaction.commit();
    }

    private void initView(View view) {
        mKanner = ((Kanner) view.findViewById(R.id.fragment_home_kanner));
        mForward = (ImageView) view.findViewById(R.id.fragment_home_forward);
        mBack = (ImageView) view.findViewById(R.id.fragment_home_back);
    }

    private void initData() {

        int images[] = new int[]{R.mipmap.back, R.mipmap.undown, R.mipmap.down};
//        mKanner.setImagesUrl(Constants.getString());
        String[] str = new String[Constants.getString().size()];
        for (int i = 0; i < Constants.getString().size(); i++) {
            str[i] = Constants.getString().get(i);
        }
        mKanner.setImagesUrl(Constants.getString());
        mKanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mKanner.getCurrentItem();
                if (currentItem > 1) {
                    mKanner.setCurrentItem(currentItem - 1);
                } else {
                    mKanner.setCurrentItem(1);
                }
            }
        });

        mForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mKanner.getCurrentItem();
                if (currentItem < mKanner.getImageViewsSize()) {
                    mKanner.setCurrentItem(currentItem + 1);
                } else {
                    mKanner.setCurrentItem(mKanner.getImageViewsSize());
                }
            }
        });
    }
}
