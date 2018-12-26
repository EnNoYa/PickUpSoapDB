package com.example.user.ast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

public class ns extends AppCompatActivity {

  Intent intent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ns);

    SharedPreferences SettingsSave = getSharedPreferences("settingsave",MODE_PRIVATE);
    final SharedPreferences.Editor editorsettings = SettingsSave.edit();

    intent = new Intent();

    final Bundle bdi = new Bundle();
    final Button ins = (Button)findViewById(R.id.irt);
    ins.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        intent.setClass(ns.this, insa.class);
        bdi.putInt("btnid", R.id.irt);
        intent.putExtras(bdi);
        startActivity(intent);
      }
    });

    final Bundle bdr = new Bundle();
    final Button rns = (Button) findViewById(R.id.rrt);
    rns.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        intent.setClass(ns.this, insa.class);
        bdr.putInt("btnid", R.id.rrt);
        intent.putExtras(bdr);
        startActivity(intent);
      }
    });

    final Spinner chsp = (Spinner)findViewById(R.id.hsp);
    final Spinner cmsp = (Spinner)findViewById(R.id.msp);

    final SwitchCompat ics = findViewById(R.id.inss);
    final SwitchCompat icv = findViewById(R.id.insv);
    final SwitchCompat icr = findViewById(R.id.irts);
    final SwitchCompat rcs = findViewById(R.id.rnss);
    final SwitchCompat rcv = findViewById(R.id.rnsv);
    final SwitchCompat rcr = findViewById(R.id.rrts);

    ins.setEnabled(false);
    icv.setEnabled(false);
    icr.setEnabled(false);
    ins.setEnabled(false);
    rns.setEnabled(false);
    rcv.setEnabled(false);
    rcr.setEnabled(false);
    rns.setEnabled(false);
    chsp.setEnabled(false);
    cmsp.setEnabled(false);

    if(SettingsSave.getBoolean("icschecked",true)==true){
      ics.setChecked(true);
      icv.setEnabled(true);
      icr.setEnabled(true);
    }
    else{
      ics.setChecked(false);
      icv.setEnabled(false);
      icr.setEnabled(false);
      ins.setEnabled(false);
    }

    if(SettingsSave.getBoolean("icvchecked",true)==true){
      icv.setChecked(true);
    }
    else{
      icv.setChecked(false);
    }

    if(SettingsSave.getBoolean("icrchecked",true)==true){
      icr.setChecked(true);
      if(ics.isChecked())
        ins.setEnabled(true);
    }
    else{
      icr.setChecked(false);
      ins.setEnabled(false);
    }

    if(SettingsSave.getBoolean("rcschecked",true)==true){
      rcs.setChecked(true);
      rcv.setEnabled(true);
      rcr.setEnabled(true);
      chsp.setEnabled(true);
      cmsp.setEnabled(true);
    }
    else{
      rcs.setChecked(false);
      rcv.setEnabled(false);
      rcr.setEnabled(false);
      rns.setEnabled(false);
      chsp.setEnabled(false);
      cmsp.setEnabled(false);
    }

    if(SettingsSave.getBoolean("rcvchecked",true)==true){
      rcv.setChecked(true);
    }
    else{
      rcv.setChecked(false);
    }

    if(SettingsSave.getBoolean("rcrchecked",true)==true){
      rcr.setChecked(true);
      if(rcs.isChecked())
        rns.setEnabled(true);
    }
    else{
      rcr.setChecked(false);
      rns.setEnabled(false);
    }



    ics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (ics.isChecked()) {
          editorsettings.putBoolean("icschecked",true);
          editorsettings.apply();
          icv.setEnabled(true);
          icr.setEnabled(true);
          if(icr.isChecked())
            ins.setEnabled(true);
        } else {
          editorsettings.putBoolean("icschecked",false);
          editorsettings.apply();
          icv.setEnabled(false);
          icr.setEnabled(false);
          ins.setEnabled(false);
        }
      }
    });

    icv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (icv.isChecked()) {
          editorsettings.putBoolean("icvchecked",true);
          editorsettings.apply();
        } else {
          editorsettings.putBoolean("icvchecked",false);
          editorsettings.apply();
        }
      }
    });

    icr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          editorsettings.putBoolean("icrchecked",true);
          editorsettings.apply();
          ins.setEnabled(true);
        } else {
          editorsettings.putBoolean("icrchecked",false);
          editorsettings.apply();
          ins.setEnabled(false);
        }
      }
    });

    rcs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          editorsettings.putBoolean("rcschecked",true);
          editorsettings.apply();
          rcv.setEnabled(true);
          rcr.setEnabled(true);
          chsp.setEnabled(true);
          cmsp.setEnabled(true);
          Intent intent; intent = new Intent(ns.this,MyService.class);
          startService(intent);

          if(rcr.isChecked())
            rns.setEnabled(true);
        } else{
          editorsettings.putBoolean("rcschecked",false);
          editorsettings.apply();
          rcv.setEnabled(false);
          rcr.setEnabled(false);
          rns.setEnabled(false);
          chsp.setEnabled(false);
          cmsp.setEnabled(false);
          stopService(new Intent(getBaseContext(),MyService.class));
        }
      }
    });

    rcv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (rcv.isChecked()) {
          editorsettings.putBoolean("rcvchecked",true);
          editorsettings.apply();
        } else {
          editorsettings.putBoolean("rcvchecked",false);
          editorsettings.apply();
        }
      }
    });

    rcr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          editorsettings.putBoolean("rcrchecked",true);
          editorsettings.apply();
          rns.setEnabled(true);
        } else {
          editorsettings.putBoolean("rcrchecked",false);
          editorsettings.apply();
          rns.setEnabled(false);
        }
      }
    });
  }
}
