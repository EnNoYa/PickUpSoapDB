package com.example.user.ast;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.net.Uri;
import android.widget.Toast;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

public class insa extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> list;
    ListView listView;//頁面
    ArrayAdapter<String> arrayAdapter;//連接資料器
    MediaPlayer mediaPlayer = new MediaPlayer();
    ArrayList<String> currentLocation = new ArrayList<String>();//當前音樂位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insa);
        if(ContextCompat.checkSelfPermission(insa.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(insa.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(insa.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else{
                ActivityCompat.requestPermissions(insa.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }
        else{
            dostuff();
        }
    }
    public void dostuff(){
        listView = findViewById(R.id.insa_music_list);
        list = new ArrayList<>();
        getMusic();
        arrayAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //打開音樂
                try {
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(currentLocation.get(position));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                    else {
                    mediaPlayer.setDataSource(currentLocation.get(position));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    }
                } catch (IOException e) {
                    Toast.makeText(insa.this, "失敗", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int id = 0;
            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                currentLocation.add(songCursor.getString(songLocation));
                list.add("名稱:    " + currentTitle + "\n" +
                        "作者:    " + currentArtist + "\n" +
                "位置:  " + currentLocation.get(id++));
            }while (songCursor.moveToNext());
        }
    }
    @Override
    public void onRequestPermissionsResult(int RequestCode, String[] permissions, int[] grantResults){
            switch (RequestCode){
                case MY_PERMISSION_REQUEST:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        if(ContextCompat.checkSelfPermission(insa.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(insa.this, "授權成功", Toast.LENGTH_SHORT).show();
                            dostuff();
                        }
                        else{
                            Toast.makeText(insa.this, "授權失敗", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    break;
            }
    }
}
