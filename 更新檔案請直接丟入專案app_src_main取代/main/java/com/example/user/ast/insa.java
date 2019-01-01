package com.example.user.ast;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.net.Uri;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.transform.Result;

public class insa extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> list;//表單陣列
    ListView listView;//頁面
    SearchView searchView;//搜尋用
    ArrayAdapter<String> arrayAdapter;//連接資料器
    MediaPlayer mediaPlayer = new MediaPlayer();//媒體播放器
    ArrayList<String> currentLocation = new ArrayList<String>();//當前音樂位置
    ArrayList<String> currentTitle = new ArrayList<String>();//音樂名稱陣列
    SharedPreferences sp;// 存檔用
    int tmppos = -1; //暫存音樂位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insa);
        searchView = findViewById(R.id.music_search);
        sp = getApplication().getSharedPreferences("settingsave",Context.MODE_PRIVATE);//讀檔案用
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //提交結果
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                /*過濾Adapter的內容*/
                Filter filter = arrayAdapter.getFilter();
                filter.filter(newText);
                return false;
            }
        });
        searchView.setIconifiedByDefault(false); //是否要點選搜尋圖示後再打開輸入框
        searchView.setFocusable(false);
        searchView.requestFocusFromTouch();      //要點選後才會開啟鍵盤輸入
        searchView.setSubmitButtonEnabled(false);//輸入框後是否要加上送出的按鈕
        searchView.setQueryHint("請輸入音樂名稱"); //輸入框沒有值時要顯示的提示文字
        listView = findViewById(R.id.insa_music_list);
        list = new ArrayList<>();
        getMusic();
        arrayAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);//連接器設值
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //打開音樂
                try {
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.reset();
                        com(((TextView)view).getText().toString());
                        mediaPlayer.setDataSource(currentLocation.get(tmppos));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                    else {
                        com(((TextView)view).getText().toString());
                        mediaPlayer.setDataSource(currentLocation.get(tmppos));
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
        /*拿手機資源*/
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int id = 0;
            do {
                currentTitle.add(songCursor.getString(songTitle)) ;
                String currentArtist = songCursor.getString(songArtist);
                currentLocation.add(songCursor.getString(songLocation));
                list.add("名稱:    " + currentTitle.get(id) + "\n" +
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
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {//上一頁的功能
        if(mediaPlayer.isPlaying() || tmppos != -1){
            mediaPlayer.release();
            if(!sp.getString("music_rw","").equals("")) //不是空的
                sp.edit().remove("music_rw").commit();
            sp.edit().putString("music_rw", currentLocation.get(tmppos)).commit();

            Toast.makeText(insa.this, "選擇成功:" + "\n" + "你選的是:"+ currentLocation.get(tmppos),Toast.LENGTH_SHORT).show();
        }
        else{
            if(!sp.getString("music_rw","").equals(""))
                sp.edit().remove("music_rw").commit();
            Toast.makeText(insa.this, "沒選擇鈴聲~",Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();
    }
    /*list比較*/
    public int com(String key){
        for(int i=0; i<list.size(); ++i){
            if(key.equals(list.get(i))){
                tmppos = i;// 得到正確位置
                return i;
            }
        }
        return -1;
    }
}
