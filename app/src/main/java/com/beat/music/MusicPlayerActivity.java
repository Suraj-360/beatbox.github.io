package com.beat.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.beat.music.databinding.ActivityMusicPlayerBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    ActivityMusicPlayerBinding activityMusicPlayerBinding;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    AnimationDrawable animationDrawable;
    Visualizer visualizer;
    int x = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMusicPlayerBinding = ActivityMusicPlayerBinding.inflate(getLayoutInflater());
        setContentView(activityMusicPlayerBinding.getRoot());
        activityMusicPlayerBinding.songTitle.setSelected(true);
        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null)
                {
                    activityMusicPlayerBinding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    activityMusicPlayerBinding.currentTime.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if(mediaPlayer.isPlaying())
                    {
                        activityMusicPlayerBinding.playPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                    }
                    else
                    {

                        activityMusicPlayerBinding.playPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });

        activityMusicPlayerBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser)
                {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        int audioSessionId = mediaPlayer.getAudioSessionId();
        if(audioSessionId !=-1)
        {
            activityMusicPlayerBinding.visualizer.setAudioSessionId(audioSessionId);
        }

    }

    @SuppressLint("SetTextI18n")
    void setResourcesWithMusic()
    {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);
        if(currentSong.getDuration()!=null) {
            String totalTime = convertToMMSS(currentSong.getDuration());
            activityMusicPlayerBinding.songTitle.setText(currentSong.getTitle());
            activityMusicPlayerBinding.totalTime.setText(totalTime);
            activityMusicPlayerBinding.playPause.setOnClickListener(v -> pausePlayMusic());
            activityMusicPlayerBinding.playNext.setOnClickListener(v -> playNextMusic());
            activityMusicPlayerBinding.playPrevious.setOnClickListener(v -> playPreviousMusic());
            playMusic();
        }
        else
        {
            activityMusicPlayerBinding.totalTime.setText("00:00");
            activityMusicPlayerBinding.songTitle.setText(currentSong.getTitle());
            activityMusicPlayerBinding.playNext.setOnClickListener(v -> playNextMusic());
            activityMusicPlayerBinding.playPrevious.setOnClickListener(v -> playPreviousMusic());
            activityMusicPlayerBinding.playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MusicPlayerActivity.this,"Cannot play this file, it have bug", Toast.LENGTH_LONG).show();
                }
            });
            Toast.makeText(MusicPlayerActivity.this,"Cannot play this file, it have bug", Toast.LENGTH_LONG).show();
        }
    }

    private void playMusic()
    {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            activityMusicPlayerBinding.seekBar.setProgress(0);
            activityMusicPlayerBinding.seekBar.setMax(mediaPlayer.getDuration());
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void playNextMusic()
    {
        if(MyMediaPlayer.currentIndex==songsList.size()-1)
        {
            return;
        }
        else
        {
            MyMediaPlayer.currentIndex +=1;
            mediaPlayer.reset();
            setResourcesWithMusic();
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if(audioSessionId !=-1)
            {
                activityMusicPlayerBinding.visualizer.setAudioSessionId(audioSessionId);
            }
        }
    }

    private void playPreviousMusic()
    {
        if(MyMediaPlayer.currentIndex==0)
        {
            return;
        }
        else
        {
            MyMediaPlayer.currentIndex -=1;
            mediaPlayer.reset();
            setResourcesWithMusic();
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if(audioSessionId !=-1)
            {
                activityMusicPlayerBinding.visualizer.setAudioSessionId(audioSessionId);
            }
        }

    }

    @Override
    protected void onDestroy() {
        activityMusicPlayerBinding.visualizer.release();
        super.onDestroy();
    }

    private void pausePlayMusic()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        else
        {
            mediaPlayer.start();
        }
    }

    @SuppressLint("DefaultLocale")
    public  static String convertToMMSS(String duration)
    {
        long millis = Long.parseLong(duration);
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d",minutes,seconds);
    }
}