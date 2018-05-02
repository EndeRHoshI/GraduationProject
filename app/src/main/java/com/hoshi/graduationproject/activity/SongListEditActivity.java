package com.hoshi.graduationproject.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
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

public class SongListEditActivity extends AppCompatActivity implements View.OnClickListener{

  private SimpleDraweeView sdv_song_list_cover;
  private TextView tv_song_list_name;
  private Handler handler;
  private final int UPDATE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setContentView(R.layout.activity_song_list_setting);

    sdv_song_list_cover = findViewById(R.id.song_list_cover);
    tv_song_list_name = findViewById(R.id.song_list_name);

    sdv_song_list_cover.setImageURI(getIntent().getStringExtra("list_avatar"));
    tv_song_list_name.setText(getIntent().getStringExtra("list_name"));

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what)
        {
          case UPDATE:
            initPage();
            break;
          default:
            break;
        }
      }
    };

    ClickManager.init(this, this,
            R.id.edit_song_list_back,
            R.id.song_list_cover_layout,
            R.id.song_list_name_layout);
  }

  private void initPage() {
  }

  private void showChangeListNameDialog() {
    // 使用LayoutInflater来加载dialog_setname.xml布局
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    View nameView = layoutInflater.inflate(R.layout.dialog_change_nickname, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    // 使用setView()方法将布局显示到dialog
    alertDialogBuilder.setView(nameView);

    final EditText userInput = (EditText) nameView.findViewById(R.id.change_nickname_edit);

    userInput.setHint(R.string.song_list_name);

    // 设置Dialog按钮
    alertDialogBuilder
            .setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        // 获取edittext的内容
                        if (!userInput.getText().toString().equals(Preferences.getNickname())) {
                          sendChangeListNameRequest(userInput.getText().toString());
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

  private void sendChangeListNameRequest(String list_name) {
    ToastUtils.show(getIntent().getStringExtra("list_id"));
    HashMap<String, String> params = new HashMap<>();
    params.put("list_id", "" + getIntent().getIntExtra("list_id", -1));
    params.put("list_name", list_name);
    OkhttpUtil.postFormRequest(ServerPath.CHANGE_SONG_LIST_NAME, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        String info = dataSuccessJson.getString("info");
        ToastUtils.show(info);

        if (dataSuccessJson.getBoolean("error")) {
          tv_song_list_name.setText(list_name);
        } else ToastUtils.show(info);
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
      case R.id.edit_song_list_back:
        finish();
        break;
      case R.id.song_list_cover_layout:
        break;
      case R.id.song_list_name_layout:
        showChangeListNameDialog();
        break;
      default:
        break;
    }
  }
}
