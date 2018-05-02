package com.hoshi.graduationproject.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;

public class AlarmClockActivity extends AppCompatActivity implements View.OnClickListener {

  private boolean switch_open = false;
  private TextView tv_music_clock_time;
  private ImageView iv_music_clock_switch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setContentView(R.layout.activity_alarm_clock);

    tv_music_clock_time = findViewById(R.id.music_clock_time);
    iv_music_clock_switch = findViewById(R.id.music_clock_switch);
    ClickManager.init(this, this,
            R.id.back_alarm_clock,
            R.id.loading_button_alarm_clock,
            R.id.music_clock_ring,
            R.id.music_clock_layout,
            R.id.music_clock_switch);
  }

  public void showTimePickerDialog() {
    // Calendar c = Calendar.getInstance();
    // 创建一个TimePickerDialog实例，并把它显示出来
    // 解释一哈，Activity是context的子类
    new TimePickerDialog(this, 0,
            // 绑定监听器
            new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tv_music_clock_time.setText(hourOfDay + ":" + minute);
              }
            }
            // 设置初始时间
            , 18
            , 35
            // true表示采用24小时制
            , true).show();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_alarm_clock:
        finish();
        break;
      case R.id.loading_button_alarm_clock:
        startActivity(new Intent(this, PlayActivity.class));
        break;
      case R.id.music_clock_layout:
        showTimePickerDialog();
        break;
      case R.id.music_clock_ring:
        startActivity(new Intent(this, LocalMusicActivity.class));
        break;
      case R.id.music_clock_repeat:
        break;
      case R.id.music_clock_switch:
        if (switch_open) {
          iv_music_clock_switch.setImageResource(R.drawable.ic_switch_close);
          switch_open = false;
        } else {
          iv_music_clock_switch.setImageResource(R.drawable.ic_switch_open);
          switch_open = true;
        }
        break;
      default:
        break;
    }
  }
}
