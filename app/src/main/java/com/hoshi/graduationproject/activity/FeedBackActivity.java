package com.hoshi.graduationproject.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.ToastUtils;

public class FeedBackActivity extends AppCompatActivity implements View.OnClickListener{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setContentView(R.layout.activity_feed_back);

    ClickManager.init(this, this,
            R.id.back_feed_back,
            R.id.feedback_send_textView);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_feed_back:
        finish();
        break;
      case R.id.feedback_send_textView:
        ToastUtils.show(R.string.send_success);
        finish();
        break;
    }
  }
}
