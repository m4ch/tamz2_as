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
    private Pos direction;

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
        items.add(new Box(this.boxWidth,0*(this.boxWidth + padding)+ padding, 1*(this.boxWidth+ padding)+ padding, 4));

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

    public void setMovin(Pos p){
        this.direction = p;
        for(int i = 0; i < this.items.size();i++){
            this.items.get(i).movin=true;
        }
        if(this.direction.x > 0)
            Collections.sort(items, new XSorter().reversed());
        if(this.direction.x < 0)
            Collections.sort(items, new XSorter());
        if(this.direction.y > 0)
            Collections.sort(items, new YSorter().reversed());
        if(this.direction.y < 0)
            Collections.sort(items, new YSorter());

    }

    public void nextFrame(long diff){
        if(isAnyoneMoving()){
            checkMoves(diff);
            for(int i = 0; i < this.movinItems.size();i++){
                this.movinItems.get(i).move(diff, this.direction);
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
        if(items.get(0).x + boxWidth + diff * items.get(0).speed + padding < this.w &&
            items.get(0).y + boxWidth + diff * items.get(0).speed + padding < this.h &&
            items.get(0).y - diff * items.get(0).speed - padding >= 0 &&
            items.get(0).x - diff * items.get(0).speed - padding >= 0
             ) { // dokud nenarazim na okraje canvasu, pokracuj bezporblemu
                movinItems = (ArrayList<Box>)items.clone();
                return;
        }
        for (int i = 0; i < items.size(); i++) { // potom, co alespon jeden prvek narazil za okraj canvasu
            if(direction.x > 0) // pokud mam direction doprava
                if(items.get(i).x + boxWidth + diff * items.get(i).speed + padding >= this.w){ // pohyb doprava only
                    Box b = items.get(i);
                    b.x = this.w - boxWidth - padding;
                    b.movin = false;
                    continue;
                }

            if(direction.x < 0) // pokud mam direction doleva
                if(items.get(i).x - diff * items.get(i).speed - padding <= 0){ // pohyb doleva
                    Box b = items.get(i);
                    b.x = padding;
                    b.movin = false;
                    continue;
                }

            if(direction.y > 0) // pokud mam direction dolu
                if(items.get(i).y + boxWidth + diff * items.get(i).speed + padding >= this.h){ // pohyb dolu
                    Box b = items.get(i);
                    b.y = this.h - boxWidth - padding;
                    b.movin = false;
                    continue;
                }

            if(direction.y < 0) // pokud mam direction nahoru
                if(items.get(i).y - diff * items.get(i).speed - padding <= 0){ // pohyb nahoru
                    Box b = items.get(i);
                    b.y = padding;
                    b.movin = false;
                    continue;
                }


            // reseni okolniho partnera
            Box bTmp = findBoxPartner(i, items.get(i));
            if(bTmp == null) {
                movinItems.add(items.get(i));
                continue;
            }
            if (direction.x > 0) {
                if (!bTmp.movin) {
                    Box b = items.get(i);
                    b.x = bTmp.x - boxWidth - padding;
                    b.movin = false;
                }
            } else if (direction.x < 0) {
                if (!bTmp.movin) {
                    Box b = items.get(i);
                    b.x = bTmp.x + boxWidth + padding;
                    b.movin = false;
                }
            } else if (direction.y > 0) {
                if (!bTmp.movin) {
                    Box b = items.get(i);
                    b.y = bTmp.y - boxWidth - padding;
                    b.movin = false;
                }
            } else if (direction.y < 0) {
                if (!bTmp.movin) {
                    Box b = items.get(i);
                    b.y = bTmp.y + boxWidth + padding;
                    b.movin = false;
                }
            }
        }
    }

    public Box findBoxPartner(int startIndex, Box b){
        for (int i = startIndex -1; i >= 0; i--
        ) {
            Box tmp = items.get(i);
            if(this.direction.x > 0 && b.x + boxWidth + padding >=  tmp.x && tmp.x + boxWidth <= b.x + 2* boxWidth + padding && tmp.y == b.y )
                return tmp;
            if(this.direction.x < 0 && b.x - boxWidth - padding >= tmp.x && tmp.x + boxWidth <= b.x - boxWidth - padding && tmp.y == b.y)
                return tmp;
            if(this.direction.y > 0 && b.y + boxWidth + padding >=  tmp.y && tmp.y + boxWidth <= b.y + 2* boxWidth + padding && tmp.x == b.x )
                return tmp;
            if(this.direction.y < 0 && b.y - boxWidth - padding >= tmp.y && tmp.y + boxWidth <= b.y - boxWidth - padding && tmp.x == b.x)
                return tmp;
        }
        return null;
    }

}