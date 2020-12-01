package com.example.gameapp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * TODO: document your custom view class.
 */
public class gameView extends View {
    public ArrayList<Box> items;
    public ArrayList<Box> movinItems;
    private boolean movin;
    public boolean score;
    private Date lastFrame;
    private int w;
    private int h;
    private float boxHeight, boxWidth;
    private int padding;

    public gameView(Context context) {
        super(context);
        init(null, 0);
    }

    public gameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public gameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        this.items = new ArrayList<Box>();
        this.movinItems = new ArrayList<Box>();
        this.padding = 50;
        this.boxWidth = 900 / 4;
            items.add(new Box(this.boxWidth, 0*(this.boxWidth + padding)+ padding, 0*(this.boxWidth+ padding)+ padding, 2));
        items.add(new Box(this.boxWidth,1*(this.boxWidth + padding)+ padding, 0*(this.boxWidth+ padding)+ padding, 4));
        items.add(new Box(this.boxWidth,1*(this.boxWidth + padding)+ padding, 1*(this.boxWidth+ padding)+ padding, 8));
        items.add(new Box(this.boxWidth,0*(this.boxWidth + padding)+ padding, 2*(this.boxWidth+ padding)+ padding, 16));

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        Log.d("width",""+w);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void invalidateTextPaintAndMeasurements() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(Color.BLUE, PorterDuff.Mode.CLEAR);
        for(int i = 0; i < this.items.size();i++){
            this.items.get(i).draw(canvas);
        }

    }

    public void setMovin(){
        for(int i = 0; i < this.items.size();i++){
            this.items.get(i).movin=true;
        }
        Collections.sort(items);
    }

    public void nextFrame(long diff){
        if(isAnyoneMoving()){
            checkMoves(diff);
            for(int i = 0; i < this.movinItems.size();i++){
                this.movinItems.get(i).move(diff);
            }
        }
        invalidate();
    }

    public boolean isAnyoneMoving(){
        for(int i = 0; i < this.items.size();i++){
            if(this.items.get(i).movin) return true;
        }
        return false;
    }

    public void checkMoves(long diff){
        this.movinItems.clear();
        if(items.get(0).x + boxWidth + diff * items.get(0).speed + padding < this.w ) {
            movinItems = (ArrayList<Box>)items.clone();
            return;
        }
        for (int i = 0; i < items.size(); i++
        ) {
            if(items.get(i).x + boxWidth + diff * items.get(i).speed + padding >= this.w){ // pohyb doprava only
                Box b = items.get(i);
                b.x = this.w - boxWidth - padding;
                b.movin = false;
                continue;
            }
            Box bTmp = findBoxPartner(i, items.get(i), new Pos(0,0));
            if(bTmp == null) {
                movinItems.add(items.get(i));
                continue;
            }
            if (!bTmp.movin){
                Box b = items.get(i);
                b.x = bTmp.x - boxWidth - padding;
                b.movin = false;
            }
        }
    }

    public Box findBoxPartner(int startIndex, Box b, Pos p){
        for (int i = startIndex -1; i >= 0; i--
        ) {
            Box tmp = items.get(i);
            if(b.x + boxWidth + padding >=  tmp.x && tmp.x + boxWidth <= b.x + 2* boxWidth + padding && tmp.y == b.y )
                return tmp;
        }
        return null;
    }

}