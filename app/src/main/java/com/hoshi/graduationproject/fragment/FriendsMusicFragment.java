package com.hoshi.graduationproject.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.SongListDetailActivity;
import com.hoshi.graduationproject.adapter.SongListAdapter;
import com.hoshi.graduationproject.model.SongList;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class FriendsMusicFragment extends Fragment {

  private RecyclerView rv_friend_detail_song_list;
  private List<SongList> mData = new ArrayList<>();
  private TextView tv_no_list;
  private Handler handler;

  private int friend_id;
  private final int READY = 1;
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case READY:
            if (mData.size() == 0) {
              rv_friend_detail_song_list.setVisibility(View.GONE);
              tv_no_list.setVisibility(View.VISIBLE);
            } else {
              rv_friend_detail_song_list.setVisibility(View.VISIBLE);
              tv_no_list.setVisibility(View.GONE);
              SongListAdapter mSongListAdapter = new SongListAdapter(mData, getContext());
              mSongListAdapter.setOnItemClickLitener(new SongListAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                  SongList tempSongList = mData.get(position);
                  startActivity(new Intent(getContext(), SongListDetailActivity.class)
                          .putExtra("list_avatar", tempSongList.getList_avatar())
                          .putExtra("list_name", tempSongList.getList_name())
                          .putExtra("list_author_id", tempSongList.getList_author_id())
                          .putExtra("list_author_name", tempSongList.getList_author_name())
                          .putExtra("list_id", tempSongList.getList_id())
                          .putExtra("list_length", tempSongList.getList_length())
                          .putExtra("list_type", 1));
                }
              });
              rv_friend_detail_song_list.setAdapter(mSongListAdapter);
            }
          default:
            break;
        }
      }
    };
    if (getArguments() != null) {
      friend_id = getArguments().getInt("friend_id");
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_friends_music, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    tv_no_list = getActivity().findViewById(R.id.textview_friend_no_list);
    rv_friend_detail_song_list = getActivity().findViewById(R.id.friend_detail_song_list_recyclerView);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());//设置布局管理器
    rv_friend_detail_song_list.setLayoutManager(layoutManager);//设置为垂直布局，这也是默认的
    layoutManager.setOrientation(OrientationHelper. VERTICAL);//设置Adapter
  }

  @Override
  public void onResume() {
    super.onResume();
    sendGetFriendListRequest();
  }

  private void sendGetFriendListRequest() {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + friend_id);
    OkhttpUtil.postFormRequest(ServerPath.GET_P_SONG_LIST, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        mData.clear();
        JSONArray dataArray = new JSONArray(result);
        for (int i = 0; i < dataArray.length(); i++) {
          JSONObject dataObject = dataArray.getJSONObject(i);
          SongList tempSongList = new SongList();
          tempSongList.setList_name(dataObject.getString("list_name"));
          tempSongList.setList_id(dataObject.getInt("id"));
          tempSongList.setList_avatar(dataObject.getString("list_avatar"));
          tempSongList.setList_author_id(dataObject.getInt("list_author_id"));
          tempSongList.setList_length(dataObject.getInt("list_length"));
          tempSongList.setList_author_name(dataObject.getString("list_author_name"));
          mData.add(tempSongList);
        }

        Message message = new Message();
        message.what = READY;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }
}
