package com.example.gameapp;

import android.graphics.Canvas;

public interface IDrawable extends Comparable {
    public void draw(Canvas c);
    public boolean isMoving();
    public void move(long diff);
}
