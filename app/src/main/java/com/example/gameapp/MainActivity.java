package com.example.gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

    }

    public void startNewGame(View view){
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("continue", false);
        startActivity(i);
    }

    public void continueGame(View view){
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("continue", true);
        startActivity(i);
    }


    public void settingActivity(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void scoreActivity(View view) {
        Intent i = new Intent(this, ScoreActivity.class);
        startActivity(i);
    }
}