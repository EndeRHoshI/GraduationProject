package com.hoshi.graduationproject.executor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.PlaylistActivity;
import com.hoshi.graduationproject.model.Music;
import com.hoshi.graduationproject.service.AudioPlayer;
import com.hoshi.graduationproject.service.OnPlayerEventListener;
import com.hoshi.graduationproject.utils.CoverLoader;
import com.hoshi.graduationproject.utils.binding.Bind;
import com.hoshi.graduationproject.utils.binding.ViewBinder;

/**
 * Created by hzwangchenyan on 2018/1/26.
 */
public class ControlPanel implements View.OnClickListener, OnPlayerEventListener {
    @Bind(R.id.iv_play_bar_cover)
    private ImageView ivPlayBarCover;
    @Bind(R.id.tv_play_bar_title)
    private TextView tvPlayBarTitle;
    @Bind(R.id.tv_play_bar_artist)
    private TextView tvPlayBarArtist;
    @Bind(R.id.iv_play_bar_play)
    private ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    private ImageView ivPlayBarNext;
    @Bind(R.id.v_play_bar_playlist)
    private ImageView vPlayBarPlaylist;
    @Bind(R.id.pb_play_bar)
    private ProgressBar mProgressBar;

    public ControlPanel(View view) {
        ViewBinder.bind(this, view);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        vPlayBarPlaylist.setOnClickListener(this);
        onChange(AudioPlayer.get().getPlayMusic());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_bar_play:
                AudioPlayer.get().playPause();
                break;
            case R.id.iv_play_bar_next:
                AudioPlayer.get().next();
                break;
            case R.id.v_play_bar_playlist:
                Context context = vPlayBarPlaylist.getContext();
                Intent intent = new Intent(context, PlaylistActivity.class);
                context.startActivity(intent);
                break;
        }
    }

    @Override
    public void onChange(Music music) {
        if (music == null) {
            return;
        }
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress((int) AudioPlayer.get().getAudioPosition());
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
    }

    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }
}