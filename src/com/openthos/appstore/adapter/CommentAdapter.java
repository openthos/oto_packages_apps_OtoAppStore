package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.CommentInfo;
import com.openthos.appstore.view.CustomRatingBar;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends BasicAdapter {
    private boolean mIsAll;

    public CommentAdapter(Context context, boolean isAll, List<CommentInfo> datas) {
        super(context);
        mIsAll = isAll;
        mDatas = datas;
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? -1 :
                ((CommentInfo) mDatas.get(position)).getId();
    }

    @Override
    public void refreshLayout() {
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.item_comment, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            CommentInfo commentInfo = (CommentInfo) mDatas.get(position);
            holder.content.setText(R.string.comment_content);
            holder.time.setText(commentInfo.getTime());
            holder.author.setText(mContext.getString(R.string.author) +
                    mContext.getString(R.string.who));
            holder.star.setRating(commentInfo.getStar());
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView content;
        private TextView author;
        private TextView time;
        private CustomRatingBar star;

        public ViewHolder(View view) {
            content = ((TextView) view.findViewById(R.id.item_comment_content));
            author = (TextView) view.findViewById(R.id.item_comment_author);
            time = (TextView) view.findViewById(R.id.item_comment_time);
            star = ((CustomRatingBar) view.findViewById(R.id.item_comment_star));
        }
    }
}
