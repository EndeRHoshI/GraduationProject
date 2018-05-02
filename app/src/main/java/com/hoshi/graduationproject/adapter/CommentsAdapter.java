package com.hoshi.graduationproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.model.Comments;
import com.hoshi.graduationproject.utils.binding.Bind;
import com.hoshi.graduationproject.utils.binding.ViewBinder;

import java.util.List;

/**
 * 评论列表适配器
 */
public class CommentsAdapter extends BaseAdapter {
  private List<Comments> mData;

  public CommentsAdapter(List<Comments> data) {
    this.mData = data;
  }

  @Override
  public int getCount() {
    return mData.size();
  }

  @Override
  public Object getItem(int position) {
    return mData.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments_list, parent, false);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    Comments tempComments = mData.get(position);
    holder.sdv_avatar.setImageURI(tempComments.getComments_avatar());
    holder.tv_nickname.setText(tempComments.getComments_nickname());
    holder.tv_date.setText(tempComments.getComments_date());
    holder.tv_content.setText(tempComments.getComments_content());
    return convertView;
  }

  private static class ViewHolder {
    @Bind(R.id.comments_nickname)
    private TextView tv_nickname;
    @Bind(R.id.comments_avatar)
    private SimpleDraweeView sdv_avatar;
    @Bind(R.id.comments_date)
    private TextView tv_date;
    @Bind(R.id.comments_content)
    private TextView tv_content;

    public ViewHolder(View view) {
      ViewBinder.bind(this, view);
    }
  }
}
