package com.hoshi.graduationproject.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hoshi.graduationproject.R;
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

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener{

  private TextView tv_friends_title, tv_null_tips;
  private List mDatas = new ArrayList();
  private RecyclerView mRecyclerView;

  private Handler handler;

  private final int TRENDS_READY = 0;
  private final int FOLLOWS_READY = 1;
  private final int FANS_READY = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_trends);

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case TRENDS_READY:
            TrendsAdapter mTrendsAdapter = new TrendsAdapter(mDatas, getBaseContext());
            mRecyclerView.setAdapter(mTrendsAdapter);
            break;
          case FOLLOWS_READY:
            //TrendsAdapter mTrendsAdapter = new TrendsAdapter(mDatas, getBaseContext());
            //mRecyclerView.setAdapter(mTrendsAdapter);
            break;
          case FANS_READY:
            //TrendsAdapter mTrendsAdapter = new TrendsAdapter(mDatas, getBaseContext());
            //mRecyclerView.setAdapter(mTrendsAdapter);
            break;
          default:
            break;
        }
      }
    };

    initPage(getIntent().getIntExtra("type", 0));
    ClickManager.init(this, this, R.id.friends_back);
  }

  private void initPage(int type) {
    int friends[] = Preferences.getFriends();
    tv_friends_title = findViewById(R.id.friends_title);
    tv_null_tips = findViewById(R.id.null_tips);
    mRecyclerView = findViewById(R.id.friends_recyclerView);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);//设置布局管理器
    mRecyclerView.setLayoutManager(layoutManager);//设置为垂直布局，这也是默认的
    layoutManager.setOrientation(OrientationHelper. VERTICAL);//设置Adapter

    switch (type) {
      case 1:
        tv_friends_title.setText(R.string.trends);
        if (friends[0] != 0) {
          mRecyclerView.setVisibility(View.VISIBLE);
          tv_null_tips.setVisibility(View.GONE);
          sendGetTrendsRequest();
        } else {
          mRecyclerView.setVisibility(View.GONE);
          tv_null_tips.setText(R.string.no_trends);
          tv_null_tips.setVisibility(View.VISIBLE);
        }
        break;
      case 2:
        tv_friends_title.setText(R.string.follows);
        tv_null_tips.setText(R.string.no_follows);
        break;
      case 3:
        tv_friends_title.setText(R.string.fans);
        tv_null_tips.setText(R.string.no_fans);
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
        message.what = TRENDS_READY;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.friends_back:
        finish();
        break;
      default:
        break;
    }
  }
}
