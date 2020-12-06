package com.example.gameapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Thread.sleep;


public class GameActivity extends AppCompatActivity
        {
    private GestureDetectorCompat mDetector;
    public gameView v;
    public TextView textTime, textScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);
        this.v = findViewById(R.id.sample_game_view);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener(this.v));

        this.textTime = findViewById(R.id.textView_time_value);
        this.textScore = findViewById(R.id.textView_score_value);
        GameThread gt = new GameThread(this);
        Thread t = new Thread(gt);
        t.start();
    }

    public  void setTextTime(String s){
        this.textTime.setText(s);
    }

    public  void setTextScore(int score){
        this.textScore.setText(""+score);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //this.v.direction = (0,1);
        /*if(event.getAction() == MotionEvent.ACTION_DOWN){
            this.v.setMovin();
            Log.d("Movin","touched");
        }

        return true;
        */
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public static final String PREFS_NAME_SETTINGS = "Settings";
    public static final String PREFS_NAME_CONTINUE = "inProgress";
    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences gamePref = getSharedPreferences(PREFS_NAME_CONTINUE,0);
        SharedPreferences.Editor editor = gamePref.edit();
        // Necessary to clear first if we save preferences onPause.
        editor.clear();
        JSONArray jsXS = null;
        JSONArray jsYS = null;
        JSONArray jsValues = null;
        float[] xs = new float[this.v.items.size()];

        float[] ys = new float[this.v.items.size()];

        int[] values = new int[this.v.items.size()];

        try {
            for(int i = 0; i < this.v.items.size();i++){
                xs[i] = this.v.items.get(i).x;
                ys[i] = this.v.items.get(i).y;
                values[i] = this.v.items.get(i).value;

            }
            jsXS = new JSONArray(xs);
            jsYS = new JSONArray(ys);
            jsValues = new JSONArray(values);

            editor.putString("XS",jsXS.toString());
            editor.putString("YS",jsYS.toString());
            editor.putString("Values",jsValues.toString());
            editor.putFloat("boxWidth", this.v.items.get(0).width);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume(){

        Intent in = getIntent();
        if(in.getBooleanExtra("continue",false)){
            SharedPreferences sp = getSharedPreferences(PREFS_NAME_CONTINUE,0);
            String jsXS = sp.getString("XS", "");
            String jsYS = sp.getString("YS", "");
            String jsValues = sp.getString("Values", "");
            Float width = sp.getFloat("boxWidth",250);
            try {
                JSONArray a1 = new JSONArray(jsXS);
                JSONArray a2 = new JSONArray(jsYS);
                JSONArray a3 = new JSONArray(jsValues);
                this.v.items.clear();
                Log.d("js",a1.toString());
                for(int i = 0; i < a1.length();i++){
                    this.v.items.add(new Box(width, (float)a1.getDouble(i),(float)a2.getDouble(i), a3.getInt(i)));
                }

            } catch (JSONException e) {

            }
        }super.onResume();
    }

}

class GameThread implements Runnable {

    private static final int SLEEP_MS = 16;
    private long last;
    private long time, now, tmpTime;
    private TextView scoreText, timeText;
    private GameActivity app;
    private gameView gameView;
    @Override
    public void run() {
        long diff = 0;
        now = 0;
        tmpTime = 0;
        while(true){
            now = (new Date()).getTime();
            diff = now - last;
            gameView.nextFrame(diff);
            if(now - tmpTime > 1000) {
                tmpTime = now;
                this.app.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                                app.setTextTime((int)((now-time)/60000)%60+":"+(int)((now-time)/1000)%60);
                                Log.d("time", ((int)((now-time)/60000)%60+":"+(int)((now-time)/1000)%60));
                        }
                    }
                );
            }
            this.last = new Date().getTime();
            try {
                synchronized (gameView){
               sleep(SLEEP_MS - diff > 0 ? SLEEP_MS - diff : 0);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public GameThread(GameActivity app){
        this.app = app;
        this.scoreText = app.textScore;
        this.timeText = app.textTime;
        this.gameView = (gameView) app.v;
        this.last = new Date().getTime();
        this.time = this.last;
        this.now = this.last;
    }
}

class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String DEBUG_TAG = "My_GameActivity_Gesture";
    private static final int TOUCH_BORDER = 400;
    private gameView v;

    public MyGestureListener(gameView v){
        this.v = v;
    }
    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {


        // ovaladani gesty pomoci velocity - zda se ne moc dobre
                        /*if(velocityX > TOUCH_BORDER && Math.abs(velocityY) < TOUCH_BORDER){
                            v.setMovin(new Pos(1,0));
                        } else if(velocityX < -TOUCH_BORDER && Math.abs(velocityY) < TOUCH_BORDER){
                            v.setMovin(new Pos(-1,0));
                        } else if(velocityY > TOUCH_BORDER && Math.abs(velocityX) < TOUCH_BORDER){
                            v.setMovin(new Pos(0,1));
                        } else if(velocityY < -TOUCH_BORDER && Math.abs(velocityX) < TOUCH_BORDER){
                            v.setMovin(new Pos(0,-1));
                            Log.d(DEBUG_TAG, "onFling: " + velocityX + " : "+velocityY);
                        }*/
        // ovladani pomoci rozdilu mezi zdvizenim a dotekem prstu
        float xDiff = event2.getX() - event1.getX();
        float yDiff = event2.getY() - event1.getY();
        if(xDiff > TOUCH_BORDER && Math.abs(yDiff) < TOUCH_BORDER){
            v.setMovin(new Pos(1,0));
        } else if(xDiff < -TOUCH_BORDER && Math.abs(yDiff) < TOUCH_BORDER){
            v.setMovin(new Pos(-1,0));
        } else if(yDiff > TOUCH_BORDER && Math.abs(xDiff) < TOUCH_BORDER){
            v.setMovin(new Pos(0,1));
        } else if(yDiff < -TOUCH_BORDER && Math.abs(xDiff) < TOUCH_BORDER){
            v.setMovin(new Pos(0,-1));
        }
        return true;

    }
}