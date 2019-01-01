package com.example.user.ast;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ai extends AppCompatActivity {
    private RequestQueue mRQ;
    private TextView Tgas[] = new TextView[6];// SO2 CO O3 PM10 PM2.5 NO2
    private TextView hourgas[] = new TextView[6];// SO2 CO O3 PM10 PM2.5 NO2
    private TextView state[] = new TextView[6];// SO2 CO O3 PM10 PM2.5 NO2
    private TextView observe_t; //觀測站名稱
    private TextView acp_value; // 值
    private TextView acp_state; // 狀態
    private String idname;//當前觀測站名稱
    private int id = -1;//index 觀測站
    private int id2 = -1; //index 小時濃度
    List<Double> aqilist = new ArrayList<>(); //暫存aqi數值
    SharedPreferences HealthRecord;// 存檔用
    SharedPreferences.Editor editor;
    String strgzil2 = new String(""); //句子2
    String strgzil1= new String(""); //句子1
    int prev; //我上一個是從哪個按鍵來的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);
        Tgas[0] = findViewById(R.id.aiiso2);
        Tgas[1] = findViewById(R.id.aiico);
        Tgas[2] = findViewById(R.id.aiio3);
        Tgas[3] = findViewById(R.id.aiipm10);
        Tgas[4] = findViewById(R.id.aiipm2_5);
        Tgas[5] = findViewById(R.id.aiino2);
        observe_t= findViewById(R.id.obs);//觀測站ID
        hourgas[0] = findViewById(R.id.aivso2);
        hourgas[1] = findViewById(R.id.aivco);
        hourgas[2] = findViewById(R.id.aivo3);
        hourgas[3] = findViewById(R.id.aivpm10);
        hourgas[4] = findViewById(R.id.aivpm2_5);
        hourgas[5] = findViewById(R.id.aivno2);
        state[0] = findViewById(R.id.ailso2);
        state[1] = findViewById(R.id.ailco);
        state[2] = findViewById(R.id.ailo3);
        state[3] = findViewById(R.id.ailpm10);
        state[4] = findViewById(R.id.ailpm2_5);
        state[5] = findViewById(R.id.textView33);
        acp_value = findViewById(R.id.aqiv);
        acp_state = findViewById(R.id.aqilevel);
        mRQ = Volley.newRequestQueue(this);
        prev = getIntent().getIntExtra("acp", 0); // 從誰而來 不可能0

        /*拿資料*/
        SharedPreferences saveid = getApplication().getSharedPreferences("ssssid",Context.MODE_PRIVATE);
        idname = saveid.getString("idsave","");

        HealthRecord = getApplication().getSharedPreferences("healthresult", Context.MODE_PRIVATE);
        editor = HealthRecord.edit();

        /*AQI指標*/
        jsonParse();

        /*小時濃度*/
        jsonParse2();


    }

    public void jsonParse(){
        String url = "http://140.136.149.239:9487/recentAQI";
        JsonArrayRequest request  = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0; i<response.length(); ++i) {
                                JSONObject tmp = response.getJSONObject(i); //json物件
                                if(tmp.getString("SiteName").equals(idname)){//觀測站名稱
                                    id = i;
                                    break;
                                }
                            }
                            observe_t.setText("觀測站-"+idname);
                            if(id==-1){//沒有這個觀測站
                                for(int i=0; i<6; ++i){
                                    Tgas[i].setText("維修");
                                    colorSet(0, state[i]);//設等級
                                }
                                acp_value.setText("維修");
                                colorSet(0, acp_state);//設等級
                            }
                            else {
                                JSONObject tmp = response.getJSONObject(id); //json物件
                                if (!tmp.isNull("SO2Ans")) {
                                    String SO2 = tmp.getString("SO2Ans");
                                    int val = Integer.valueOf(SO2);
                                    Tgas[0].setText(SO2);
                                    aqilist.add(Double.valueOf(val));
                                    colorSet(val, state[0]);//設等級
                                    aboutyourbreath(val,2);
                                } else{
                                    Tgas[0].setText("維修");
                                    colorSet(0, state[0]);//設等級
                                }

                                if (!tmp.isNull("COAns")) {
                                    String CO = tmp.getString("COAns");
                                    int val = Integer.valueOf(CO);
                                    aqilist.add(Double.valueOf(val));
                                    Tgas[1].setText(CO);
                                    colorSet(val, state[1]);//設等級
                                } else{
                                    Tgas[1].setText("維修");
                                    colorSet(0, state[1]);//設等級
                                }

                                if (!tmp.isNull("PM10Ans")) {
                                    String PM10 = tmp.getString("PM10Ans");
                                    int val = Integer.valueOf(PM10);
                                    Tgas[3].setText(PM10);
                                    aqilist.add(Double.valueOf(val));
                                    colorSet(val, state[3]);//設等級
                                    aboutyourbreath(val,4);
                                } else{
                                    Tgas[3].setText("維修");
                                    colorSet(0, state[3]);//設等級
                                }

                                if (!tmp.isNull("PM25Ans")) {
                                    String PM25 = tmp.getString("PM25Ans");
                                    int val = Integer.valueOf(PM25);
                                    Tgas[4].setText(PM25);
                                    aqilist.add(Double.valueOf(val));
                                    colorSet(val, state[4]);//設等級
                                    aboutyourbreath(val,1);
                                } else {
                                    Tgas[4].setText("維修");
                                    colorSet(0, state[4]);//設等級
                                }

                                if (!tmp.isNull("NO2Ans")) {
                                    String NO2 = tmp.getString("NO2Ans");
                                    int val = Integer.valueOf(NO2);
                                    Tgas[5].setText(NO2);
                                    aqilist.add(Double.valueOf(val));
                                    colorSet(val, state[5]);//設等級
                                    aboutyourbreath(val,3);
                                } else{
                                    Tgas[5].setText("維修");
                                    colorSet(0, state[5]);//設等級
                                }

                                if (!tmp.isNull("O3Ans")) {
                                    String O3 = tmp.getString("O3Ans");
                                    int val = Integer.valueOf(O3);
                                    Tgas[2].setText(O3);
                                    aqilist.add(Double.valueOf(val));
                                    colorSet(val, state[2]);//設等級
                                    aboutyourbreath(val,5);
                                } else{
                                    Tgas[2].setText("維修");
                                    colorSet(0, state[2]);//設等級
                                }
                                Collections.sort(aqilist); //排序
                                Collections.reverse(aqilist); //由大到小
                                /*aqi設定值*/
                                if(aqilist.size()>1){
                                    acp_value.setText(String.valueOf((int)Math.ceil((aqilist.get(0)+aqilist.get(1))/2)));
                                    colorSet((int)Math.ceil((aqilist.get(0)+aqilist.get(1))/2), acp_state);//設等級
                                }
                                else if(aqilist.size()==1){
                                    acp_value.setText(String.valueOf(aqilist.get(0).intValue()));
                                    colorSet(aqilist.get(0).intValue(), acp_state);//設等級
                                }
                                else{
                                    acp_value.setText("維修");
                                    colorSet(0, acp_state);//設等級
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        mRQ.add(request);
    }

    private void jsonParse2(){//小時濃度

        String url = "http://140.136.149.239:9487/recentAIR";
        JsonArrayRequest request  = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0; i<response.length(); ++i) {
                                JSONObject tmp = response.getJSONObject(i); //json物件
                                if(tmp.getString("SiteName").equals(idname)) {//觀測站名稱
                                    id2 = i;
                                    break;
                                }
                            }
                            if(id2 == -1){//沒有小時濃度資料
                                for(int i=0; i<6; ++i)
                                    hourgas[i].setText("維修");
                            }
                            else {
                                JSONObject tmp = response.getJSONObject(id2); //json物件
                                DecimalFormat df = new DecimalFormat("##.00");
                                /*設定值*/
                                if (!tmp.isNull("SO2")&&tmp.getDouble("SO2")>=0) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("SO2")));
                                    hourgas[0].setText(String.valueOf(dtmp));
                                } else
                                    hourgas[0].setText("維修");

                                if (!tmp.isNull("CO")&&tmp.getDouble("CO")>=0) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("CO")));
                                    hourgas[1].setText(String.valueOf(dtmp));
                                } else
                                    hourgas[1].setText("維修");

                                if (!tmp.isNull("PM10")&&tmp.getDouble("PM10")>=0) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("PM10")));
                                    hourgas[3].setText(String.valueOf(tmp.getDouble("PM10")));
                                } else
                                    hourgas[3].setText("維修");

                                if (!tmp.isNull("PM25")&&tmp.getDouble("PM25")>=0) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("PM25")));
                                    hourgas[4].setText(String.valueOf(dtmp));
                                } else
                                    hourgas[4].setText("維修");

                                if (!tmp.isNull("NO2")&&tmp.getDouble("NO2")>=0) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("NO2")));
                                    hourgas[5].setText(String.valueOf(dtmp));
                                } else
                                    hourgas[5].setText("維修");

                                if (!tmp.isNull("O3")&&tmp.getDouble("O3")>=0) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("O3")));
                                    hourgas[2].setText(String.valueOf(dtmp));
                                } else
                                    hourgas[2].setText("維修");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        mRQ.add(request);
    }
    /*設定嚴重等級*/
    public void colorSet(int cas, TextView mystate){
        switch (cas){
            case 0:
                mystate.setText("維修");
                mystate.setTextColor(getResources().getColor(R.color.gray));
                break;
            case 1:
                mystate.setText(getResources().getString(R.string.strgas_good));
                mystate.setTextColor(getResources().getColor(R.color.g));
                break;
            case 2:
                mystate.setText(getResources().getString(R.string.strgas_normal));
                mystate.setTextColor(getResources().getColor(R.color.y));
                break;
            case 3:
                mystate.setText(getResources().getString(R.string.strgas_notgood));
                mystate.setTextColor(getResources().getColor(R.color.o));
                break;
            case 4:
                mystate.setText(getResources().getString(R.string.strgas_bad));
                mystate.setTextColor(getResources().getColor(R.color.r));
                break;
            case 5:
                mystate.setText(getResources().getString(R.string.strgas_verybad));
                mystate.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
            case 6:
                mystate.setText(getResources().getString(R.string.strgas_god));
                mystate.setTextColor(getResources().getColor(R.color.p));
                break;
            case 7:
                mystate.setText(getResources().getString(R.string.strgas_god));
                mystate.setTextColor(getResources().getColor(R.color.br));
                break;
        }
    }

    public void aboutyourbreath(int num,int who) {//引發疾病


        switch (who) {
            case 1://pm2.5
                if(HealthRecord.getBoolean("checkedHeartDisease",true)==false&&HealthRecord.getBoolean("checkedDVC",true)==false&&HealthRecord.getBoolean("checkedRespiratoryDisease",true)==false&&HealthRecord.getBoolean("checkedConjunctivitis",true)==false&&HealthRecord.getBoolean("checkedAllergicRhinitis",true)==false){
                    if (num ==1) {
                        strgzil1="非常新鮮的空氣，多到戶外走走吧!";
                    } else if (num ==2) {
                        strgzil1="新鮮的空氣，放心到戶外走走吧!";
                    } else if (num ==3) {
                        strgzil1+="正常不正常邊緣的空氣，盡量別待在戶外太久!";
                    } else if(num==4) {
                        strgzil1 = "戴一下口罩吧!這空氣有點髒!";
                    }else if(num==5){
                        strgzil1="別出門了，要不然戴個防毒面具好不?";
                    }else if(num==6){
                        strgzil1="求你了，別出門，防毒面具也救不了你";
                    }else if(num==7){
                        strgzil1="求你了，別出門，防毒面具也救不了你";
                    }
                }
                else{
                    if (num ==1) {
                        strgzil1="非常新鮮的空氣，多到戶外走走吧!";
                    } else if (num ==2) {
                        strgzil1="新鮮的空氣，放心到戶外走走吧!";
                    } else if (num ==3) {
                        strgzil1="正常不正常邊緣的空氣，記得要戴口罩喔!";
                    } else if(num==4) {
                        strgzil1 = "這空氣有點髒!還是別出門了吧!";
                    }else if(num==5){
                        strgzil1="沒防毒面具救別出門了!";
                    }else if(num==6){
                        strgzil1="求你了，別出門，防毒面具也救不了你";
                    }else if(num==7){
                        strgzil1="求你了，別出門，防毒面具也救不了你";
                    }
                }

                break;

            case 2://so2
                if(HealthRecord.getBoolean("checkedRespiratoryDisease",true)==false) {
                    if (num ==1) {
                        strgzil1="多C一點O氣";
                    } else if (num ==2) {
                        strgzil1=" 可以正常出門";
                    } else if (num ==3) {
                        strgzil1="要出門要戴口罩!";
                    } else if(num==4) {
                        strgzil1 = "不建議出門";
                    }else if(num==5){
                        strgzil1="不能出門，出門會造成身體危害";
                    }else if(num==6){
                        strgzil1="絕對不能出門，出門會直接傷害到您的生命";
                    }else if(num==7){
                        strgzil1="絕對不能出門，出門會直接傷害到您的生命";
                    }
                }
                else{
                    if (num ==1) {
                        strgzil1="多C一點O氣";
                    } else if (num ==2) {
                        strgzil1="準備個口罩";
                    } else if (num ==3) {
                        strgzil1="不建議久留外面";
                    } else if(num==4) {
                        strgzil1 = "別出門了吧!";
                    }else if(num==5){
                        strgzil1="外面的世界是很危險的!";
                    }else if(num==6){
                        strgzil1="絕對不能出門，出門會直接傷害到您的生命";
                    }else if(num==7){
                        strgzil1="絕對不能出門，出門會直接傷害到您的生命";
                    }
                }
                break;

            case 3://no2
                if(HealthRecord.getBoolean("checkedRespiratoryDisease",true)==false) {
                    if (num ==1) {
                        strgzil1="完美";
                    } else if (num ==2) {
                        strgzil1="還好";
                    } else if (num ==3) {
                        strgzil1="記得要戴口罩喔!";
                    } else if(num==4) {
                        strgzil1 = "北京";
                    }else if(num==5){
                        strgzil1="北京";
                    }else if(num==6){
                        strgzil1="北京";
                    }else if(num==7){
                        strgzil1="北京";
                    }
                }
                else{
                    if (num ==1) {
                        strgzil1="完美";
                    } else if (num ==2) {
                        strgzil1="還好";
                    } else if (num ==3) {
                        strgzil1="記得要戴口罩喔!";
                    } else if(num==4) {
                        strgzil1 = "北京";
                    }else if(num==5){
                        strgzil1="北京";
                    }else if(num==6){
                        strgzil1="北京";
                    }else if(num==7){
                        strgzil1="北京";
                    }
                }
                break;

            case 4://rm10
                if(HealthRecord.getBoolean("checkedHeartDisease",true)==false&&HealthRecord.getBoolean("checkedDVC",true)==false&&HealthRecord.getBoolean("checkedRespiratoryDisease",true)==false&&HealthRecord.getBoolean("checkedConjunctivitis",true)==false&&HealthRecord.getBoolean("checkedAllergicRhinitis",true)==false) {
                    if (num ==1) {
                        strgzil1="非常新鮮的空氣，多到戶外走走吧!";
                    } else if (num ==2) {
                        strgzil1+="新鮮的空氣，放心到戶外走走吧!";
                    } else if (num ==3) {
                        strgzil1="正常不正常邊緣的空氣，盡量別待在戶外太久!";
                    } else if(num==4) {
                        strgzil1 = "戴一下口罩吧!這空氣有點髒!";
                    }else if(num==5){
                        strgzil1="別出門了，要不然戴個防毒面具好不?";
                    }else if(num==6){
                        strgzil1="求你了，別出門，防毒面具也救不了你";
                    }else if(num==7){
                        strgzil1="求你了，別出門，防毒面具也救不了你";
                    }
                }
                else{
                    if (num ==1) {
                        strgzil1="非常新鮮的空氣，多到戶外走走吧!";
                    } else if (num ==2) {
                        strgzil1="新鮮的空氣，放心到戶外走走吧!";
                    } else if (num ==3) {
                        strgzil1="正常不正常邊緣的空氣，記得要戴口罩喔!";
                    } else if(num==4) {
                        strgzil1 = "這空氣有點髒!還是別出門了吧!";
                    }else if(num==5){
                        strgzil1="沒防毒面具救別出門了!";
                    }else if(num==6){
                        strgzil1="求你了，別出門，防毒面具也救不了你";
                    }else if(num==7){
                        strgzil1="求你了，別出門，防毒面具也救不了你";
                    }
                }
                break;

            case 5://o3
                if(HealthRecord.getBoolean("checkedRespiratoryDisease",true)==false) {
                    if (num ==1) {
                        strgzil1="完美";
                    } else if (num ==2) {
                        strgzil1="還好";
                    } else if (num ==3) {
                        strgzil1="記得要戴口罩喔!";
                    } else if(num==4) {
                        strgzil1 = "北京";
                    }else if(num==5){
                        strgzil1="北京";
                    }else if(num==6){
                        strgzil1="北京";
                    }else if(num==7){
                        strgzil1="北京";
                    }
                }
                else{
                    if (num ==1) {
                        strgzil1="完美";
                    } else if (num ==2) {
                        strgzil1="還好";
                    } else if (num ==3) {
                        strgzil1="記得要戴口罩喔!";
                    } else if(num==4) {
                        strgzil1 = "北京";
                    }else if(num==5){
                        strgzil1="北京";
                    }else if(num==6){
                        strgzil1="北京";
                    }else if(num==7){
                        strgzil1="北京";
                    }
                }
                break;
        }

        boolean checkmul=false;

        if (who == 1 && num > 2 || who == 2 && num > 2 || who == 3 && num > 2 || who == 4 && num > 2 || who == 5 && num > 2)
        {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="肺功能下降";
        }
        if (who == 1 && num > 2 || who == 2 && num > 2 || who == 3 && num > 2  || who == 5 && num > 2) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="咳嗽";
        }
        if (who == 1 && num > 2|| who == 5 && num > 2) {
            if(checkmul)strgzil2+="、";
            checkmul=true;
            strgzil2+="肺癌、血癌、自律神經失調";
        }
        if ( who == 2 && num > 2) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="呼吸困難、呼吸道阻塞";
        }
        if ( who == 5 && num > 2) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="加速老化、皮膚疾病";
        }
        if (who == 3 && num > 2|| who == 4 && num > 2 ) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="頭痛";
        }
        if (who == 4 && num > 2 ) {
            if(checkmul) strgzil2+="、";
            strgzil2+="噁心、虛弱";
        }

    }
    @Override
    public void onBackPressed() {//上一頁的功能
        super.onBackPressed();

        /*句子1*/
        if(!HealthRecord.getString("gzil1","").equals(""))//不是空的就刪除
            editor.remove("gzil1").commit();
        editor.putString("gzil1", strgzil1).commit();

        /*句子2*/
        if(!HealthRecord.getString("gzil2","").equals(""))
            editor.remove("gzil2").commit();
        if(!strgzil2.isEmpty()){
            strgzil2 = "可能引起:\n\t\t\t" + strgzil2;
        }
        editor.putString("gzil2",strgzil2).commit();

        /*acp等級*/
        if(prev == 1){ //從ais 來
            if(HealthRecord.getInt("acp",-1) != -1)//不是空的就刪除
                editor.remove("acp").commit();
            if(acp_value.getText().toString().equals("維修")) //維修狀態
                editor.putInt("acp", 0).commit();
            else{
                editor.putInt("acp", Integer.valueOf(acp_value.getText().toString())).commit();
            }
        }
        else{ //從am來
            if(HealthRecord.getInt("acp2",-1) != -1)//不是空的就刪除
                editor.remove("acp2").commit();
            if(acp_value.getText().toString().equals("維修")) //維修狀態
                editor.putInt("acp2", 0).commit();
            else{
                editor.putInt("acp2", Integer.valueOf(acp_value.getText().toString())).commit();
            }
        }
    }
}
