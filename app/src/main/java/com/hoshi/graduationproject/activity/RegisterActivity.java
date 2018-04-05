package com.hoshi.graduationproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.hoshi.graduationproject.MainActivity;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

  EditText et_phone, et_nickname, et_password, et_confirm_password;
  String phone, nickname, password, confirm_password;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    et_phone            = findViewById(R.id.phone_edittext);
    et_nickname         = findViewById(R.id.nickname_edittext);
    et_password         = findViewById(R.id.password_edittext);
    et_confirm_password = findViewById(R.id.password_confirm_edittext);

    ClickManager.init(this, this,
            R.id.register_back,
            R.id.register_button);
  }

  public void sendRegisterRequest(String phone, String nickname, String password) {
    HashMap<String, String> params = new HashMap<>();
    params.put("phone", phone);
    params.put("nickname", nickname);
    params.put("password", password);
    OkhttpUtil.postFormRequest(ServerPath.REGISTER, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        JSONObject friendsJson = dataSuccessJson.getJSONObject("friends");
        boolean error = dataSuccessJson.getBoolean("error");
        String info = dataSuccessJson.getString("info");
        if (error) {
          Preferences.saveNickname(dataSuccessJson.getString("nickname"));
          Preferences.savePhone(dataSuccessJson.getString("phone"));
          Preferences.saveId(dataSuccessJson.getInt("id"));
          Preferences.saveSessionId(dataSuccessJson.getString("sessionId"));
          Preferences.saveFriends(friendsJson.getInt("trends"),
                  friendsJson.getInt("follows"),
                  friendsJson.getInt("fans"));
          startActivity(new Intent(getBaseContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        ToastUtils.show(info);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  public boolean judgeIllegal() {
    phone            = et_phone           .getText().toString();
    nickname         = et_nickname        .getText().toString();
    password         = et_password        .getText().toString();
    confirm_password = et_confirm_password.getText().toString();

    if (phone.equals("")) {
      ToastUtils.show("手机号码不能为空");
      return false;
    }
    if (nickname.equals("")) {
      ToastUtils.show("昵称不能为空");
      return false;
    }
    if (password.equals("")) {
      ToastUtils.show("密码不能为空");
      return false;
    }
    if (!password.equals(confirm_password)) {
      ToastUtils.show("两次输入的密码不相同");
      return false;
    }
    return true;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.register_back:
        finish();
        break;
      case R.id.register_button:
        if (judgeIllegal()) sendRegisterRequest(phone, nickname, password);
        break;
      default:
        break;
    }
  }
}
