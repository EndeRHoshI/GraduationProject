package com.hoshi.graduationproject.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.PlayActivity;
import com.hoshi.graduationproject.activity.ShareActivity;
import com.hoshi.graduationproject.adapter.TrendsAdapter;
import com.hoshi.graduationproject.model.FriendsTrends;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;
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

public class FriendsFragment extends BaseFragment implements View.OnClickListener {

  private View mRootView;
  private TextView tv_no_trends;
  private RecyclerView mRecyclerView;

  private List<FriendsTrends> mDatas = new ArrayList<>();

  private Handler handler;
  private final int READY = 1;
  private final int LOGOUT_STAT = 2;
  private final int LOGIN_STAT = 3;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case READY:
            TrendsAdapter mTrendsAdapter = new TrendsAdapter(mDatas, getContext());
            mRecyclerView.setAdapter(mTrendsAdapter);
          default:
            break;
        }
      }
    };

    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_friends, container, false);
    return mRootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {

    tv_no_trends = getActivity().findViewById(R.id.no_trends);
    mRecyclerView = getActivity().findViewById(R.id.trends_recyclerView);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());//设置布局管理器
    mRecyclerView.setLayoutManager(layoutManager);//设置为垂直布局，这也是默认的
    layoutManager.setOrientation(OrientationHelper. VERTICAL);//设置Adapter

    ClickManager.init(getActivity(), this, R.id.loading_button,
            R.id.add_trends);
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onResume() {
    super.onResume();
    checkLogin();
    sendGetTrendsRequest();
  }

  public void checkLogin() {
    String id = "" + Preferences.getId();
    if (id.equals("")) {
      changeViewStat(LOGOUT_STAT);
      return;
    }
    HashMap<String, String> params = new HashMap<>();
    params.put("id", id);
    OkhttpUtil.postFormRequest(ServerPath.CHECK_LOGIN, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        String sessionId = dataSuccessJson.getString("sessionId");
        if (!sessionId.equals(Preferences.getSessionId())) {
          changeViewStat(LOGOUT_STAT);
        } else {
          changeViewStat(LOGIN_STAT);
        }
      }

      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void changeViewStat(int stat) {
    switch (stat) {
      case LOGIN_STAT:
        mRecyclerView.setVisibility(View.VISIBLE);
        tv_no_trends.setVisibility(View.GONE);
        break;
      case LOGOUT_STAT:
        mRecyclerView.setVisibility(View.GONE);
        tv_no_trends.setVisibility(View.VISIBLE);
        break;
      default:
        break;
    }
  }

  private void sendGetTrendsRequest() {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
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

          //ToastUtils.show(""+ dataSuccessJson.length());

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

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.loading_button:
        startActivity(new Intent(getActivity(), PlayActivity.class));
        break;
      case R.id.add_trends:
        if (!Preferences.getNickname().equals(""))
          startActivity(new Intent(getActivity(), ShareActivity.class));
        else ToastUtils.show(R.string.please_login);
        break;
      default:
        break;
    }
  }
}
