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
import com.hoshi.graduationproject.activity.FriendsTrendsActivity;
import com.hoshi.graduationproject.adapter.FollowsAdapter;
import com.hoshi.graduationproject.adapter.TrendsAdapter;
import com.hoshi.graduationproject.model.FriendsTrends;
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

public class FriendsTrendsFragment extends Fragment {

  private List<FriendsTrends> mDatas = new ArrayList<>();
  private RecyclerView mRecyclerView;
  private TextView tv_no_trends;
  private Handler handler;

  private int friend_id;
  private String friend_birthday, friend_profile;
  private final int READY = 1;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case READY:
            if (mDatas.size() == 0) {
              mRecyclerView.setVisibility(View.GONE);
              tv_no_trends.setVisibility(View.VISIBLE);
            } else {
              mRecyclerView.setVisibility(View.VISIBLE);
              tv_no_trends.setVisibility(View.GONE);
              TrendsAdapter mTrendsAdapter = new TrendsAdapter(mDatas, getContext());
              mTrendsAdapter.setOnItemClickLitener(new FollowsAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                  FriendsTrends tempFriendsTrends = mDatas.get(position);
                  startActivity(new Intent(getContext(), FriendsTrendsActivity.class)
                          .putExtra("trends_avatar", tempFriendsTrends.getTrends_avatar())
                          .putExtra("trends_name", tempFriendsTrends.getTrends_name())
                          .putExtra("trends_type", tempFriendsTrends.getTrends_type())
                          .putExtra("trends_date", tempFriendsTrends.getTrends_date())
                          .putExtra("trends_content", tempFriendsTrends.getTrends_content())
                          .putExtra("trends_comment", tempFriendsTrends.getTrends_comment())
                          .putExtra("trends_good", tempFriendsTrends.getTrends_good()));
                }
              });
              mRecyclerView.setAdapter(mTrendsAdapter);
            }
          default:
            break;
        }
      }
    };
    if (getArguments() != null) {
      friend_id = getArguments().getInt("friend_id");
      friend_birthday = getArguments().getString("friend_birthday");
      friend_profile = getArguments().getString("friend_profile");
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_friends_trends, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {

    tv_no_trends = getActivity().findViewById(R.id.textview_friend_no_trends);
    mRecyclerView = getActivity().findViewById(R.id.song_list_recyclerView);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());//设置布局管理器
    mRecyclerView.setLayoutManager(layoutManager);//设置为垂直布局，这也是默认的
    layoutManager.setOrientation(OrientationHelper. VERTICAL);//设置Adapter

    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onResume() {
    super.onResume();
    sendGetTrendsRequest();
  }

  private void sendGetTrendsRequest() {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + friend_id);
    OkhttpUtil.postFormRequest(ServerPath.GET_TRENDS_BY_ID, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        mDatas.clear();
        JSONArray dataSuccessJson = new JSONArray(result);
        for (int i = 0; i < dataSuccessJson.length(); i++) {
          JSONObject tempTrends = dataSuccessJson.getJSONObject(i);
          FriendsTrends tempFriendsTrends = new FriendsTrends();
          tempFriendsTrends.setTrends_avatar("http://cdn.aixifan.com/acfun-pc/2.0.97/img/niudan/niudango.png");
          tempFriendsTrends.setTrends_name(tempTrends.getString("author_name"));
          tempFriendsTrends.setTrends_type("分享动态");
          tempFriendsTrends.setTrends_date(tempTrends.getString("date"));
          tempFriendsTrends.setTrends_content(tempTrends.getString("content"));
          tempFriendsTrends.setTrends_comment(tempTrends.getInt("comment"));
          tempFriendsTrends.setTrends_good(tempTrends.getInt("good"));

          mDatas.add(tempFriendsTrends);
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
