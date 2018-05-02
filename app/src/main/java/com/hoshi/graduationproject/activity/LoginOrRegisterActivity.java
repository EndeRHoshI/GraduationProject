package com.hoshi.graduationproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;

public class LoginOrRegisterActivity extends AppCompatActivity implements View.OnClickListener{

  private String phone, password;
  private EditText et_phone, et_password;
  private Context mContext;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setContentView(R.layout.activity_login_or_register);

    if (mContext == null) {
      mContext = this;
    }

    et_phone    = findViewById(R.id.phone_edittext);
    et_password = findViewById(R.id.password_edittext);

    ClickManager.init(this, this,
            R.id.login_button,
            R.id.register_button,
            R.id.login_or_register_back,
            R.id.forget_password);
  }

  public void sendLoginRequest(String phone, String password) {
    HashMap<String, String> params = new HashMap<>();
    params.put("phone", phone);
    params.put("password", password);
    OkhttpUtil.postFormRequest(ServerPath.LOGIN, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        boolean error = dataSuccessJson.getBoolean("error");
        String info = dataSuccessJson.getString("info");
        if (error) {
          JSONObject friendsJson = dataSuccessJson.getJSONObject("friends");
          Preferences.saveNickname(dataSuccessJson.getString("nickname"));
          Preferences.saveAvatar(dataSuccessJson.getString("avatar"));
          Preferences.saveProfile(dataSuccessJson.getString("profile"));
          Preferences.saveSex(dataSuccessJson.getInt("sex"));
          Preferences.saveBirthday(dataSuccessJson.getString("birthday"));
          Preferences.saveNickname(dataSuccessJson.getString("nickname"));
          Preferences.savePhone(dataSuccessJson.getString("phone"));
          Preferences.saveId(dataSuccessJson.getInt("id"));
          Preferences.saveSessionId(dataSuccessJson.getString("sessionId"));
          Preferences.saveFriends(friendsJson.getInt("trends"),
                  friendsJson.getInt("follows"),
                  friendsJson.getInt("fans"));
          finish();
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

  public boolean judgeIllegal() {
    phone            = et_phone   .getText().toString();
    password         = et_password.getText().toString();

    if (phone.equals("")) {
      ToastUtils.show("手机号码不能为空");
      return false;
    }
    if (password.equals("")) {
      ToastUtils.show("密码不能为空");
      return false;
    }
    return true;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.login_button:
        if (judgeIllegal()) {
          sendLoginRequest(phone, password);
        }
        break;
      case R.id.register_button:
        startActivity(new Intent(this, RegisterActivity.class));
        break;
      case R.id.login_or_register_back:
        finish();
        break;
      case R.id.forget_password:
        ToastUtils.show("暂未开发");
        break;
      default:
        break;
    }
  }
}
