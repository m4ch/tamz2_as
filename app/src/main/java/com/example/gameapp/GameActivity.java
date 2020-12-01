package com.example.gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Date;

import static java.lang.Thread.sleep;

public class GameActivity extends AppCompatActivity {

    private gameView v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);
        this.v = findViewById(R.id.sample_game_view);
        GameThread gt = new GameThread(v);
        Thread t = new Thread(gt);
        t.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //this.v.direction = (0,1);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            this.v.setMovin();
            Log.d("Movin","touched");
        }

        return true;
    }
}

class GameThread implements Runnable {

    private long last;
    private long time;
    private gameView gameView;
    @Override
    public void run() {
        long diff = 0;
        while(true){
            diff = (new Date()).getTime() - last;
            gameView.nextFrame(diff);
            this.last = new Date().getTime();
            try {
                synchronized (gameView){
               sleep(33);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public GameThread(View canvasView){
        this.gameView = (gameView) canvasView;
        this.last = new Date().getTime();
        this.time = this.last;
    }
}