package com.hoshi.graduationproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.adapter.CommentsAdapter;
import com.hoshi.graduationproject.model.Comments;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class FriendsTrendsActivity extends AppCompatActivity implements View.OnClickListener {

  private List<Comments> mComments = new ArrayList<>();

  private SimpleDraweeView trends_avatar;
  private TextView trends_name, trends_type, trends_date, trends_content, trends_comment, tv_no_comment;
  private EditText et_comments;
  private ListView lv_comments;

  private Handler handler;
  private final int READY = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setContentView(R.layout.activity_friends_trends);
    initPage();

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case READY:
            if (mComments.size() != 0) {
              tv_no_comment.setVisibility(View.GONE);
              lv_comments.setVisibility(View.VISIBLE);
              CommentsAdapter mAdapter = new CommentsAdapter(mComments);
              String sFormat = getString(R.string.comment_num);
              String sFinal = String.format(sFormat, mComments.size());
              trends_comment.setText(sFinal);
              lv_comments.setAdapter(mAdapter);
            } else {
              tv_no_comment.setVisibility(View.VISIBLE);
              lv_comments.setVisibility(View.GONE);
            }
            break;
          default:
            break;
        }
      }
    };

    sendGetcommentsRequest();
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
    tv_no_comment = findViewById(R.id.no_comment);
    lv_comments = findViewById(R.id.listview_comments);
    et_comments = findViewById(R.id.edittext_comment);

    trends_avatar.setImageURI(getIntent().getStringExtra("trends_avatar"));
    trends_name.setText(getIntent().getStringExtra("trends_name"));
    trends_type.setText(getIntent().getStringExtra("trends_type"));
    trends_date.setText(getIntent().getStringExtra("trends_date"));
    trends_content.setText(getIntent().getStringExtra("trends_content"));
    et_comments.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND){
          sendComments(et_comments.getText().toString());
          return true;
        }
        return false;
      }
    });

    String sFormat = getString(R.string.comment_num);
    String sFinal = String.format(sFormat, getIntent().getIntExtra("trends_comment", 0));
    trends_comment.setText(sFinal);
  }

  private void sendComments(String comments) {
    HashMap<String, String> params = new HashMap<>();
    params.put("trends_id", "" + getIntent().getIntExtra("trends_id", 0));
    params.put("comment_id", "" + Preferences.getId());
    params.put("content", comments);
    OkhttpUtil.postFormRequest(ServerPath.ADD_COMMENTS, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        if (!dataSuccessJson.getBoolean("error")) {
          ToastUtils.show(dataSuccessJson.getString("info"));
        } else {
          et_comments.setText("");
          sendGetcommentsRequest();
        }
      }

      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void sendGetcommentsRequest() {
    HashMap<String, String> params = new HashMap<>();
    params.put("trends_id", "" + getIntent().getIntExtra("trends_id", 0));
    OkhttpUtil.postFormRequest(ServerPath.GET_COMMENTS_BY_ID, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONArray dataArray = new JSONArray(result);
        mComments.clear();
        for (int i = 0; i < dataArray.length(); i ++) {
          JSONObject tempObject = dataArray.getJSONObject(i);
          Comments tempComments = new Comments();
          tempComments.setComments_avatar(tempObject.getString("avatar"));
          tempComments.setComments_nickname(tempObject.getString("nickname"));
          tempComments.setComments_date(tempObject.getString("date"));
          tempComments.setComments_content(tempObject.getString("content"));
          mComments.add(tempComments);
        }

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

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_trends_detail:
        finish();
        break;
      case R.id.loading_button_trends_detail:
        startActivity(new Intent(this, PlayActivity.class));
        break;
      default:
        break;
    }
  }
}
