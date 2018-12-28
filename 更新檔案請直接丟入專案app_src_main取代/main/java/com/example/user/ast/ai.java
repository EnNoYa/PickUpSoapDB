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

import android.widget.TextView;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ai extends AppCompatActivity {
    private RequestQueue mRQ;
    private TextView Tgas[] = new TextView[6];// SO2 CO O3 PM10 PM2.5 NO2
    private TextView hourgas[] = new TextView[6];// SO2 CO O3 PM10 PM2.5 NO2
    private TextView state[] = new TextView[6];// SO2 CO O3 PM10 PM2.5 NO2
    private TextView observe_t; //觀測站名稱
    private String sarr[] = new String[79]; //紀錄觀測站順序(暫存)
    private String idname;//當前觀測站名稱
    private int id = -1;//index 觀測站
    private int id2 = -1; //index 小時濃度
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
        mRQ = Volley.newRequestQueue(this);

        /*拿資料*/
        SharedPreferences saveid = getApplication().getSharedPreferences("ssssid",Context.MODE_PRIVATE);
        idname = saveid.getString("idsave","");

        /*AQI指標*/
        jsonParse();

        /*小時濃度*/
        jsonParse2();
    }

    private void jsonParse(){
        String url = "http://140.136.149.239:9487/recentAQI";
        JsonArrayRequest request  = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0; i<response.length(); ++i) {
                                JSONObject tmp = response.getJSONObject(i); //json物件
                                sarr[i] = tmp.getString("SiteName");//觀測站名稱
                            }
                            for(int i=0; i<sarr.length; ++i){
                                if(sarr[i].equals(idname)){
                                    id =i;
                                    observe_t.setText("觀測站-"+idname);
                                    break;
                                }
                            }
                            if(id==-1){//沒有這個觀測站
                                for(int i=0; i<6; ++i){
                                    Tgas[i].setText("維修");
                                    colorSet(0, state[i]);//設等級
                                }
                            }
                            else {
                                JSONObject tmp = response.getJSONObject(id); //json物件
                                if (!tmp.isNull("SO2Ans")) {
                                    String SO2 = tmp.getString("SO2Ans");
                                    Tgas[0].setText(SO2);
                                    colorSet(Integer.valueOf(SO2), state[0]);//設等級
                                } else{
                                    Tgas[0].setText("維修");
                                    colorSet(0, state[0]);//設等級
                                }

                                if (!tmp.isNull("COAns")) {
                                    String CO = tmp.getString("COAns");
                                    Tgas[1].setText(CO);
                                    colorSet(Integer.valueOf(CO), state[1]);//設等級
                                } else{
                                    Tgas[1].setText("維修");
                                    colorSet(0, state[1]);//設等級
                                }

                                if (!tmp.isNull("PM10Ans")) {
                                    String PM10 = tmp.getString("PM10Ans");
                                    Tgas[3].setText(PM10);
                                    colorSet(Integer.valueOf(PM10), state[3]);//設等級
                                } else{
                                    Tgas[3].setText("維修");
                                    colorSet(0, state[3]);//設等級
                                }

                                if (!tmp.isNull("PM25Ans")) {
                                    String PM25 = tmp.getString("PM25Ans");
                                    Tgas[4].setText(PM25);
                                    colorSet(Integer.valueOf(PM25), state[4]);//設等級
                                } else {
                                    Tgas[4].setText("維修");
                                    colorSet(0, state[4]);//設等級
                                }

                                if (!tmp.isNull("NO2Ans")) {
                                    String NO2 = tmp.getString("NO2Ans");
                                    Tgas[5].setText(NO2);
                                    colorSet(Integer.valueOf(NO2), state[5]);//設等級
                                } else{
                                    Tgas[5].setText("維修");
                                    colorSet(0, state[5]);//設等級
                                }

                                if (!tmp.isNull("O3Ans")) {
                                    String O3 = tmp.getString("O3Ans");
                                    Tgas[2].setText(O3);
                                    colorSet(Integer.valueOf(O3), state[2]);//設等級
                                } else{
                                    Tgas[2].setText("維修");
                                    colorSet(0, state[2]);//設等級
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
                                sarr[i] = tmp.getString("SiteName");//觀測站名稱
                            }
                            for(int i=0; i<sarr.length; ++i){
                                if(sarr[i].equals(idname)){
                                    id2 =i;
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
                                if (!tmp.isNull("SO2")) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("SO2")));
                                    hourgas[0].setText(String.valueOf(dtmp));
                                } else
                                    hourgas[0].setText("維修");

                                if (!tmp.isNull("CO")) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("CO")));
                                    hourgas[1].setText(String.valueOf(dtmp));
                                } else
                                    hourgas[1].setText("維修");

                                if (!tmp.isNull("PM10")) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("PM10")));
                                    hourgas[3].setText(String.valueOf(tmp.getDouble("PM10")));
                                } else
                                    hourgas[3].setText("維修");

                                if (!tmp.isNull("PM25")) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("PM25")));
                                    hourgas[4].setText(String.valueOf(dtmp));
                                } else
                                    hourgas[4].setText("維修");

                                if (!tmp.isNull("NO2")) {
                                    Double dtmp = Double.parseDouble(df.format(tmp.getDouble("NO2")));
                                    hourgas[5].setText(String.valueOf(dtmp));
                                } else
                                    hourgas[5].setText("維修");

                                if (!tmp.isNull("O3")) {
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

}
