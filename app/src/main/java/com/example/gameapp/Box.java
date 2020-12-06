package com.example.gameapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Comparator;

public class Box implements IDrawable {
    public int value;
    public float x, y, width, height;
    public boolean movin;
    public double speed;
    private Paint p;
    public Box(float width, float x, float y, int value){
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = width;
        this.movin = false;
        this.speed = 5;
        this.p = new Paint();
        this.p.setTextSize(width/4);
        this.p.setColor(Color.WHITE);
        this.p.setStyle(Paint.Style.FILL);
    }
    @Override
    public void draw(Canvas c) {
        c.drawRect(x,y, x+width, y+height, new Paint());
        c.drawText(""+this.value, this.x + width/2, this.y + height/2, this.p);
    }

    @Override
    public boolean isMoving() {
        return movin;
    }

    @Override
    public void move(long diff, Pos direction) {
        Log.d("x + diff", x +" : "+diff);
        this.x += direction.x * speed * diff;
        this.y += direction.y * speed * diff;
    }
}

