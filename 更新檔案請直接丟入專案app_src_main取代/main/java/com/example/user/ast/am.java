package com.example.user.ast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class am extends AppCompatActivity {
    TextView name;  //當前測站名字
    Button caredata;    //詳細資料
    private String place_name[] = {
            "富貴角", "陽明", "萬里", "淡水", "基隆", "士林", "林口", "三重", "菜寮", "汐止", "大同", "中山", "大園", "松山",
            "萬華", "新莊", "觀音", "古亭", "永和", "板橋", "桃園", "土城", "新店", "平鎮", "中壢", "龍潭", "湖口", "新竹",
            "頭份", "苗栗", "三義", "豐原", "沙鹿", "西屯", "忠明", "線西", "大里", "彰化", "埔里", "二林", "南投", "竹山",
            "麥寮", "臺西", "斗六", "新港", "圤子", "嘉義", "新營", "善化", "安南", "臺南", "美濃", "橋頭", "楠梓", "仁武",
            "左營", "屏東", "前金", "鳳山", "復興", "前鎮", "小港", "大寮", "潮州", "林園", "恆春", "宜蘭", "冬山", "花蓮",
            "關山", "臺東", "馬祖", "金門", "馬公", "臺南", "彰化", "崙背", "屏東"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_am);
        /*詳細資料按鈕*/
        caredata = findViewById(R.id.obs_get2);
        caredata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        name = findViewById(R.id.area2);
        /*讀檔案*/
        SharedPreferences tmp = getApplication().getSharedPreferences("area_data",Context.MODE_PRIVATE);
        name.setText(tmp.getString("area_save", "臺北市中正區"));
    }
    private void open_activity(){//開啟觀測站資訊頁面
        Intent intent;
        intent = new Intent();
        intent.setClass(am.this,ai.class);
        startActivity(intent);
    }
    private void save_data(int id){//存入當前地區
        SharedPreferences saveid = getApplication().getSharedPreferences("ssssid", Context.MODE_PRIVATE);
        saveid.edit().clear().commit();
        saveid.edit().putString("idsave", place_name[id]).apply();
    }
}
