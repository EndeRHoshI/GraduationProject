package com.hoshi.graduationproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.model.FriendsTrends;

import java.util.List;

public class TrendsAdapter extends RecyclerView.Adapter<TrendsAdapter.TrendsHolder>{
  private List<FriendsTrends> mDatas;
  private Context mContext;
  private LayoutInflater inflater;

  public TrendsAdapter (List<FriendsTrends> mDatas, Context mContext) {
    this.mDatas = mDatas;
    this.mContext = mContext;
    inflater = LayoutInflater. from(mContext);
  }

  @Override
  public TrendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.trends_item_layout, parent, false);
    TrendsHolder mTrendsHolder= new TrendsHolder(view);
    return mTrendsHolder;
  }

  public interface OnItemClickLitener {
    void onItemClick(View view, int position);
  }

  private FollowsAdapter.OnItemClickLitener mOnItemClickLitener;

  public void setOnItemClickLitener(FollowsAdapter.OnItemClickLitener mOnItemClickLitener) {
    this.mOnItemClickLitener = mOnItemClickLitener;
  }

  @Override
  public void onBindViewHolder(TrendsHolder holder, int position) {
    FriendsTrends mFriendsTrends = mDatas.get(position);
    holder.trends_avatar .setImageURI(mFriendsTrends.getTrends_avatar());
    holder.trends_name   .setText(mFriendsTrends.getTrends_name());
    holder.trends_type   .setText(mFriendsTrends.getTrends_type());
    holder.trends_date   .setText(mFriendsTrends.getTrends_date());
    holder.trends_content.setText(mFriendsTrends.getTrends_content());
    holder.trends_comment.setText("" + mFriendsTrends.getTrends_comment());
    holder.trends_good   .setText("" + mFriendsTrends.getTrends_good());

    if (mOnItemClickLitener != null) {
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          int pos = holder.getLayoutPosition();
          mOnItemClickLitener.onItemClick(holder.itemView, pos);
        }
      });
    }
  }

  @Override
  public int getItemCount() {
    return mDatas.size();
  }

  public class TrendsHolder extends RecyclerView.ViewHolder {
    public SimpleDraweeView trends_avatar;
    public TextView trends_name, trends_type, trends_date, trends_content, trends_comment, trends_good;

    //实现的方法
    public TrendsHolder(View itemView) {
      super(itemView);
      trends_avatar  = itemView.findViewById(R.id.trends_avatar );
      trends_name    = itemView.findViewById(R.id.trends_name   );
      trends_type    = itemView.findViewById(R.id.trends_type   );
      trends_date    = itemView.findViewById(R.id.trends_date   );
      trends_content = itemView.findViewById(R.id.trends_content);
      trends_comment = itemView.findViewById(R.id.trends_comment);
      trends_good    = itemView.findViewById(R.id.trends_good   );
    }
  }
}
