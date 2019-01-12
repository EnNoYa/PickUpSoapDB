package com.example.user.ast;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/*抓狀態之service*/
public class MyService extends Service {

    private RequestQueue mRQ;
    private String idname;//當前觀測站名稱
    private int id = -1;//index 觀測站
    SharedPreferences HealthRecord;// 存檔用
    SharedPreferences.Editor editor;
    int max_acp = 0, whomax = -1; // 最大acp ， 跟誰最大
    String strgzil2; //句子2
    String strgzil1; //句子1

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        mRQ = Volley.newRequestQueue(this);
        /*拿資料*/
        SharedPreferences saveid = getApplication().getSharedPreferences("ssssid",Context.MODE_PRIVATE);
        idname = saveid.getString("idsave","");
        HealthRecord = getApplication().getSharedPreferences("healthresult", Context.MODE_PRIVATE);
        editor = HealthRecord.edit();

        /*清空句子*/
        strgzil2 = ""; //句子2
        strgzil1 = ""; //句子1

        Log.d("shit","開始");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid){ //內容、額外資訊、啟動編號
        Log.d("shit","執行指令");
        /*AQI指標*/
        jsonParse();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("shit","結束");
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
                                    id =i;
                                    break;
                                }
                            }
                            max_acp = 0;
                            whomax = -1;
                            int primecheck = 0; //質數選擇空氣

                            if(id==-1){//沒有這個觀測站
                                editor.putInt("acp2", 0).commit();
                                Log.d("shit","數值維修");
                            }
                            else {

                                JSONObject tmp = response.getJSONObject(id); //json物件
                                if (!tmp.isNull("SO2Ans")) {
                                    String SO2 = tmp.getString("SO2Ans");
                                    int val = Integer.valueOf(SO2);
                                    if(val >= max_acp){
                                        max_acp = val;
                                        whomax = 2;
                                        primecheck |= 4;
                                    }
                                }

                                if (!tmp.isNull("COAns")) {
                                    String CO = tmp.getString("COAns");
                                    int val = Integer.valueOf(CO);
                                    if(val >= max_acp){
                                        max_acp = val;
                                        whomax = 0;
                                        primecheck |= 1;
                                    }
                                }

                                if (!tmp.isNull("PM10Ans")) {
                                    String PM10 = tmp.getString("PM10Ans");
                                    int val = Integer.valueOf(PM10);
                                    if(val >= max_acp){
                                        max_acp = val;
                                        whomax = 4;
                                        primecheck |= 16;
                                    }
                                }

                                if (!tmp.isNull("PM25Ans")) {
                                    String PM25 = tmp.getString("PM25Ans");
                                    int val = Integer.valueOf(PM25);
                                    if(val >= max_acp){
                                        max_acp = val;
                                        whomax = 1;
                                        primecheck |= 2;
                                    }
                                }

                                if (!tmp.isNull("NO2Ans")) {
                                    String NO2 = tmp.getString("NO2Ans");
                                    int val = Integer.valueOf(NO2);
                                    if(val >= max_acp){
                                        max_acp = val;
                                        whomax = 3;
                                        primecheck |= 8;
                                    }
                                }

                                if (!tmp.isNull("O3Ans")) {
                                    String O3 = tmp.getString("O3Ans");
                                    int val = Integer.valueOf(O3);
                                    if(val >= max_acp){
                                        max_acp = val;
                                        whomax=5;
                                        primecheck |= 32;
                                    }
                                }
                                /*acp等級*/
                                if(HealthRecord.getInt("acp2",-1) != -1)//不是空的就刪除
                                    editor.remove("acp2").commit();
                                if(max_acp != 0){
                                    editor.putInt("acp2", max_acp).commit();
                                    Log.d("shit","數值"+String.valueOf(max_acp));
                                }
                                else{
                                    editor.putInt("acp2", 0).commit();
                                    Log.d("shit","數值維修");
                                }
                            }

                            aboutyourbreath(max_acp, whomax, primecheck);

                            /*句子1*/
                            if(!HealthRecord.getString("gzil1","").equals(""))//不是空的就刪除
                                editor.remove("gzil1").commit();
                            editor.putString("gzil1", strgzil1).commit();

                            /*句子2*/
                            if(!HealthRecord.getString("gzil2","").equals(""))
                                editor.remove("gzil2").commit();
                            if(!strgzil2.isEmpty()){
                                strgzil2 = "可能引起:　" + strgzil2;
                            }
                            editor.putString("gzil2",strgzil2).commit();

                            sendBroadcast(new Intent("com.example.user.ast.task"));
                            Log.d("shit","完成+廣播");


                            stopSelf(); //停止自己
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

    public void aboutyourbreath(int num, int who, int gas_select) {//引發疾病

        if(who==-1){
            strgzil1 ="維修中哦~~";
            who=0;
        }
        if(HealthRecord.getBoolean("checkedHeartDisease",true)==false&&HealthRecord.getBoolean("checkedDVC",true)==false&&HealthRecord.getBoolean("checkedRespiratoryDisease",true)==false&&HealthRecord.getBoolean("checkedConjunctivitis",true)==false&&HealthRecord.getBoolean("checkedAllergicRhinitis",true)==false||who==0){
            if (num ==1) {
                strgzil1 ="非常新鮮的空氣，多到戶外走走吧!";
            } else if (num ==2) {
                strgzil1 ="新鮮的空氣，放心到戶外走走吧!";
            } else if (num ==3) {
                strgzil1 ="正常不正常邊緣的空氣，盡量別待在戶外太久!";
            } else if(num==4) {
                strgzil1 = "戴一下口罩吧!這空氣有點髒!";
            }else if(num==5){
                strgzil1 ="別出門了，要不然戴個防毒面具好不?";
            }else if(num==6){
                strgzil1 ="求您了，別出門，防毒面具也救不了您";
            }else if(num==7){
                strgzil1 ="求您了，別出門，生命是很寶貴的!";
            }
        }
        else{
            if(HealthRecord.getBoolean("checkedHeartDisease",true)==false&&HealthRecord.getBoolean("checkedDVC",true)==false&&HealthRecord.getBoolean("checkedRespiratoryDisease",true)==true&&HealthRecord.getBoolean("checkedConjunctivitis",true)==false&&HealthRecord.getBoolean("checkedAllergicRhinitis",true)==false){
                if (num ==1) {
                    strgzil1 ="非常新鮮的空氣，多到戶外走走吧!";
                } else if (num ==2) {
                    strgzil1 ="新鮮的空氣，放心到戶外走走吧!";
                } else if (num ==3) {
                    strgzil1 ="正常不正常邊緣的空氣，記得要戴口罩喔!";
                } else if(num==4) {
                    strgzil1  = "這空氣有點髒!還是別出門了吧!";
                }else if(num==5){
                    strgzil1 ="沒防毒面具救別出門了!";
                }else if(num==6){
                    strgzil1 ="求您了，別出門，防毒面具也救不了您";
                }else if(num==7){
                    strgzil1 ="求您了，別出門，生命是很寶貴的!";
                }
            }
            else{
                if (num ==1) {
                    strgzil1 ="多C一點O氣";
                } else if (num ==2) {
                    strgzil1 =" 可以正常出門";
                } else if (num ==3) {
                    strgzil1 ="要出門要戴口罩!";
                } else if(num==4) {
                    strgzil1 = "不建議出門";
                }else if(num==5){
                    strgzil1 ="不能出門，出門會造成身體危害";
                }else if(num==6){
                    strgzil1 ="絕對不能出門，出門會直接傷害到您的生命";
                }else if(num==7){
                    strgzil1 ="絕對不能出門，外面應該是世界末日了!";
                }
            }
        }

        boolean checkmul=false;

        if ( (gas_select & 2) == 2 && num > 2 || (gas_select & 4) == 4 && num > 2 || (gas_select & 8) == 8 && num > 2 || (gas_select & 16) == 16 && num > 2 || (gas_select & 32) == 32 && num > 2)
        {
            checkmul=true;
            strgzil2+="肺功能下降";
        }
        if ( (gas_select & 2) == 2 && num > 2 || (gas_select & 4) == 4 && num > 2 || (gas_select & 8) == 8 && num > 2  || (gas_select & 32) == 32 && num > 2) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="咳嗽";
        }
        if ( (gas_select & 2)==2 && num > 2|| (gas_select & 32) == 32 && num > 2) {
            if(checkmul)strgzil2+="、";
            checkmul=true;
            strgzil2+="肺癌、血癌、自律神經失調";
        }
        if ( (gas_select & 4) == 4 && num > 2) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="呼吸困難、呼吸道阻塞";
        }
        if ( (gas_select & 32) == 32 && num > 2) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="加速老化、皮膚疾病";
        }
        if ( (gas_select & 8) == 8 && num > 2|| (gas_select & 16) == 16 && num > 2 ) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="頭痛";
        }
        if ( (gas_select & 16) == 16 && num > 2 ) {
            if(checkmul) strgzil2+="、";
            strgzil2+="噁心、虛弱";
        }
    }
}