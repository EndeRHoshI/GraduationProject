package com.hoshi.graduationproject.discover;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.fragment.BaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DiscoverFragment extends BaseFragment {

  final int UPDATE_COVER = 1;

  private View mRootView;
  SimpleDraweeView mSimpleDraweeView[] = new SimpleDraweeView[24];
  TextView tv_updateFrequency[] = new TextView[24];
  TextView tv_name[] = new TextView[18];
  String coverUrlArray[] = new String[24];
  String rankName[] = new String[18];
  String rankUpdateFrequency[] = new String[24];
  int rankId[] = new int[24];

  private Handler handler;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    sendRequest();

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what)
        {
          case UPDATE_COVER:
            loadData();//解析json数据
            break;
          default:
            break;
        }
      }
    };
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_discover, container, false);
    return mRootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    initPage();
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onResume() {
    loadData();
    super.onResume();
  }

  public void initPage() {
    int coverIdArray[] = new int[24];
    int nameIdArray[] = new int[18];
    int updateFrequencyIdArray[] = new int[24];
    for (int i = 0; i < 24; i++) {
      coverIdArray[i] = getResources().getIdentifier("simpleDraweeView" + i, "id", mRootView.getContext().getPackageName());
      if (i < 18)
        nameIdArray[i] = getResources().getIdentifier("name" + (i + 6), "id", mRootView.getContext().getPackageName());
      updateFrequencyIdArray[i] = getResources().getIdentifier("updateFrequency" + i, "id", mRootView.getContext().getPackageName());
    }
    for (int i = 0; i < 24; i++) {
      mSimpleDraweeView[i] = (SimpleDraweeView) getActivity().findViewById(coverIdArray[i]);
      if (i < 18)
        tv_name[i] = (TextView) getActivity().findViewById(nameIdArray[i]);
      tv_updateFrequency[i] = (TextView) getActivity().findViewById(updateFrequencyIdArray[i]);
    }
  }

  public void loadData() {
    for (int i = 0; i < 24; i++) {
      if (i == 0)
        mSimpleDraweeView[i].setImageURI("http://ogtt75s5d.bkt.clouddn.com/ic_biaosheng.png");
      else {
        mSimpleDraweeView[i].setImageURI(coverUrlArray[i]);
      }
      if (i < 18)
        tv_name[i].setText(rankName[i]);
      tv_updateFrequency[i].setText(rankUpdateFrequency[i]);
    }
    for (int i = 0; i < 24; i++) {
      final int tempId = rankId[i];
      final String tempUrl;
      if (i == 0) {
        tempUrl = "http://ogtt75s5d.bkt.clouddn.com/ic_biaosheng.png";
      } else {
        tempUrl = coverUrlArray[i];
      }
      mSimpleDraweeView[i].setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent rankIntent = new Intent(getContext() ,RankDetailActivity.class);
          rankIntent.putExtra("id", tempId);
          rankIntent.putExtra("avatarUrl", tempUrl);
          startActivity(rankIntent);
        }
      });
    }
  }

  public void sendRequest() {
    OkHttpClient mOkHttpClient = new OkHttpClient();
    final Request request = new Request.Builder()
            .url("http://music.163.com/api/toplist")
            .build();

    Call rankCall = mOkHttpClient.newCall(request);
    rankCall.enqueue(new Callback() {
      @Override
      public void onFailure(Call failureCall, IOException e) {
      }

      @Override
      public void onResponse(Call responseCall, final Response response) throws IOException {
        String htmlStr = response.body().string();
        try {
          JSONObject dataJson = new JSONObject(htmlStr);
          JSONArray rankArray = dataJson.getJSONArray("list");
          for (int i = 0; i < rankArray.length(); i++) {
            coverUrlArray[i] = rankArray.getJSONObject(i).getString("coverImgUrl");
            if (i < 18)
              rankName[i] = rankArray.getJSONObject(i + 6).getString("name");
            rankUpdateFrequency[i] = rankArray.getJSONObject(i).getString("updateFrequency");
            rankId[i] = rankArray.getJSONObject(i).getInt("id");
          }

          Message message = new Message();
          message.what = UPDATE_COVER;
          handler.sendMessage(message);
        } catch (JSONException e) {
        }
      }
    });
  }
}
