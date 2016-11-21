package com.openthos.appstore.fragment.item;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.adapter.CommentAdapter;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.CommentInfo;
import com.openthos.appstore.fragment.BaseFragment;
import com.openthos.appstore.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends BaseFragment implements View.OnClickListener {

    private ListView mListView;
    private CommentAdapter mAdapter;
    private boolean mIsAll;
    private TextView mWhole;
    private Button mSubmit;
    private Button mCancel;
    private RatingBar mRatingBar;
    private EditText mContent;
    private LinearLayout mCommentLayout;
    private TextView mTouchComment;
    private List<CommentInfo> mDatas = new ArrayList<>();

    public void setAll(boolean isAll) {
        mIsAll = isAll;
    }

    public void setDatas(List<CommentInfo> datas) {
        mDatas = datas;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);

        loadData();
    }

    private void initView(View view) {
        mWhole = ((TextView) view.findViewById(R.id.fragment_comment_whole));
        mListView = ((ListView) view.findViewById(R.id.fragment_comment_listview));
        mSubmit = ((Button) view.findViewById(R.id.fragment_comment_submit));
        mCancel = ((Button) view.findViewById(R.id.fragment_comment_cancel));
        mCommentLayout = ((LinearLayout) view.findViewById(
                R.id.fragment_comment_commentlayout));
        mRatingBar = ((RatingBar) view.findViewById(R.id.fragment_comment_ratingbar));
        mContent = ((EditText) view.findViewById(R.id.fragment_comment_content));
        mTouchComment = ((TextView) view.findViewById(R.id.fragment_comment_touchcomment));
    }

    private void loadData() {
        mAdapter = new CommentAdapter(getActivity(), mIsAll);
        mListView.setAdapter(mAdapter);
        mAdapter.addDatas(mDatas);

        mSubmit.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mTouchComment.setOnClickListener(this);

        if (mIsAll) {
            mWhole.setVisibility(View.GONE);
        } else {
            mWhole.setVisibility(View.VISIBLE);
            mWhole.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_comment_whole:
                startComment();
                break;
            case R.id.fragment_comment_submit:
                submitOperate();
                break;
            case R.id.fragment_comment_cancel:
                String writeComment = getResources().getString(R.string.write_comment);
                mSubmit.setText(writeComment);
                mCancel.setVisibility(View.GONE);
                mCommentLayout.setVisibility(View.GONE);
                break;
            case R.id.fragment_comment_touchcomment:
                float rating = mRatingBar.getRating();
                Tools.toast(getActivity(), rating + "");
                break;
            default:
                break;
        }
    }

    private void startComment() {
        Message message = MainActivity.mHandler.obtainMessage();
        message.what = Constants.COMMENT_FRAGMENT;
        MainActivity.mHandler.sendMessage(message);
    }

    private void submitOperate() {
        String s = mSubmit.getText().toString();
        String writeComment = getResources().getString(R.string.write_comment);
        String submitComment = getResources().getString(R.string.submit_comment);
        Tools.printLog("commentfragment", writeComment.equals(s) + "");
        if (writeComment.equals(s)) {
            mSubmit.setText(submitComment);
            mCancel.setVisibility(View.VISIBLE);
            mCommentLayout.setVisibility(View.VISIBLE);

        } else {
            float rating = mRatingBar.getRating();
            String contents = mContent.getText().toString();
            mSubmit.setText(writeComment);
            mCancel.setVisibility(View.GONE);
            mCommentLayout.setVisibility(View.GONE);
            startComment();
        }
    }
}