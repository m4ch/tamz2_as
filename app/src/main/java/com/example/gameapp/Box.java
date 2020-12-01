package com.example.gameapp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Box implements IDrawable {
    public int value;
    public float x, y, width, height;
    public boolean movin;
    public double speed;
    public Box(float width, float x, float y, int value){
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = width;
        this.movin = false;
        this.speed = 0.5;
    }
    @Override
    public void draw(Canvas c) {
        c.drawRect(x,y, x+width, y+height, new Paint());

    }

    @Override
    public boolean isMoving() {
        return movin;
    }

    @Override
    public void move(long diff) {
        Log.d("x + diff", x +" : "+diff);
        this.x += speed * diff;
    }

    @Override
    public int compareTo(Object o) {
        Box b = (Box)o;

        return (int)( b.x - this.x);
    }
}
