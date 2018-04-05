package com.hoshi.graduationproject.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.adapter.OnMoreClickListener;
import com.hoshi.graduationproject.adapter.OnlineMusicAdapter;
import com.hoshi.graduationproject.constants.Extras;
import com.hoshi.graduationproject.enums.LoadStateEnum;
import com.hoshi.graduationproject.executor.DownloadOnlineMusic;
import com.hoshi.graduationproject.executor.PlayOnlineMusic;
import com.hoshi.graduationproject.executor.ShareOnlineMusic;
import com.hoshi.graduationproject.model.Music;
import com.hoshi.graduationproject.model.OnlineMusic;
import com.hoshi.graduationproject.model.OnlineMusicList;
import com.hoshi.graduationproject.model.SheetInfo;
import com.hoshi.graduationproject.service.AudioPlayer;
import com.hoshi.graduationproject.utils.FileUtils;
import com.hoshi.graduationproject.utils.ImageUtils;
import com.hoshi.graduationproject.utils.ScreenUtils;
import com.hoshi.graduationproject.utils.ToastUtils;
import com.hoshi.graduationproject.utils.ViewUtils;
import com.hoshi.graduationproject.utils.binding.Bind;
import com.hoshi.graduationproject.widget.AutoLoadListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OnlineMusicActivity extends BaseActivity implements OnItemClickListener
        , OnMoreClickListener, AutoLoadListView.OnLoadListener {
  private static final int MUSIC_LIST_SIZE = 20;

  @Bind(R.id.lv_online_music_list)
  private AutoLoadListView lvOnlineMusic;
  @Bind(R.id.ll_loading)
  private LinearLayout llLoading;
  @Bind(R.id.ll_load_fail)
  private LinearLayout llLoadFail;
  private View vHeader;
  private SheetInfo mListInfo;
  private OnlineMusicList mOnlineMusicList;
  private List<OnlineMusic> mMusicList = new ArrayList<>();
  private OnlineMusicAdapter mAdapter = new OnlineMusicAdapter(mMusicList);
  private int mOffset = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_online_music);
  }

  @Override
  protected void onServiceBound() {
    mListInfo = (SheetInfo) getIntent().getSerializableExtra(Extras.MUSIC_LIST_TYPE);
    setTitle(mListInfo.getTitle());

    initView();
    onLoad();
  }

  private void initView() {
    vHeader = LayoutInflater.from(this).inflate(R.layout.activity_online_music_list_header, null);
    AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dp2px(150));
    vHeader.setLayoutParams(params);
    lvOnlineMusic.addHeaderView(vHeader, null, false);
    lvOnlineMusic.setAdapter(mAdapter);
    lvOnlineMusic.setOnLoadListener(this);
    ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOADING);

    lvOnlineMusic.setOnItemClickListener(this);
    mAdapter.setOnMoreClickListener(this);
  }

  private void getMusic(final int offset) {
        /*HttpClient.getSongListInfo(mListInfo.getType(), MUSIC_LIST_SIZE, offset, new HttpCallback<OnlineMusicList>() {
            @Override
            public void onSuccess(OnlineMusicList response) {
                lvOnlineMusic.onLoadComplete();
                mOnlineMusicList = response;
                if (offset == 0 && response == null) {
                    ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    return;
                } else if (offset == 0) {
                    initHeader();
                    ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                }
                if (response == null || response.getSong_list() == null || response.getSong_list().size() == 0) {
                    lvOnlineMusic.setEnable(false);
                    return;
                }
                mOffset += MUSIC_LIST_SIZE;
                mMusicList.addAll(response.getSong_list());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {
                lvOnlineMusic.onLoadComplete();
                if (e instanceof RuntimeException) {
                    // 歌曲全部加载完成
                    lvOnlineMusic.setEnable(false);
                    return;
                }
                if (offset == 0) {
                    ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                } else {
                    ToastUtils.show(R.string.load_fail);
                }
            }
        });*/
  }

  @Override
  public void onLoad() {
    getMusic(mOffset);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    play((OnlineMusic) parent.getAdapter().getItem(position));
  }

  @Override
  public void onMoreClick(int position) {
    final OnlineMusic onlineMusic = mMusicList.get(position);
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(mMusicList.get(position).getTitle());
    String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(onlineMusic.getArtist_name(), onlineMusic.getTitle());
    File file = new File(path);
    int itemsId = file.exists() ? R.array.online_music_dialog_without_download : R.array.online_music_dialog;
    dialog.setItems(itemsId, (dialog1, which) -> {
      switch (which) {
        case 0:// 分享
          share(onlineMusic);
          break;
        case 1:// 查看歌手信息
          artistInfo(onlineMusic);
          break;
        case 2:// 下载
          download(onlineMusic);
          break;
      }
    });
    dialog.show();
  }

  private void initHeader() {
    final ImageView ivHeaderBg = (ImageView) vHeader.findViewById(R.id.iv_header_bg);
    final ImageView ivCover = (ImageView) vHeader.findViewById(R.id.iv_cover);
    TextView tvTitle = (TextView) vHeader.findViewById(R.id.tv_title);
    TextView tvUpdateDate = (TextView) vHeader.findViewById(R.id.tv_update_date);
    TextView tvComment = (TextView) vHeader.findViewById(R.id.tv_comment);
    tvTitle.setText(mOnlineMusicList.getBillboard().getName());
    tvUpdateDate.setText(getString(R.string.recent_update, mOnlineMusicList.getBillboard().getUpdate_date()));
    tvComment.setText(mOnlineMusicList.getBillboard().getComment());
    Glide.with(this)
            .load(mOnlineMusicList.getBillboard().getPic_s640())
            .asBitmap()
            .placeholder(R.drawable.default_cover)
            .error(R.drawable.default_cover)
            .override(200, 200)
            .into(new SimpleTarget<Bitmap>() {
              @Override
              public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ivCover.setImageBitmap(resource);
                ivHeaderBg.setImageBitmap(ImageUtils.blur(resource));
              }
            });
  }

  private void play(OnlineMusic onlineMusic) {
    new PlayOnlineMusic(this, onlineMusic) {
      @Override
      public void onPrepare() {
        showProgress();
      }

      @Override
      public void onExecuteSuccess(Music music) {
        cancelProgress();
        AudioPlayer.get().addAndPlay(music);
        ToastUtils.show("已添加到播放列表");
      }

      @Override
      public void onExecuteFail(Exception e) {
        cancelProgress();
        ToastUtils.show(R.string.unable_to_play);
      }
    }.execute();
  }

  private void share(final OnlineMusic onlineMusic) {
    new ShareOnlineMusic(this, onlineMusic.getTitle(), onlineMusic.getSong_id()) {
      @Override
      public void onPrepare() {
        showProgress();
      }

      @Override
      public void onExecuteSuccess(Void aVoid) {
        cancelProgress();
      }

      @Override
      public void onExecuteFail(Exception e) {
        cancelProgress();
      }
    }.execute();
  }

  private void artistInfo(OnlineMusic onlineMusic) {
    ArtistInfoActivity.start(this, onlineMusic.getTing_uid());
  }

  private void download(final OnlineMusic onlineMusic) {
    new DownloadOnlineMusic(this, onlineMusic) {
      @Override
      public void onPrepare() {
        showProgress();
      }

      @Override
      public void onExecuteSuccess(Void aVoid) {
        cancelProgress();
        ToastUtils.show(getString(R.string.now_download, onlineMusic.getTitle()));
      }

      @Override
      public void onExecuteFail(Exception e) {
        cancelProgress();
        ToastUtils.show(R.string.unable_to_download);
      }
    }.execute();
  }
}
