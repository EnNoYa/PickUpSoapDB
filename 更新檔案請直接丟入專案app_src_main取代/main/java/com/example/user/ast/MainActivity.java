package com.example.user.ast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent();

        final Bundle bds = new Bundle();
        ImageButton bms = findViewById(R.id.bs);
        bms.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(MainActivity.this,SettingsA.class);
                bds.putInt("btnid", R.id.bs);
                intent.putExtras(bds);
                startActivity(intent);
            }
        });

        final Bundle blocal_area = new Bundle();
        Button local_area = (Button)findViewById(R.id.button_local_area);
        local_area.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(MainActivity.this,ais.class);
                blocal_area.putInt("btnid", R.id.button_local_area);
                intent.putExtras(blocal_area);
                startActivity(intent);
            }
        });

        final Bundle bsodi_area = new Bundle();
        Button sodi_area = (Button)findViewById(R.id.button_sodi_area);
        sodi_area.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(MainActivity.this,am.class);
                bsodi_area.putInt("btnid", R.id.button_sodi_area);
                intent.putExtras(bsodi_area);
                startActivity(intent);
            }
        });

    }
}
