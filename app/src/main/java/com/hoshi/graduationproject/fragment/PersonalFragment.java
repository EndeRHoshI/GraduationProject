package com.hoshi.graduationproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.LoginOrRegisterActivity;
import com.hoshi.graduationproject.activity.PlayActivity;
import com.hoshi.graduationproject.activity.PresonalActivity;
import com.hoshi.graduationproject.activity.FriendsActivity;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;

public class PersonalFragment extends BaseFragment implements View.OnClickListener {

  final int LOGIN_STAT = 0;
  final int LOGOUT_STAT = 1;

  private TextView tv_nickname, tv_trends, tv_follows, tv_fans;
  SimpleDraweeView draweeView;
  private View mRootView;
  private Handler handler;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case LOGIN_STAT:
            loadData();//登录状态，载入数据
            break;
          case LOGOUT_STAT:
            Preferences.clear();//不是登录状态，清除数据
            loadData();
            break;
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
    mRootView = inflater.inflate(R.layout.fragment_personal, container, false);
    return mRootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    draweeView = getActivity().findViewById(R.id.head_pic);
    tv_nickname = getActivity().findViewById(R.id.nickname_textview);
    tv_trends = getActivity().findViewById(R.id.trends_textview);
    tv_follows = getActivity().findViewById(R.id.follows_textview);
    tv_fans = getActivity().findViewById(R.id.fans_textview);

    ClickManager.init(getView(), this,
            R.id.avatar_nickname_layout,
            R.id.trends_layout,
            R.id.follows_layout,
            R.id.fans_layout,
            R.id.music_clock_layout,
            R.id.change_skin_layout,
            R.id.night_mode_layout,
            R.id.feedback_layout,
            R.id.logout_textview,
            R.id.loading_button);

  }

  @Override
  public void onResume() {
    super.onResume();
    checkLogin();
  }

  public void checkLogin() {
    String nickname = "" + Preferences.getNickname();
    if (nickname.equals("")) {
      Preferences.clear();
      loadData();
      return;
    }
    String id = "" + Preferences.getId();
    HashMap<String, String> params = new HashMap<>();
    params.put("id", id);
    OkhttpUtil.postFormRequest(ServerPath.CHECK_LOGIN, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        JSONObject friendsJson = dataSuccessJson.getJSONObject("friends");
        String sessionId = dataSuccessJson.getString("sessionId");

        if (!sessionId.equals(Preferences.getSessionId())) {
          Message message = new Message();
          message.what = LOGOUT_STAT;
          handler.sendMessage(message);
        } else {
          Preferences.saveNickname(dataSuccessJson.getString("nickname"));
          Preferences.saveFriends(friendsJson.getInt("trends"),
                  friendsJson.getInt("follows"),
                  friendsJson.getInt("fans"));

          Message message = new Message();
          message.what = LOGIN_STAT;
          handler.sendMessage(message);
        }
      }

      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void loadData() {
    String nickname = Preferences.getNickname();
    int friends[] = Preferences.getFriends();

    draweeView.setImageURI(Preferences.getAvatar());
    if (!nickname.equals("")) {
      tv_nickname.setText(nickname);
      tv_trends.setText("" + friends[0]);
      tv_follows.setText("" + friends[1]);
      tv_fans.setText("" + friends[2]);
    } else {
      tv_nickname.setText(R.string.no_login);
      tv_trends.setText("--");
      tv_follows.setText("--");
      tv_fans.setText("--");
    }
  }

  private void logout(String phone) {
    HashMap<String, String> params = new HashMap<>();
    params.put("phone", phone);
    OkhttpUtil.postFormRequest(ServerPath.LOGOUT, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        boolean error = dataSuccessJson.getBoolean("error");
        String info = dataSuccessJson.getString("info");
        if (error) {
          Preferences.clear();
          loadData();
        }
        ToastUtils.show(info);
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
      case R.id.avatar_nickname_layout:
        if (!Preferences.getNickname().equals("")) {
          startActivity(new Intent(getActivity(), PresonalActivity.class));
        } else {
          startActivity(new Intent(getActivity(), LoginOrRegisterActivity.class));
        }
        break;
      case R.id.trends_layout:
        if (!Preferences.getNickname().equals("")) {
          startActivity(new Intent(getActivity(), FriendsActivity.class).putExtra("type", 1));
        } else {
          Toast.makeText(getActivity(), R.string.please_login, Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.follows_layout:
        if (!Preferences.getNickname().equals("")) {
          startActivity(new Intent(getActivity(), FriendsActivity.class).putExtra("type", 2));
        } else {
          Toast.makeText(getActivity(), R.string.please_login, Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.fans_layout:
        if (!Preferences.getNickname().equals("")) {
          startActivity(new Intent(getActivity(), FriendsActivity.class).putExtra("type", 3));
        } else {
          Toast.makeText(getActivity(), R.string.please_login, Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.music_clock_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.change_skin_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.night_mode_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.feedback_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.logout_textview:
        if (!Preferences.getNickname().equals("")) {
          logout(Preferences.getPhone());
        } else {
          ToastUtils.show(R.string.no_login);
        }
        break;
      case R.id.loading_button:
        startActivity(new Intent(getActivity(), PlayActivity.class));
        break;
    }
  }
}
