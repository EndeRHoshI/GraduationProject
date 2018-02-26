package com.hoshi.graduationproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.model.OnlineMusic;
import com.hoshi.graduationproject.utils.FileUtils;
import com.hoshi.graduationproject.utils.binding.Bind;
import com.hoshi.graduationproject.utils.binding.ViewBinder;

import java.util.List;

/**
 * 在线音乐列表适配器
 * Created by wcy on 2015/12/22.
 */
public class OnlineMusicAdapter extends BaseAdapter {
  private List<OnlineMusic> mData;
  private OnMoreClickListener mListener;

  public OnlineMusicAdapter(List<OnlineMusic> data) {
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
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_songs, parent, false);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    OnlineMusic onlineMusic = mData.get(position);
    holder.tvTitle.setText(onlineMusic.getTitle());
    String artist = FileUtils.getArtistAndAlbum(onlineMusic.getArtist_name(), onlineMusic.getAlbum_title());
    holder.tvArtist.setText(artist + " - " + onlineMusic.getTitle());

    if (!onlineMusic.getAlias().equals("") && onlineMusic.getAlias() != null) {
      holder.tvAlias.setText("（" + onlineMusic.getAlias() + "）");
    } else {
      holder.tvAlias.setVisibility(View.INVISIBLE);
    }

    if (onlineMusic.getIndex() < 10) {
      holder.tvIndex.setText("0" + onlineMusic.getIndex());
    }
    else {
      holder.tvIndex.setText("" + onlineMusic.getIndex());
    }
    holder.ivMore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mListener.onMoreClick(position);
      }
    });
    return convertView;
  }

  public void setOnMoreClickListener(OnMoreClickListener listener) {
    mListener = listener;
  }

  private static class ViewHolder {
    @Bind(R.id.rank_songList_name)
    private TextView tvTitle;
    @Bind(R.id.rank_songList_name_singer)
    private TextView tvArtist;
    @Bind(R.id.iv_more)
    private ImageView ivMore;
    @Bind(R.id.rank_songList_alias)
    private TextView tvAlias;
    @Bind(R.id.rank_index)
    private TextView tvIndex;

    public ViewHolder(View view) {
      ViewBinder.bind(this, view);
    }
  }
}
