package com.example.gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {
    private String diffi;
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_settings);
        this.radioGroup = (RadioGroup)findViewById(R.id.id_radio_group);

    }
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences gamePref = getSharedPreferences("SETTINGS",0);
        SharedPreferences.Editor editor = gamePref.edit();
        editor.clear();
        RadioButton r = ((RadioButton)findViewById(this.radioGroup.getCheckedRadioButtonId()));
        editor.putString("DIFFICULTY",r.getText().toString());
        editor.commit();
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences gamePref = getSharedPreferences("SETTINGS",0);
        String s = gamePref.getString("DIFFICULTY","easy");
        if(s.equals("easy")){
            ((RadioButton)findViewById(R.id.radioEasy)).setChecked(true);
            ((RadioButton)findViewById(R.id.radioHard)).setChecked(false);
        } else {
            ((RadioButton)findViewById(R.id.radioEasy)).setChecked(false);
            ((RadioButton)findViewById(R.id.radioHard)).setChecked(true);
        }
    }
}