package com.hoshi.graduationproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.utils.ClickManager;

public class FriendsTrendsActivity extends AppCompatActivity implements View.OnClickListener {

  private SimpleDraweeView trends_avatar;
  private TextView trends_name, trends_type, trends_date, trends_content, trends_comment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friends_trends);

    initPage();

    ClickManager.init(this, this,
            R.id.back_trends_detail,
            R.id.loading_button_trends_detail);
  }

  private void initPage() {
    trends_avatar = findViewById(R.id.trends_avatar);
    trends_name = findViewById(R.id.trends_detail_name);
    trends_type = findViewById(R.id.trends_detail_type);
    trends_date = findViewById(R.id.trends_detail_date);
    trends_content = findViewById(R.id.trends_detail_content);
    trends_comment = findViewById(R.id.trends_detail_comment);

    trends_avatar.setImageURI(getIntent().getStringExtra("trends_avatar"));
    trends_name.setText(getIntent().getStringExtra("trends_name"));
    trends_type.setText(getIntent().getStringExtra("trends_type"));
    trends_date.setText(getIntent().getStringExtra("trends_date"));
    trends_content.setText(getIntent().getStringExtra("trends_content"));

    String sFormat = getString(R.string.comment_num);
    String sFinal = String.format(sFormat, getIntent().getIntExtra("trends_comment", 0));
    trends_comment.setText(sFinal);
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
      default:
        break;
    }
  }
}
