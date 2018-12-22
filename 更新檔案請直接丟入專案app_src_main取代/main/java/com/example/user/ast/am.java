package com.example.user.ast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CircleOptions;


public class am extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener{
    private GoogleMap mMap;
    static final int MIN_TIME = 5000; //位置更新條件：5000 毫秒
    static final float MIN_DIST = 10;   //位置更新條件：10 公尺
    LocationManager mgr;    // 定位管理員
    LatLng currPoint;
    boolean isGPSEnabled;      //GPS定位是否可用
    boolean isNetworkEnabled;  //網路定位是否可用
    private int M = 79;//最大值
    public int nowid = 0;//當前觀測站編號
    private Marker Marr[] = new Marker[M];//標記
    private int bag[] = new int[M];//收納鎖定
    private int bagsize = 0; //背包大小
    private Button bgetdata; //抓當前觀測站資訊
    private Button select_id; //選擇你要查看的觀測站
    private Button recover_id; //解除鎖定
    private Boolean onoff = false; //收納鎖定按鈕是否有按下去
    /*stack運作*/
    private int arr[]=new int[80];
    private int stacki = 0;
    public boolean isEmpty(){
        return stacki==0;
    }
    public void push(int x){
        arr[stacki++]=x;
    }
    public void pop(){
        stacki--;
    }
    public int top(){
        return arr[stacki-1];
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_am);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//設置返回
        // 取得系統服務的LocationManager物件
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        /*地圖預設*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bgetdata = (Button) findViewById(R.id.getdata);
        bgetdata.setOnClickListener(new View.OnClickListener(){//按鈕事件
            @Override
            public void onClick(View v) {
                open_activity(); //開啟觀測站
            }
        });
        select_id = findViewById(R.id.obs_delete);
        select_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onoff=!onoff;
                showOnoff();
            }
        });
        recover_id = findViewById(R.id.obs_recover);
        recover_id.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                for(int i=0; i<M; ++i)
                    Marr[i].setVisible(true);
                reset_save();
            }
        });
        /*讀檔案*/
        SharedPreferences saveid = getApplication().getSharedPreferences("baglist", Context.MODE_PRIVATE);
        for(int i=0; i <M; ++i){
            String tmp= String.valueOf(i);
            bag[i]=saveid.getInt("baglist"+tmp,0);
        }
        /*背包大小讀檔案*/
        saveid = getApplication().getSharedPreferences("baglist", Context.MODE_PRIVATE);
        bagsize=saveid.getInt("bagsize",0);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//設定地圖類別
        for(int i=0; i<M; ++i){
            Marr[i]=mMap.addMarker(new MarkerOptions().position(LLplace[i]).title(place_name[i]));
            if(bag[i]==1)
                Marr[i].setVisible(false);
        }
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);//開啟按鈕事件
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);//開啟點點事件
        checkPermission();//檢查權限
        mMap.getUiSettings().setZoomControlsEnabled(true);//控制放大縮小
        mMap.setMinZoomPreference(8);
        mMap.setMaxZoomPreference(15);
        mMap.setOnMarkerClickListener(this);//標記點擊事件
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableLocationUpdates(true);
    }
    @Override
    protected void onPause() {
        super.onPause();
        enableLocationUpdates(false);    //關閉定位更新功能
    }
    @Override
    public void onLocationChanged(@NonNull Location location) { // 位置變更事件
        currPoint = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(this, "目前位置", Toast.LENGTH_LONG).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currPoint));
        nowid = shortest_place(currPoint);
        save_data(nowid);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override
    public void onProviderEnabled(String provider) { }
    @Override
    public void onProviderDisabled(String provider) { }
    //檢查若尚未授權, 則向使用者要求定位權限
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        }
        else if( mMap !=null){
            mMap.setMyLocationEnabled(true); //gps on
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 200){
            if (grantResults.length >= 1 &&
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {  // 使用者不允許權限
                Toast.makeText(this, "程式需要定位權限才能運作", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    //開啟或關閉定位更新功能
    private void enableLocationUpdates(boolean isTurnOn) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {  // 使用者已經允許定位權限
            if (isTurnOn) {
                //檢查 GPS 與網路定位是否可用
                isGPSEnabled = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!isGPSEnabled && !isNetworkEnabled) {
                    // 無提供者, 顯示提示訊息
                    Toast.makeText(this, "請確認已開啟定位功能!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "取得定位資訊中...", Toast.LENGTH_LONG).show();
                    if (isGPSEnabled)
                        mgr.requestLocationUpdates(   //向 GPS 定位提供者註冊位置事件監聽器
                                LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, this);
                    if (isNetworkEnabled)
                        mgr.requestLocationUpdates(   //向網路定位提供者註冊位置事件監聽器
                                LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST,  this);
                }
            }
            else {
                mgr.removeUpdates(this);    //停止監聽位置事件
            }
        }
    }
    /*按鈕事件*/
    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    return false;
                }
            };
    /*點點事件*/
    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);

                    mMap.addCircle(circleOptions);
                }
            };
    /*觀測站位置資訊*/
    public LatLng LLplace[] = {
                new LatLng(25.29743611,121.537975),
                new LatLng(25.18272222,121.5295833),
                new LatLng(25.17966667,121.6898806),
                new LatLng(25.1645,121.4492389),
                new LatLng(25.12916667,121.7600556),
                new LatLng(25.10591667,121.5145),
                new LatLng(25.07857,121.3657028),
                new LatLng(25.07261111,121.4938056),
                new LatLng(25.06895,121.4810278),
                new LatLng(25.06566944,121.6408),
                new LatLng(25.0632,	121.5133111),
                new LatLng(25.06236111,121.5265278),
                new LatLng(25.06034444,	121.2018111),
                new LatLng(25.05,	121.5786111),
                new LatLng(25.04650278,	121.5079722),
                new LatLng(25.03797222,	121.4325),
                new LatLng(25.03550278,	121.0827611),
                new LatLng(25.02060833,	121.5295556),
                new LatLng(25.017,	121.5163056),
                new LatLng(25.01297222,	121.4586667),
                new LatLng(24.98677778,	121.3087222),
                new LatLng(24.98252778,	121.4518611),
                new LatLng(24.97722222,	121.5377778),
                new LatLng(24.95420833,	121.2049806),
                new LatLng(24.95327778,	121.2216667),
                new LatLng(24.86386944,	121.21635),
                new LatLng(24.90014167,	121.0386528),
                new LatLng(24.80561944,	120.972075),
                new LatLng(24.69696944,	120.8985722),
                new LatLng(24.56526944,	120.8202),
                new LatLng(24.38294167,	120.7588333),
                new LatLng(24.25658611,	120.7417111),
                new LatLng(24.22562778,	120.5687944),
                new LatLng(24.16219722,	120.6169167),
                new LatLng(24.15195833,	120.6410917),
                new LatLng(24.13167222,	120.4690611),
                new LatLng(24.09961111,	120.6776889),
                new LatLng(24.066,	120.5415194),
                new LatLng(23.96884167,	120.9679028),
                new LatLng(23.925175,	120.4096528),
                new LatLng(23.913,	120.6853056),
                new LatLng(23.75638889,	120.6773056),
                new LatLng(23.75350556,	120.251825),
                new LatLng(23.71753333,	120.2028417),
                new LatLng(23.71185278,	120.5449944),
                new LatLng(23.55483889,	120.3455306),
                new LatLng(23.46530833,	120.24735),
                new LatLng(23.46277778,	120.4408333),
                new LatLng(23.30563333,	120.31725),
                new LatLng(23.11509722,	120.2971417),
                new LatLng(23.04916667,	120.2183333),
                new LatLng(22.98458056,	120.2026167),
                new LatLng(22.88358333,	120.5305417),
                new LatLng(22.75750556,	120.3056889),
                new LatLng(22.73366667,	120.3282889),
                new LatLng(22.68905556,	120.3326306),
                new LatLng(22.67486111,	120.2929167),
                new LatLng(22.67308056,	120.4880333),
                new LatLng(22.63256667,	120.2880861),
                new LatLng(22.62739167,	120.3580833),
                new LatLng(22.60871111,	120.3120167),
                new LatLng(22.60538611,	120.3075639),
                new LatLng(22.56583333,	120.3377361),
                new LatLng(22.56413611,	120.425311),
                new LatLng(22.52310833,	120.561175),
                new LatLng(22.4795,	120.41175),
                new LatLng(21.95806944,	120.7889278),
                new LatLng(24.74791667,	121.7463944),
                new LatLng(24.63220278,	121.7929278),
                new LatLng(23.97130556,	121.5997694),
                new LatLng(23.04508333,	121.1619333),
                new LatLng(22.75535833,	121.15045),
                new LatLng(26.15361111,	119.9525),
                new LatLng(24.43213333,	118.3122556),
                new LatLng(23.56903056,	119.5661583),
                new LatLng(23.12216944,	120.4697361),
                new LatLng(23.84315833,	120.2818139),
                new LatLng(23.75754722,	120.3487417),
                new LatLng(22.35222222,	120.3772222),
        };
        public String place_name[] = {
                "富貴角", "陽明", "萬里", "淡水", "基隆", "士林", "林口", "三重", "菜寮", "汐止", "大同", "中山", "大園", "松山",
                "萬華", "新莊", "觀音", "古亭", "永和", "板橋", "桃園", "土城", "新店", "平鎮", "中壢", "龍潭", "湖口", "新竹",
                "頭份", "苗栗", "三義", "豐原", "沙鹿", "西屯", "忠明", "線西", "大里", "彰化", "埔里", "二林", "南投", "竹山",
                "麥寮", "臺西", "斗六", "新港", "圤子", "嘉義", "新營", "善化", "安南", "臺南", "美濃", "橋頭", "楠梓", "仁武",
                "左營", "屏東", "前金", "鳳山", "復興", "前鎮", "小港", "大寮", "潮州", "林園", "恆春", "宜蘭", "冬山", "花蓮",
                "關山", "臺東", "馬祖", "金門", "馬公", "臺南", "彰化", "崙背", "屏東"
        };
        public int shortest_place(LatLng curL){
            double dis = 1000.0;
            int id=0;
            for(int i=0; i<M; ++i){
                double diff1=curL.latitude-LLplace[i].latitude,diff2=curL.longitude-LLplace[i].longitude;
                if(diff1*diff1+diff2*diff2<dis){
                    id=i;
                    dis=diff1*diff1+diff2*diff2;
                }
            }
            return id;
        }

    @Override
    public boolean onMarkerClick(Marker marker) {//點擊標記事件
        LatLng tmp = marker.getPosition();
        int indx = shortest_place(tmp);
        if (isEmpty()) {//空的就存資料
            push(indx);
        } else {
            if (top() == indx) {//如果連續點兩次標記
                if(onoff){//true
                    if(bagsize<10){
                        marker.setVisible(false);//設定為不可看見
                        save_select_obs(get_NowMarker(marker));//存檔
                        /*存背包大小資料*/
                        SharedPreferences saveid = getApplication().getSharedPreferences("baglist", Context.MODE_PRIVATE);
                        saveid.edit().putInt("bagsize",++bagsize).commit();
                    }
                    else{
                        Toast.makeText(this, "收納地區最多10個，不能再收納啦!!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                save_data(indx);//存檔
                open_activity();//開觀測站資料
                }
            } else {
                pop();
                push(indx);
            }
        }

        return false;
    }

    private void save_data(int id){//存入當前地區
        SharedPreferences saveid = getApplication().getSharedPreferences("ssssid", Context.MODE_PRIVATE);
        saveid.edit().clear().commit();
        saveid.edit().putString("idsave", place_name[id]).apply();
    }

    private void open_activity(){//開啟觀測站資訊頁面
        Intent intent;
        intent = new Intent();
        //final Bundle bdlc = new Bundle();
        intent.setClass(am.this,ai.class);
        //bdlc.putInt("btnid", R.id.locatedcv);
        //intent.putExtras(bdlc);
        startActivity(intent);
    }
    private void save_select_obs(int id){//存收納袋
        bag[id]=1;
        SharedPreferences saveid = getApplication().getSharedPreferences("baglist", Context.MODE_PRIVATE);
        String tmp= String.valueOf(id);
        saveid.edit().putInt("baglist"+tmp,bag[id]).commit();
    }
    private int get_NowMarker(Marker marker){//哪個標記
        for(int i=0; i<M; ++i){
            if(marker.equals(Marr[i]))
                return i;
        }
        return -1;
    }
    private void showOnoff(){
        if(onoff)
            Toast.makeText(this, "收納鎖定開啟中", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "收納鎖定已關閉", Toast.LENGTH_SHORT).show();
    }
    private void reset_save(){//重設定讀檔
        SharedPreferences saveid = getApplication().getSharedPreferences("baglist", Context.MODE_PRIVATE);
        saveid.edit().clear().commit();
        for(int i=0; i<M; ++i){
            bag[i]=0;
            String tmp= String.valueOf(i);
            saveid.edit().putInt("baglist"+tmp,bag[i]).commit();
        }
        bagsize=0;
        saveid.edit().putInt("bagsize", bagsize).commit();
    }
}
