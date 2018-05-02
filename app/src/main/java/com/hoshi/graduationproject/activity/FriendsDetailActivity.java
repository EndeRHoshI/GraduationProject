package com.hoshi.graduationproject.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.fragment.FriendsDetailFragment;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;

public class FriendsDetailActivity extends AppCompatActivity implements View.OnClickListener {

  private TextView tv_friends_detail_nickname, tv_friends_follows_fans, tv_friends_follow_button;
  private SimpleDraweeView sdv_friends_detail_avatar;

  // 关注关系，0为互相未关注，1为我关注他，2为他关注我，3为互相关注
  private int friend_follow_num, friend_fans_num, friend_follow_type,
          friend_id;
  private String friend_birthday, friend_profile;
  private final int READY = 0;
  private final int CHANGE_TYPE_READY = 1;

  private Handler handler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setContentView(R.layout.activity_friends_detail);

    friend_id = getIntent().getIntExtra("id", 0);
    friend_birthday = getIntent().getStringExtra("birthday");
    friend_profile = getIntent().getStringExtra("profile");

    sendGetFriendDetailRequest(Preferences.getId(), friend_id,
            getIntent().getIntExtra("follow_type", -1));

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case READY:
            initPage();
            break;
          case CHANGE_TYPE_READY:
            changeButtonText(friend_follow_type);
            break;
          default:
            break;
        }
      }
    };

    ClickManager.init(this, this,
            R.id.back_friends_detail,
            R.id.loading_button_friends_detail,
            R.id.friends_follow_button);
  }

  private void sendGetFriendDetailRequest(int id, int friend_id, int page_type) {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + id);
    params.put("friend_id", "" + friend_id);
    params.put("page_type", "" + page_type);
    OkhttpUtil.postFormRequest(ServerPath.GET_FRIENDS_DETAIL, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        friend_follow_num = dataSuccessJson.getInt("follows");
        friend_fans_num = dataSuccessJson.getInt("fans");
        friend_follow_type = dataSuccessJson.getInt("followtype");

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

  private void initPage() {
    sdv_friends_detail_avatar = findViewById(R.id.simpleDraweeView_friend_detail_avatar);
    tv_friends_detail_nickname = findViewById(R.id.friends_detail_nickname);
    tv_friends_follows_fans = findViewById(R.id.friends_follows_fans);
    tv_friends_follow_button = findViewById(R.id.friends_follow_button);

    String sFormat = getString(R.string.follow_fans);
    String sFinal = String.format(sFormat, friend_follow_num, friend_fans_num);

    sdv_friends_detail_avatar.setImageURI(getIntent().getStringExtra("avatar"));
    tv_friends_detail_nickname.setText(getIntent().getStringExtra("nickname"));
    tv_friends_follows_fans.setText(sFinal);

    changeButtonText(friend_follow_type);

    switch (getIntent().getIntExtra("sex", -1)) {
      case 0://保密
        tv_friends_detail_nickname.setCompoundDrawables(null, null, null, null);
        break;
      case 1://男
        Drawable man = getResources().getDrawable(R.drawable.ic_man);
        man.setBounds(0, 0, man.getMinimumWidth(), man.getMinimumHeight());
        tv_friends_detail_nickname.setCompoundDrawables(null, null, man, null);
        break;
      case 2://女
        Drawable women = getResources().getDrawable(R.drawable.ic_woman);
        women.setBounds(0, 0, women.getMinimumWidth(), women.getMinimumHeight());
        tv_friends_detail_nickname.setCompoundDrawables(null, null, women, null);
        break;
    }

    String[] title = {"音乐", "动态", "关于TA"};
    FriendsDetailFragment fragment = FriendsDetailFragment.newInstance(0, title, friend_id,
            friend_birthday, friend_profile, getIntent().getIntExtra("sex", -1));
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.add(R.id.friends_detail_tab_container, fragment);
    transaction.commitAllowingStateLoss();
  }

  private void changeButtonText(int friend_follow_type) {
    switch (friend_follow_type) {
      case 0:
      case 2:
        tv_friends_follow_button.setText(R.string.button_no_follow);
        break;
      case 1:
        tv_friends_follow_button.setText(R.string.button_already_follow);
        break;
      case 3:
        tv_friends_follow_button.setText(R.string.button_follow_each_other);
        break;
    }
    String sFormat = getString(R.string.follow_fans);
    String sFinal = String.format(sFormat, friend_follow_num, friend_fans_num);

    tv_friends_follows_fans.setText(sFinal);
  }

  private void sendCancelRequest(int friendid) {
    HashMap<String, String> params = new HashMap<>();
    params.put("friendid", "" + friendid);
    params.put("follower_id", "" + Preferences.getId());
    OkhttpUtil.postFormRequest(ServerPath.CANCEL, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        String info = dataSuccessJson.getString("info");
        boolean error = dataSuccessJson.getBoolean("error");
        friend_fans_num = dataSuccessJson.getInt("fans");
        if (!error) {
          ToastUtils.show(info);
        }

        if (friend_follow_type == 1)
          friend_follow_type = 0;
        else friend_follow_type = 2;

        Message message = new Message();
        message.what = CHANGE_TYPE_READY;
        handler.sendMessage(message);
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
    OkhttpUtil.postFormRequest(ServerPath.FOLLOW, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        String info = dataSuccessJson.getString("info");
        boolean error = dataSuccessJson.getBoolean("error");
        friend_fans_num = dataSuccessJson.getInt("fans");
        if (!error) {
          ToastUtils.show(info);
        }

        if (friend_follow_type == 2)
          friend_follow_type = 3;
        else friend_follow_type = 1;

        Message message = new Message();
        message.what = CHANGE_TYPE_READY;
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
      case R.id.back_friends_detail:
        finish();
        break;
      case R.id.loading_button_friends_detail:
        startActivity(new Intent(this, PlayActivity.class));
        break;
      case R.id.friends_follow_button:
        if (friend_follow_type == 1 || friend_follow_type == 3)
          sendCancelRequest(friend_id);
        else
          sendFollowRequest(friend_id);
        break;
    }
  }
}
