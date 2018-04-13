package com.hoshi.graduationproject.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.model.FriendsDetails;

import java.util.List;

public class FollowsAdapter extends RecyclerView.Adapter<FollowsAdapter.FriendsDetailsHolder>{
  private List<FriendsDetails> mDatas;
  private Context mContext;
  private LayoutInflater inflater;

  public FollowsAdapter (List<FriendsDetails> mDatas, Context mContext) {
    this.mDatas = mDatas;
    this.mContext = mContext;
    inflater = LayoutInflater. from(mContext);
  }

  @Override
  public FriendsDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.follows_item_layout, parent, false);
    FriendsDetailsHolder mTrendsHolder = new FriendsDetailsHolder(view);
    return mTrendsHolder;
  }

  public interface OnItemClickLitener {
    void onItemClick(View view, int position);
  }

  private OnItemClickLitener mOnItemClickLitener;

  public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
    this.mOnItemClickLitener = mOnItemClickLitener;
  }

  @Override
  public void onBindViewHolder(FriendsDetailsHolder holder, int position) {
    FriendsDetails mFriendsDetails = mDatas.get(position);
    holder.follows_avatar.setImageURI(mFriendsDetails.getFriend_avatar());
    holder.follows_name.setText(mFriendsDetails.getFriend_name());
    switch (mFriendsDetails.getFriend_sex()) {
      case 0://保密
        holder.follows_name.setCompoundDrawables(null,null, null, null);
        break;
      case 1://男
        Drawable man = mContext.getResources().getDrawable(R.drawable.ic_man);
        man.setBounds(0, 0, man.getMinimumWidth(), man.getMinimumHeight());
        holder.follows_name.setCompoundDrawables(null,null, man, null);
        break;
      case 2://女
        Drawable women = mContext.getResources().getDrawable(R.drawable.ic_woman);
        women.setBounds(0, 0, women.getMinimumWidth(), women.getMinimumHeight());
        holder.follows_name.setCompoundDrawables(null,null, women, null);
        break;
    }
    if (mFriendsDetails.getFriend_presonal_profile() == null ||
            mFriendsDetails.getFriend_presonal_profile().equals("")) {
      holder.follows_presonal_profile.setVisibility(View.GONE);
    } else {
      holder.follows_presonal_profile.setText(mFriendsDetails.getFriend_presonal_profile());
    }

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

  public class FriendsDetailsHolder extends RecyclerView.ViewHolder {
    public SimpleDraweeView follows_avatar;
    public TextView follows_name, follows_presonal_profile;

    //实现的方法
    public FriendsDetailsHolder(View itemView) {
      super(itemView);
      follows_avatar           = itemView.findViewById(R.id.follows_avatar);
      follows_name             = itemView.findViewById(R.id.follows_name);
      follows_presonal_profile = itemView.findViewById(R.id.follows_presonal_profile);
    }
  }
}
