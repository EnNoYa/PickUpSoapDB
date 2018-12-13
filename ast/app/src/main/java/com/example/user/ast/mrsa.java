package com.example.user.ast;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class mrsa extends AppCompatActivity {

  private CheckBox checkBoxHeartDisease,checkBoxDVC,checkBoxRespiratoryDisease,checkBoxConjunctivitis,checkBoxAllergicRhinitis;
  private Button patientBntOK;
  private TextView patientView;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mrsa);
    SharedPreferences HealthRecord = getSharedPreferences("healthresult",0);
    checkBoxHeartDisease = (CheckBox)findViewById(R.id.HeartDisease);
    checkBoxDVC = (CheckBox)findViewById(R.id.DVC);
    checkBoxRespiratoryDisease = (CheckBox)findViewById(R.id.RespiratoryDisease);
    checkBoxConjunctivitis = (CheckBox)findViewById(R.id.Conjunctivitis);
    checkBoxAllergicRhinitis = (CheckBox)findViewById(R.id.AllergicRhinitis);
    patientBntOK = (Button)findViewById(R.id.patientViewOK);
    patientView = (TextView)findViewById(R.id.patientCheck);
    patientBntOK.setOnClickListener(patientBntOnClick);
    final SharedPreferences.Editor editor = HealthRecord.edit();
    if(HealthRecord.getBoolean("checkedHeartDisease",true) == true) {
      checkBoxHeartDisease.setChecked(true);
    }else {
      checkBoxHeartDisease.setChecked(false);
    }
    if(HealthRecord.getBoolean("checkedDVC",true) == true) {
      checkBoxDVC.setChecked(true);
    }else {
      checkBoxDVC.setChecked(false);
    }
    if(HealthRecord.getBoolean("checkedRespiratoryDisease",true) == true) {
      checkBoxRespiratoryDisease.setChecked(true);
    }else {
      checkBoxRespiratoryDisease.setChecked(false);
    }
    if(HealthRecord.getBoolean("checkedConjunctivitis",true) == true) {
      checkBoxConjunctivitis.setChecked(true);
    }else {
      checkBoxConjunctivitis.setChecked(false);
    }
    if(HealthRecord.getBoolean("checkedAllergicRhinitis",true) == true) {
      checkBoxAllergicRhinitis.setChecked(true);
    }else {
      checkBoxAllergicRhinitis.setChecked(false);
    }

    checkBoxHeartDisease.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(checkBoxHeartDisease.isChecked()) {
          editor.putBoolean("checkedHeartDisease", true);
          editor.apply();
        }else{
          editor.putBoolean("checkedHeartDisease", false);
          editor.apply();
        }
      }
    });
    checkBoxDVC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(checkBoxDVC.isChecked()) {
          editor.putBoolean("checkedDVC", true);
          editor.apply();
        }else{
          editor.putBoolean("checkedDVC", false);
          editor.apply();
        }
      }
    });
    checkBoxRespiratoryDisease.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(checkBoxRespiratoryDisease.isChecked()) {
          editor.putBoolean("checkedRespiratoryDisease", true);
          editor.apply();
        }else{
          editor.putBoolean("checkedRespiratoryDisease", false);
          editor.apply();
        }
      }
    });
    checkBoxConjunctivitis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(checkBoxConjunctivitis.isChecked()) {
          editor.putBoolean("checkedConjunctivitis", true);
          editor.apply();
        }else{
          editor.putBoolean("checkedConjunctivitis", false);
          editor.apply();
        }
      }
    });
    checkBoxAllergicRhinitis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(checkBoxAllergicRhinitis.isChecked()) {
          editor.putBoolean("checkedAllergicRhinitis", true);
          editor.apply();
        }else{
          editor.putBoolean("checkedAllergicRhinitis", false);
          editor.apply();
        }
      }
    });
  }


  private View.OnClickListener patientBntOnClick = new View.OnClickListener() {
    @Override
    public void onClick(View v) {

      String s = "您選擇的病例是:";
      if(checkBoxHeartDisease.isChecked())
        s+="\n心臟疾病";
      if(checkBoxDVC.isChecked())
        s+="\n心血管疾病";
      if(checkBoxRespiratoryDisease.isChecked())
        s+="\n呼吸道疾病";
      if (checkBoxConjunctivitis.isChecked())
        s+="\n結膜炎";
      if (checkBoxAllergicRhinitis.isChecked())
        s+="\n過敏性鼻炎";
      patientView.setText(s);
    }
  };


}
