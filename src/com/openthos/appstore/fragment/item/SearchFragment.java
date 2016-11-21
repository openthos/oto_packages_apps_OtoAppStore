package com.openthos.appstore.fragment.item;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.openthos.appstore.R;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.view.CustomListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends BaseFragment {
    private String mContent;
    private EditText mText;
    private CustomListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        initData();
    }

    private void initData() {
        if (mContent != null) {
            mText.setText(mContent);
        }
    }

    private void initView(View view) {
        mText = ((EditText) view.findViewById(R.id.fragment_search_text));
        mListView = ((CustomListView) view.findViewById(R.id.fragment_search_listview));
        ImageView search = (ImageView) view.findViewById(R.id.activity_title_search);
//        search.setVisibility(View.GONE);
    }

    public void setDatas(String content) {
        if (content != null) {
            mContent = content;
        }
    }
}