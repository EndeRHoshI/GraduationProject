package com.hoshi.graduationproject.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.adapter.FollowsAdapter;
import com.hoshi.graduationproject.adapter.TrendsAdapter;
import com.hoshi.graduationproject.model.FriendsDetails;
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
  private ImageView iv_friends_search, iv_add_trends;
  private List mDatas = new ArrayList();
  private RecyclerView mRecyclerView;

  private Handler handler;

  private final int GET_TRENDS_READY = 0;
  private final int GET_FOLLOWS_READY = 1;
  private final int GET_FANS_READY = 2;
  private final int FRIENDS_SEARCH_READY = 3;
  private final int FRIENDS_FOLLOW_READY = 4;

  private final int TRENDS = 0;
  private final int FOLLOWS = 1;
  private final int FANS = 2;

  private String friend_nickname = "", friend_avatar = "";
  private int friend_sex = 0, friend_id = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friends);

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case GET_TRENDS_READY:
            changeUiType(GET_TRENDS_READY);
            TrendsAdapter mTrendsAdapter = new TrendsAdapter(mDatas, getBaseContext());
            mTrendsAdapter.setOnItemClickLitener(new FollowsAdapter.OnItemClickLitener() {
              @Override
              public void onItemClick(View view, int position) {
                FriendsTrends tempFriendsTrends = (FriendsTrends)mDatas.get(position);
                startActivity(new Intent(getBaseContext(), FriendsTrendsActivity.class)
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
            break;
          case GET_FOLLOWS_READY:
            changeUiType(GET_FOLLOWS_READY);
            FollowsAdapter mFollowsAdapter = new FollowsAdapter(mDatas, getBaseContext());
            mFollowsAdapter.setOnItemClickLitener(new FollowsAdapter.OnItemClickLitener() {
              @Override
              public void onItemClick(View view, int position) {
                FriendsDetails tempFriendsDetails = (FriendsDetails)mDatas.get(position);
                startActivity(new Intent(getBaseContext(), FriendsDetailActivity.class)
                        .putExtra("id", tempFriendsDetails.getFriend_id())
                        .putExtra("avatar", tempFriendsDetails.getFriend_avatar())
                        .putExtra("nickname", tempFriendsDetails.getFriend_name())
                        .putExtra("profile", tempFriendsDetails.getFriend_presonal_profile())
                        .putExtra("birthday", tempFriendsDetails.getFriend_birthday())
                        .putExtra("follow_type", 0)
                        .putExtra("sex", tempFriendsDetails.getFriend_sex()));
              }
            });
            mRecyclerView.setAdapter(mFollowsAdapter);
            break;
          case GET_FANS_READY:
            changeUiType(GET_FANS_READY);
            FollowsAdapter mFansAdapter = new FollowsAdapter(mDatas, getBaseContext());
            mFansAdapter.setOnItemClickLitener(new FollowsAdapter.OnItemClickLitener() {
              @Override
              public void onItemClick(View view, int position) {
                FriendsDetails tempFriendsDetails = (FriendsDetails)mDatas.get(position);
                startActivity(new Intent(getBaseContext(), FriendsDetailActivity.class)
                        .putExtra("id", tempFriendsDetails.getFriend_id())
                        .putExtra("avatar", tempFriendsDetails.getFriend_avatar())
                        .putExtra("nickname", tempFriendsDetails.getFriend_name())
                        .putExtra("profile", tempFriendsDetails.getFriend_presonal_profile())
                        .putExtra("birthday", tempFriendsDetails.getFriend_birthday())
                        .putExtra("follow_type", 1)
                        .putExtra("sex", tempFriendsDetails.getFriend_sex()));
              }
            });
            mRecyclerView.setAdapter(mFansAdapter);
            break;
          case FRIENDS_SEARCH_READY:
            showFriendsDialog(friend_nickname, friend_avatar, friend_sex);
            break;
          case FRIENDS_FOLLOW_READY:
            sendRequest(GET_FOLLOWS_READY);
            break;
          default:
            break;
        }
      }
    };

    ClickManager.init(this, this,
            R.id.friends_back,
            R.id.friends_search_button,
            R.id.friends_add_trends);
  }

  @Override
  protected void onResume() {
    super.onResume();
    sendRequest(getIntent().getIntExtra("type", -1));
    initPage(getIntent().getIntExtra("type", -1));
  }

  private void initPage(int type) {
    tv_friends_title = findViewById(R.id.friends_title);
    tv_null_tips = findViewById(R.id.null_tips);
    iv_friends_search = findViewById(R.id.friends_search_button);
    iv_add_trends = findViewById(R.id.friends_add_trends);
    mRecyclerView = findViewById(R.id.friends_recyclerView);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);//设置布局管理器
    mRecyclerView.setLayoutManager(layoutManager);//设置为垂直布局，这也是默认的
    layoutManager.setOrientation(OrientationHelper. VERTICAL);//设置Adapter

    changeUiType(type);
  }

  private void sendRequest(int type) {
    switch (type) {
      case TRENDS:
        sendGetTrendsRequest();
        break;
      case FOLLOWS:
        sendGetFollowsRequest();
        break;
      case FANS:
        sendGetFansRequest();
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
          mDatas.add(tempFriendsTrends);
        }
        Message message = new Message();
        message.what = GET_TRENDS_READY;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void sendGetFollowsRequest() {
    HashMap<String, String> params = new HashMap<>();
    params.put("follower_id", "" + Preferences.getId());
    OkhttpUtil.postFormRequest(ServerPath.GET_FOLLOWS_BY_ID, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        mDatas.clear();
        JSONArray dataSuccessJson = new JSONArray(result);
        for (int i = 0; i < dataSuccessJson.length(); i++) {
          JSONObject tempTrends = dataSuccessJson.getJSONObject(i);
          FriendsDetails tempFriendsDetails = new FriendsDetails();
          tempFriendsDetails.setFriend_id(tempTrends.getInt("id"));
          tempFriendsDetails.setFriend_avatar(tempTrends.getString("avatar"));
          tempFriendsDetails.setFriend_name(tempTrends.getString("nickname"));
          tempFriendsDetails.setFriend_birthday(tempTrends.getString("birthday"));
          tempFriendsDetails.setFriend_presonal_profile(tempTrends.getString("profile"));
          tempFriendsDetails.setFriend_sex(tempTrends.getInt("sex"));
          mDatas.add(tempFriendsDetails);
        }

        Message message = new Message();
        message.what = GET_FOLLOWS_READY;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void sendGetFansRequest() {
    HashMap<String, String> params = new HashMap<>();
    params.put("follows_id", "" + Preferences.getId());
    OkhttpUtil.postFormRequest(ServerPath.GET_FANS_BY_ID, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        mDatas.clear();
        JSONArray dataSuccessJson = new JSONArray(result);
        for (int i = 0; i < dataSuccessJson.length(); i++) {
          JSONObject tempFans = dataSuccessJson.getJSONObject(i);
          FriendsDetails tempFriendsDetails = new FriendsDetails();
          tempFriendsDetails.setFriend_id(tempFans.getInt("id"));
          tempFriendsDetails.setFriend_avatar(tempFans.getString("avatar"));
          tempFriendsDetails.setFriend_name(tempFans.getString("nickname"));
          tempFriendsDetails.setFriend_birthday(tempFans.getString("birthday"));
          tempFriendsDetails.setFriend_presonal_profile(tempFans.getString("profile"));
          tempFriendsDetails.setFriend_sex(tempFans.getInt("sex"));
          mDatas.add(tempFriendsDetails);
        }

        Message message = new Message();
        message.what = GET_FANS_READY;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void sendSearchFriendsRequest(String friendnickname) {
    HashMap<String, String> params = new HashMap<>();
    params.put("friendnickname", friendnickname);
    OkhttpUtil.postFormRequest(ServerPath.SEARCH_FRIENDS, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        boolean error = dataSuccessJson.getBoolean("error");
        String info = dataSuccessJson.getString("info");
        if (error) {
          friend_nickname = dataSuccessJson.getString("nickname");
          friend_avatar = dataSuccessJson.getString("avatar");
          friend_sex = dataSuccessJson.getInt("sex");
          friend_id = dataSuccessJson.getInt("id");

          Message message = new Message();
          message.what = FRIENDS_SEARCH_READY;
          handler.sendMessage(message);
        } else {
          ToastUtils.show(info);
        }
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void sendFollowRequest(int friendid) {
    HashMap<String, String> params = new HashMap<>();
    params.put("friendid", "" + friendid);
    params.put("follower_id", "" + Preferences.getId());
    OkhttpUtil.postFormRequest(ServerPath.FOLLOW, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        String info = dataSuccessJson.getString("info");
        boolean error = dataSuccessJson.getBoolean("error");
        if (!error) {
          ToastUtils.show(info);
        }

        Message message = new Message();
        message.what = FRIENDS_FOLLOW_READY;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void showSearchFriendsDialog() {
    // 使用LayoutInflater来加载dialog_setname.xml布局
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    View nameView = layoutInflater.inflate(R.layout.dialog_change_nickname, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    // 使用setView()方法将布局显示到dialog
    alertDialogBuilder.setView(nameView);

    final EditText userInput = (EditText) nameView.findViewById(R.id.change_profile_edit);

    userInput.setHint(R.string.friend_nicknam);

    // 设置Dialog按钮
    alertDialogBuilder
            .setTitle("查找好友")
            .setPositiveButton("查找",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        if (userInput.getText().toString().equals("")) {
                          ToastUtils.show(R.string.null_content);
                        } else if (!userInput.getText().toString().equals(Preferences.getNickname())) {
                          sendSearchFriendsRequest(userInput.getText().toString());
                        } else {
                          ToastUtils.show(R.string.not_yourself_nickname);
                        }
                      }
                    })
            .setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                      }
                    });

    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();

    // show it
    alertDialog.show();
  }

  private void showFriendsDialog(String nickname, String avatar, int sex) {
    // 使用LayoutInflater来加载dialog_setname.xml布局
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    View nameView = layoutInflater.inflate(R.layout.dialog_friends_detail, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    // 使用setView()方法将布局显示到dialog
    alertDialogBuilder.setView(nameView);

    SimpleDraweeView sdv_friends_avatar = nameView.findViewById(R.id.friends_avatar);
    TextView tv_friends_nickname = nameView.findViewById(R.id.friends_nickname);

    sdv_friends_avatar.setImageURI(avatar);
    tv_friends_nickname.setText(nickname);

    switch (friend_sex) {
      case 0://保密
        tv_friends_nickname.setCompoundDrawables(null,null, null, null);
        break;
      case 1://男
        Drawable man = getResources().getDrawable(R.drawable.ic_man);
        man.setBounds(0, 0, man.getMinimumWidth(), man.getMinimumHeight());
        tv_friends_nickname.setCompoundDrawables(null,null, man, null);
        break;
      case 2://女
        Drawable women = getResources().getDrawable(R.drawable.ic_woman);
        women.setBounds(0, 0, women.getMinimumWidth(), women.getMinimumHeight());
        tv_friends_nickname.setCompoundDrawables(null,null, women, null);
        break;
    }

    // 设置Dialog按钮
    alertDialogBuilder
            .setTitle("查找到用户")
            .setPositiveButton("添加",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        sendFollowRequest(friend_id);
                      }
                    })
            .setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                      }
                    });

    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();

    // show it
    alertDialog.show();
  }

  private void changeUiType(int uiType) {
    switch (uiType) {
      case TRENDS:
        tv_friends_title.setText(R.string.trends);
        iv_friends_search.setVisibility(View.GONE);
        iv_add_trends.setVisibility(View.VISIBLE);
        if (mDatas.size() != 0) {
          mRecyclerView.setVisibility(View.VISIBLE);
          tv_null_tips.setVisibility(View.GONE);
        } else {
          mRecyclerView.setVisibility(View.GONE);
          tv_null_tips.setText(R.string.no_trends);
          tv_null_tips.setVisibility(View.VISIBLE);
        }
        break;
      case FOLLOWS:
        tv_friends_title.setText(R.string.follows);
        iv_friends_search.setVisibility(View.VISIBLE);
        iv_add_trends.setVisibility(View.GONE);
        if (mDatas.size() != 0) {
          mRecyclerView.setVisibility(View.VISIBLE);
          tv_null_tips.setVisibility(View.GONE);
        } else {
          mRecyclerView.setVisibility(View.GONE);
          tv_null_tips.setText(R.string.no_follows);
          tv_null_tips.setVisibility(View.VISIBLE);
        }
        break;
      case FANS:
        tv_friends_title.setText(R.string.fans);
        iv_friends_search.setVisibility(View.GONE);
        iv_add_trends.setVisibility(View.GONE);
        if (mDatas.size() != 0) {
          mRecyclerView.setVisibility(View.VISIBLE);
          tv_null_tips.setVisibility(View.GONE);
        } else {
          mRecyclerView.setVisibility(View.GONE);
          tv_null_tips.setText(R.string.no_fans);
          tv_null_tips.setVisibility(View.VISIBLE);
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.friends_back:
        finish();
        break;
      case R.id.friends_search_button:
        showSearchFriendsDialog();
        break;
      case R.id.friends_add_trends:
        startActivity(new Intent(this, ShareActivity.class));
        break;
      default:
        break;
    }
  }
}
