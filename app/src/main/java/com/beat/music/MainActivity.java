package com.beat.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import com.beat.music.databinding.ActivityMainBinding;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    ArrayList<AudioModel> songList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF0000")));

        if(!checkStoragePermission())
        {
            requestStoragePermission();
            return;
        }
        else
        {
            String[] projection = {
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION
            };

            String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";

            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
            while (cursor.moveToNext())
            {
                AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
                if(new File(songData.getPath()).exists())
                {
                    songList.add(songData);
                }
                if(songList.size()==0)
                {
                    activityMainBinding.noSongFound.setVisibility(View.VISIBLE);
                }
                else
                {
                    // Recycler View
                    activityMainBinding.recyclerId.setLayoutManager(new LinearLayoutManager(this));
                    activityMainBinding.recyclerId.setAdapter(new MusicListAdapter(songList,getApplicationContext()));
                }
            }
        }
    }

    boolean checkStoragePermission()
    {
        int resultStorage = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int resultAudio = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);
        if(resultStorage == PackageManager.PERMISSION_GRANTED && resultAudio == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    void requestStoragePermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.RECORD_AUDIO))
        {
            Toast.makeText(MainActivity.this, "Please allow a storage permission and Audio Record Permission", Toast.LENGTH_SHORT).show();
        }else
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},173);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityMainBinding.recyclerId.setAdapter(new MusicListAdapter(songList,getApplicationContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}