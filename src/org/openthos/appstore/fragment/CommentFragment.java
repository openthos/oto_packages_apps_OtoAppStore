package org.openthos.appstore.fragment;

import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.openthos.appstore.MainActivity;
import org.openthos.appstore.R;
import org.openthos.appstore.adapter.CommentAdapter;
import org.openthos.appstore.app.Constants;
import org.openthos.appstore.bean.AppInstallInfo;
import org.openthos.appstore.bean.CommentInfo;
import org.openthos.appstore.utils.Tools;
import org.openthos.appstore.view.CustomRatingBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentFragment extends BaseFragment implements View.OnClickListener {

    private ListView mListView;
    private CommentAdapter mAdapter;
    private boolean mIsAll;
    private TextView mWhole;
    private Button mSubmit;
    private Button mCancel;
    private CustomRatingBar mRatingBar;
    private EditText mContent;
    private LinearLayout mCommentLayout;
    private TextView mTouchComment;
    private List<CommentInfo> mDatas = new ArrayList<>();

    public CommentFragment() {
        super();
    }

    public void setAll(boolean isAll) {
        mIsAll = isAll;
    }

    public void setDatas(List<CommentInfo> datas) {
        mDatas = datas;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_comment;
    }

    @Override
    public void setData(Object data) {
    }

    @Override
    public void refresh() {
    }

    public void initView(View view) {
        mWhole = ((TextView) view.findViewById(R.id.fragment_comment_whole));
        mListView = ((ListView) view.findViewById(R.id.fragment_comment_listview));
        mSubmit = ((Button) view.findViewById(R.id.fragment_comment_submit));
        mCancel = ((Button) view.findViewById(R.id.fragment_comment_cancel));
        mCommentLayout = ((LinearLayout) view.findViewById(
                R.id.fragment_comment_commentlayout));
        mRatingBar = ((CustomRatingBar) view.findViewById(R.id.fragment_comment_ratingbar));
        mContent = ((EditText) view.findViewById(R.id.fragment_comment_content));
        mTouchComment = ((TextView) view.findViewById(R.id.fragment_comment_touchcomment));
    }

    @Override
    public void initData() {
        mAdapter = new CommentAdapter(getActivity(), mIsAll, mDatas);
        mListView.setAdapter(mAdapter);
        mAdapter.refreshLayout();

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
    public void getHandlerMessage(Message message) {
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