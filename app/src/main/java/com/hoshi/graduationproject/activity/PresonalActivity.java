package com.hoshi.graduationproject.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.uploadimg.Auth;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Request;

public class PresonalActivity extends AppCompatActivity implements View.OnClickListener{

  private Handler handler;
  private SimpleDraweeView sdv_avatar;
  private TextView tv_nickname, tv_sex, tv_birthday, tv_presonal_profile;

  Calendar nowdate = Calendar.getInstance();
  int mYear, mMonth, mDay;

  private String[] sexArray = new String[]{"保密", "男", "女"};// 性别选择

  private final int UPDATE = 1;
  private final int GALLERY_ACTIVITY_CODE = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setContentView(R.layout.activity_presonal);

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

    initPage();

    ClickManager.init(this, this,
            R.id.presonal_detail_back,
            R.id.presonal_avatar_layout,
            R.id.presonal_nickname_layout,
            R.id.presonal_sex_layout,
            R.id.presonal_birthday_layout,
            R.id.presonal_profile_layout);
  }

  private void initPage() {
    String birthday = Preferences.getBirthday();

    if (!birthday.equals("")) {
      DateFormat mdf = new SimpleDateFormat("yyyy-MM-dd");
      try {
        Date mDate = mdf.parse(birthday);
        nowdate.setTime(mDate);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    mYear = nowdate.get(Calendar.YEAR);
    mMonth = nowdate.get(Calendar.MONTH);
    mDay = nowdate.get(Calendar.DAY_OF_MONTH);

    sdv_avatar = findViewById(R.id.presonal_detail_avatar);
    tv_nickname = findViewById(R.id.presonal_nickname);
    tv_sex = findViewById(R.id.presonal_sex);
    tv_birthday = findViewById(R.id.presonal_birthday);
    tv_presonal_profile = findViewById(R.id.presonal_profile);

    sdv_avatar.setImageURI(Preferences.getAvatar());
    tv_nickname.setText(Preferences.getNickname());

    int sex = Preferences.getSex();
    if (sex == 0) { //未知，保密
      tv_sex.setText(R.string.secret);
    } else if (sex == 1) { //男
      tv_sex.setText(R.string.male);
    } else { //女
      tv_sex.setText(R.string.female);
    }
    tv_birthday.setText(Preferences.getBirthday());
    tv_presonal_profile.setText(Preferences.getProfile());
  }

  private void showChangeNicknameDialog() {
    // 使用LayoutInflater来加载dialog_setname.xml布局
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    View nameView = layoutInflater.inflate(R.layout.dialog_change_nickname, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    // 使用setView()方法将布局显示到dialog
    alertDialogBuilder.setView(nameView);

    final EditText userInput = (EditText) nameView.findViewById(R.id.change_nickname_edit);

    userInput.setHint(R.string.new_nicknam);

    // 设置Dialog按钮
    alertDialogBuilder
            .setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        // 获取edittext的内容
                        if (!userInput.getText().toString().equals(Preferences.getNickname())) {
                          sendChangeNicknameRequest(userInput.getText().toString());
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

  private void showChangeProfileDialog() {
    // 使用LayoutInflater来加载dialog_setname.xml布局
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    View nameView = layoutInflater.inflate(R.layout.dialog_change_profile, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    // 使用setView()方法将布局显示到dialog
    alertDialogBuilder.setView(nameView);

    final EditText userInput = nameView.findViewById(R.id.change_nickname_edit);

    userInput.setText(Preferences.getProfile());
    userInput.setSelection(Preferences.getProfile().length());

    // 设置Dialog按钮
    alertDialogBuilder
            .setTitle(R.string.change_profile)
            .setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        // 获取edittext的内容
                        if (!userInput.getText().toString().equals(Preferences.getNickname())) {
                          sendChangeProfileRequest(userInput.getText().toString());
                        }
                      }
                    })
            .setNegativeButton(R.string.cancel,
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

  private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      int mYear = year;
      int mMonth = monthOfYear + 1;
      int mDay = dayOfMonth;
      String days;
      days = new StringBuffer().append(mYear).append("-").append(mMonth).append("-").append(mDay).toString();
      sendChangeBirthdayRequest(days);
    }
  };

  private void sendChangeNicknameRequest(String nickname) {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    params.put("nickname", nickname);
    OkhttpUtil.postFormRequest(ServerPath.CHANGE_NICKNAME, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        if (dataSuccessJson.getBoolean("error")) {
          Preferences.saveNickname(nickname);
        } else {
          ToastUtils.show(dataSuccessJson.getString("info"));
        }

        Message message = new Message();
        message.what = UPDATE;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void sendChangeBirthdayRequest(String birthday) {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    params.put("birthday", birthday);
    OkhttpUtil.postFormRequest(ServerPath.CHANGE_BIRTHDAY, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        if (dataSuccessJson.getBoolean("error")) {
          Preferences.saveBirthday(birthday);
        } else {
          ToastUtils.show(dataSuccessJson.getString("info"));
        }

        Message message = new Message();
        message.what = UPDATE;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void sendChangeProfileRequest(String profile) {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    params.put("profile", profile);
    OkhttpUtil.postFormRequest(ServerPath.CHANGE_PROFILE, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        if (dataSuccessJson.getBoolean("error")) {
          Preferences.saveProfile(profile);
        } else {
          ToastUtils.show(dataSuccessJson.getString("info"));
        }

        Message message = new Message();
        message.what = UPDATE;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void showChangeSexDialog() {
    AlertDialog.Builder sexChooseBuilder = new AlertDialog.Builder(this);// 自定义对话框
    sexChooseBuilder.setSingleChoiceItems(sexArray, Preferences.getSex(), new DialogInterface.OnClickListener() {// 2默认的选中

      @Override
      public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
        sendChangeSexRequest(which);
        dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
      }
    });
    sexChooseBuilder.show();// 让弹出框显示
  }

  private void sendChangeSexRequest(int sex) {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    params.put("sex", "" + sex);
    OkhttpUtil.postFormRequest(ServerPath.CHANGE_SEX, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        if (dataSuccessJson.getBoolean("error")) {
          Preferences.saveSex(sex);
        } else {
          ToastUtils.show(dataSuccessJson.getString("info"));
        }

        Message message = new Message();
        message.what = UPDATE;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void uploadImg2QiNiu(String picPath) {
    UploadManager uploadManager = new UploadManager();
    // 设置图片名字
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String key = "user"  + Preferences.getId() + "_" + sdf.format(new Date());
    Log.i("", "picPath: " + picPath);
    uploadManager.put(picPath, key, Auth.create(Auth.AccessKey, Auth.SecretKey).uploadToken("hoshimusic"), new UpCompletionHandler() {
      @Override
      public void complete(String key, ResponseInfo info, JSONObject res) {
        // info.error中包含了错误信息，可打印调试
        // 上传成功后将key值上传到自己的服务器
        if (info.isOK()) {
          sendChangeAvatarRequest("http://p7eu09pkq.bkt.clouddn.com/" + key);
        }
      }
    }, null);
  }

  private void sendChangeAvatarRequest(String path) {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    params.put("avatar", path);
    OkhttpUtil.postFormRequest(ServerPath.CHANGE_AVATAR, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        if (dataSuccessJson.getBoolean("error")) {
          Preferences.saveAvatar(path);
        } else {
          ToastUtils.show(dataSuccessJson.getString("info"));
        }

        Message message = new Message();
        message.what = UPDATE;
        handler.sendMessage(message);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case GALLERY_ACTIVITY_CODE:
          String[] proj = {MediaStore.Images.Media.DATA};
          //好像是android多媒体数据库的封装接口，具体的看Android文档
          Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
          //按我个人理解 这个是获得用户选择的图片的索引值
          int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          //将光标移至开头 ，这个很重要，不小心很容易引起越界
          cursor.moveToFirst();
          //最后根据索引值获取图片路径
          String path = cursor.getString(column_index);
          //ToastUtils.show(path);
          uploadImg2QiNiu(path);
          break;
      }
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.presonal_detail_back:
        finish();
        break;
      case R.id.presonal_avatar_layout:
        startActivityForResult(new Intent(Intent.ACTION_PICK, null).setType("image/*"), GALLERY_ACTIVITY_CODE);
        break;
      case R.id.presonal_nickname_layout:
        showChangeNicknameDialog();
        break;
      case R.id.presonal_sex_layout:
        showChangeSexDialog();
        break;
      case R.id.presonal_birthday_layout:
        new DatePickerDialog(this, onDateSetListener, mYear, mMonth, mDay).show();
        break;
      case R.id.presonal_profile_layout:
        showChangeProfileDialog();
        break;
    }
  }
}
