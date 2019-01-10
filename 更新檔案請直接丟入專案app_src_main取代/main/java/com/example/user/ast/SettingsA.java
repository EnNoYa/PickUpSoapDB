package com.example.user.ast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.content.SharedPreferences;
import android.widget.Toast;

public class SettingsA extends AppCompatActivity{

    Intent intent;
    Resources res; //資源檔
    private Spinner area, smallarea; //下拉選單 地區 小地區
    String name; //全名
    ArrayAdapter<CharSequence> adapter;// 連接資料器
    boolean ok = false;//檢查兩次選擇是否一樣
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        intent = new Intent();

        final Bundle bdn = new Bundle();
        Button bns = (Button)findViewById(R.id.bns);
        bns.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(SettingsA.this, ns.class);
                bdn.putInt("btnid", R.id.bns);
                intent.putExtras(bdn);
                startActivity(intent);
            }
        });

        final Bundle bdm = new Bundle();
        Button bnm = (Button)findViewById(R.id.bmrs);
        bnm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(SettingsA.this, mrsa.class);
                bdm.putInt("btnid", R.id.bmrs);
                intent.putExtras(bdm);
                startActivity(intent);
            }
        });
        /*下拉式選單*/
        area = findViewById(R.id.sp_area);
        smallarea = findViewById(R.id.sp_smallarea);
        res = getResources();//拿資源
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(SettingsA.this,
                R.array.arr_city, android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        area.setAdapter(adapter);
        /*讀本地資料*/
        load_local_area1();
        area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {//選擇事件
                ArrayAdapter<String> list;
                switch (pos){
                    case 0:
                        list = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Taipei));
                        smallarea.setAdapter(list);
                        break;
                    case 1:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_NewTaipei));
                        smallarea.setAdapter(list);
                        break;
                    case 2:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Taoyuan));
                        smallarea.setAdapter(list);
                        break;
                    case 3:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Taichung));
                        smallarea.setAdapter(list);
                        break;
                    case 4:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Tainan));
                        smallarea.setAdapter(list);
                        break;
                    case 5:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Kaohsiung));
                        smallarea.setAdapter(list);
                        break;
                    case 6:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Keelung));
                        smallarea.setAdapter(list);
                        break;
                    case 7:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Hsinchu));
                        smallarea.setAdapter(list);
                        break;
                    case 8:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Chiayi));
                        smallarea.setAdapter(list);
                        break;
                    case 9:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_HsinchuLine));
                        smallarea.setAdapter(list);
                        break;
                    case 10:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Miaoli));
                        smallarea.setAdapter(list);
                        break;
                    case 11:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Changhua));
                        smallarea.setAdapter(list);
                        break;
                    case 12:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Nantou));
                        smallarea.setAdapter(list);
                        break;
                    case 13:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Yunlin));
                        smallarea.setAdapter(list);
                        break;
                    case 14:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_ChiayiLine));
                        smallarea.setAdapter(list);
                        break;
                    case 15:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Pingtung));
                        smallarea.setAdapter(list);
                        break;
                    case 16:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Yilan));
                        smallarea.setAdapter(list);
                        break;
                    case 17:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Hualien));
                        smallarea.setAdapter(list);
                        break;
                    case 18:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Taitung));
                        smallarea.setAdapter(list);
                        break;
                    case 19:
                        list  = new ArrayAdapter<>(SettingsA.this, android.R.layout.simple_spinner_dropdown_item,
                                res.getStringArray(R.array.arr_Wuhu));
                        smallarea.setAdapter(list);
                        break;
                    default:
                        break;
                }
                /*讀小地區*/
                load_local_area2();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*小地區設定*/
        smallarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name = area.getSelectedItem().toString()+smallarea.getSelectedItem().toString();//存入
                /*存檔*/
                save_area(name);//存入名稱
                save_local_area();//存本地
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
    private void save_area(String str){//存入當前選擇地區
        SharedPreferences savearea = getApplication().getSharedPreferences("area_data", Context.MODE_PRIVATE);
        savearea.edit().clear().commit();
        savearea.edit().putString("area_save", str).apply();
    }
    private void save_local_area(){//存入本地當前選擇地區
        int areaid = area.getSelectedItemPosition();
        int smallid = smallarea.getSelectedItemPosition();
        SharedPreferences savearea = getApplication().getSharedPreferences("local_area_data", Context.MODE_PRIVATE);
        savearea.edit().clear().commit();
        savearea.edit().putInt("local_area_data1",areaid).commit();
        savearea.edit().putInt("local_area_data2",smallid).commit();
    }
    private void load_local_area1(){//讀本地當前選擇地區
        SharedPreferences savearea = getApplication().getSharedPreferences("local_area_data", Context.MODE_PRIVATE);
        int areaid;
        areaid = savearea.getInt("local_area_data1",0);
        adapter.notifyDataSetChanged();       //通知spinner刷新數據
        area.setSelection(areaid);
    }
    private void load_local_area2(){//讀本地當前選擇小地區
        SharedPreferences savearea = getApplication().getSharedPreferences("local_area_data", Context.MODE_PRIVATE);
        int smallid;
        smallid = savearea.getInt("local_area_data2",0);
        if(area.getSelectedItemPosition()==savearea.getInt("local_area_data1",0))
            ok=true;
        else
            ok=false;
        if(ok){
            adapter.notifyDataSetChanged();       //通知spinner刷新數據
            smallarea.setSelection(smallid);
        }
    }
}