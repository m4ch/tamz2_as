package com.example.gameapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Date;

import static java.lang.Thread.sleep;


public class GameActivity extends AppCompatActivity
        {
    private GestureDetectorCompat mDetector;
    private gameView v;
    private static int TOUCH_BORDER = 800;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        this.v = findViewById(R.id.sample_game_view);
        GameThread gt = new GameThread(v);
        Thread t = new Thread(gt);
        t.start();
    }

                class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
                    private static final String DEBUG_TAG = "My_GameActivity_Gesture";

                    @Override
                    public boolean onDown(MotionEvent event) {
                        Log.d(DEBUG_TAG,"onDown: " + event.toString());
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent event1, MotionEvent event2,
                                           float velocityX, float velocityY) {
                        if(velocityX > TOUCH_BORDER){
                            v.setMovin(new Pos(1,0));
                        } else if(velocityX < -TOUCH_BORDER){
                            v.setMovin(new Pos(-1,0));
                        } else if(velocityY > TOUCH_BORDER){
                            v.setMovin(new Pos(0,1));
                        } else if(velocityY < -TOUCH_BORDER){
                            v.setMovin(new Pos(0,-1));
                        }

                        Log.d(DEBUG_TAG, "onFling: " + velocityX + " : "+velocityY);
                        return true;
                    }
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