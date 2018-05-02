package com.hoshi.graduationproject.activity;

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

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {

  private EditText et_trends;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setContentView(R.layout.activity_share);

    et_trends = findViewById(R.id.trends_editText);

    ClickManager.init(this, this,
            R.id.send_textView,
            R.id.share_trends_back,
            R.id.send_textView);
  }

  private void sendTrends(String trend) {
    HashMap<String, String> params = new HashMap<>();
    params.put("author_id", "" + Preferences.getId());
    params.put("author_name", "" + Preferences.getNickname());
    params.put("content", trend);
    OkhttpUtil.postFormRequest(ServerPath.TRENDS, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        boolean error = dataSuccessJson.getBoolean("error");
        String info = dataSuccessJson.getString("info");
        ToastUtils.show(info);
        if (error) {
          finish();
        }
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
      case R.id.share_trends_back:
        finish();
        break;
      case R.id.send_textView:
        String mTrends = et_trends.getText().toString();
        if (!mTrends.equals("")) {
          sendTrends(mTrends);
        } else {
          ToastUtils.show(R.string.null_trends);
        }
        break;
      default:
        break;
    }
  }
}
