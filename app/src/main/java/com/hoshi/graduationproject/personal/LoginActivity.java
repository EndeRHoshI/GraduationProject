package com.hoshi.graduationproject.personal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.util.ClickManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    ClickManager.init(this, this, R.id.back);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back:
        finish();
        break;
    }
  }
}
