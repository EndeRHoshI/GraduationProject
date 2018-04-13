package com.hoshi.graduationproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.model.SongList;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongListHolder>{
  private List<SongList> mDatas;
  private Context mContext;
  private LayoutInflater inflater;

  public SongListAdapter(List<SongList> mDatas, Context mContext) {
    this.mDatas = mDatas;
    this.mContext = mContext;
    inflater = LayoutInflater. from(mContext);
  }

  @Override
  public SongListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_song_list, parent, false);
    SongListHolder mTrendsHolder= new SongListHolder(view);
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
  public void onBindViewHolder(SongListHolder holder, int position) {
    SongList mSongList = mDatas.get(position);
    if (!mSongList.getList_avatar().equals("")) {
      holder.list_avatar.setImageURI(mSongList.getList_avatar());
    }
    holder.list_name  .setText(mSongList.getList_name());
    holder.list_length.setText("" + mSongList.getList_length());

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

  public class SongListHolder extends RecyclerView.ViewHolder {
    public SimpleDraweeView list_avatar;
    public TextView list_name, list_length;

    //实现的方法
    public SongListHolder(View itemView) {
      super(itemView);
      list_avatar = itemView.findViewById(R.id.song_list_avatar);
      list_name   = itemView.findViewById(R.id.song_list_name);
      list_length = itemView.findViewById(R.id.song_list_length);
    }
  }
}
