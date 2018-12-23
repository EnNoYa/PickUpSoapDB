package com.example.user.ast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class SettingsA extends AppCompatActivity{

    Intent intent;
    private Spinner area, smallarea; //下拉選單 地區 小地區
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
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SettingsA.this,
                R.array.arr_city, android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        area.setAdapter(adapter);
        area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {//選擇事件
                Resources res = getResources();//拿資源
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
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}