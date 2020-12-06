package com.example.gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ScoreActivity extends AppCompatActivity {
    private ArrayList<Integer> score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.score = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences("SCORE",0);
        try {
            JSONArray js = new JSONArray(sp.getString("score","[]"));
            for(int i = 0; i < js.length(); i++){
                this.score.add(js.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_score);
        Collections.sort(this.score, Collections.reverseOrder());
        ArrayAdapter adapter = new ArrayAdapter<Integer>(this,
                R.layout.single_view, score);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
    }
}