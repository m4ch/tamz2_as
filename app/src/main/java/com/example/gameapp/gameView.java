package com.example.gameapp;

import android.content.Context;
import android.content.ContextWrapper;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class gameView extends View {
    public ArrayList<Box> items;
    public ArrayList<Box> movinItems;
    private boolean movin;
    public int score;
    private Date lastFrame;
    private int w;
    private int h;
    private float boxHeight, boxWidth;
    private int padding;
    private Pos direction;
    private boolean canAdd;

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
        this.canAdd = false;

        items.add(new Box(this.boxWidth, 0*(this.boxWidth + padding)+ padding, 0*(this.boxWidth+ padding)+ padding, 2));
        items.add(new Box(this.boxWidth,0*(this.boxWidth + padding)+ padding, 1*(this.boxWidth+ padding)+ padding, 4));


        items.add(new Box(this.boxWidth, 2*(this.boxWidth + padding)+ padding, 0*(this.boxWidth+ padding)+ padding, 2));
        items.add(new Box(this.boxWidth,2*(this.boxWidth + padding)+ padding, 2*(this.boxWidth+ padding)+ padding, 4));

        items.add(new Box(this.boxWidth, 3*(this.boxWidth + padding)+ padding, 3*(this.boxWidth+ padding)+ padding, 4));
        items.add(new Box(this.boxWidth,3*(this.boxWidth + padding)+ padding, 0*(this.boxWidth+ padding)+ padding, 4));

        items.add(new Box(this.boxWidth, 0*(this.boxWidth + padding)+ padding, 3*(this.boxWidth+ padding)+ padding, 2));
        items.add(new Box(this.boxWidth,1*(this.boxWidth + padding)+ padding, 2*(this.boxWidth+ padding)+ padding, 4));

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = w;
        this.padding = 50;
        this.boxWidth = (this.w - 5*this.padding) / 4;
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
            this.canAdd = true;
            checkMoves(diff);
            for(int i = 0; i < this.movinItems.size();i++){
                this.movinItems.get(i).move(diff, this.direction);
            }
        } else if(this.canAdd){
            this.canAdd = false;
            addBox();
        }
        invalidate();
    }

    private void addBox(){
        ArrayList<Pos> p = findFreePositions();
        if(p.size() == 0) Log.d("state","win Or Lose");
        int random = new Random().nextInt(((p.size()-1) - 0) + 1);
        Log.d("FreePositionIndex",""+random);
        this.items.add(new Box(this.boxWidth, p.get(random).x, p.get(random).y, 2));

    }

    private ArrayList<Pos> findFreePositions(){
        ArrayList<Pos> p = new ArrayList<Pos>();
        boolean add = true;
        for(int x = padding; x <= padding * 4 + boxWidth * 4; x+=padding + boxWidth){
            for(int y = padding; y <= padding * 4 + boxWidth * 4; y+=padding + boxWidth){
                for(int z = 0; z < items.size();z++){
                    if(items.get(z).x >= x && items.get(z).x <= x + boxWidth && items.get(z).y >= y && items.get(z).y <= y + boxWidth){
                        add = false;
                        break;
                    } else {
                        add = true;
                    }
                }
                if(add) p.add(new Pos(x,y));
                add = true;
            }
        }
        return p;
    }

    public boolean isAnyoneMoving(){
        for(int i = 0; i < this.items.size();i++){
            if(this.items.get(i).movin) return true;
        }
        return false;
    }

    public void checkMoves(long diff){
        this.movinItems.clear();
        if(this.direction.x > 0 && this.items.get(0).x + this.boxWidth + diff * this.items.get(0).speed + this.padding < this.w ||
           this.direction.y > 0 && this.items.get(0).y + this.boxWidth + diff * this.items.get(0).speed + this.padding < this.h ||
           this.direction.y < 0 && this.items.get(0).y - diff * this.items.get(0).speed - this.padding >= 0 ||
           this.direction.x < 0 && this.items.get(0).x - diff * this.items.get(0).speed - this.padding >= 0
             ) { // dokud nenarazim na okraje canvasu, pokracuj bezporblemu
                this.movinItems = (ArrayList<Box>)items.clone();
                return;
        }
        for (int i = 0; i < this.items.size(); i++) { // potom, co alespon jeden prvek narazil za okraj canvasu
            if(this.direction.x > 0) // pokud mam direction doprava
                if(this.items.get(i).x + this.boxWidth + diff * this.items.get(i).speed + this.padding >= this.w){ // pohyb doprava only
                    Box b = this.items.get(i);
                    b.x = this.w - this.boxWidth - this.padding;
                    b.movin = false;
                    continue;
                }

            if(this.direction.x < 0) // pokud mam direction doleva
                if(this.items.get(i).x - diff * this.items.get(i).speed - this.padding <= 0){ // pohyb doleva
                    Box b = this.items.get(i);
                    b.x = this.padding;
                    b.movin = false;
                    continue;
                }

            if(this.direction.y > 0) // pokud mam direction dolu
                if(this.items.get(i).y + this.boxWidth + diff * this.items.get(i).speed + this.padding >= this.h){ // pohyb dolu
                    Box b = this.items.get(i);
                    b.y = this.h - this.boxWidth - this.padding;
                    b.movin = false;
                    continue;
                }

            if(this.direction.y < 0) // pokud mam direction nahoru
                if(this.items.get(i).y - diff * this.items.get(i).speed - this.padding <= 0){ // pohyb nahoru
                    Box b = this.items.get(i);
                    b.y = this.padding;
                    b.movin = false;
                    continue;
                }


            // reseni okolniho partnera
            Box bTmp = findBoxPartner(i, this.items.get(i), diff);
            if(bTmp == null) {
                this.movinItems.add(this.items.get(i));
                continue;
            }
            if (this.direction.x > 0) {
                if (!bTmp.movin && bTmp.value != this.items.get(i).value) {
                    Box b = this.items.get(i);
                    b.x = bTmp.x - boxWidth - padding;
                    b.movin = false;
                } else if(!bTmp.movin && bTmp.value == this.items.get(i).value){
                    bTmp.value += this.items.get(i).value;
                    removeWithScore(i);
                } else {
                    this.movinItems.add(this.items.get(i));
                }
            } else if (this.direction.x < 0) {
                if (!bTmp.movin && bTmp.value != this.items.get(i).value) {
                    Box b = this.items.get(i);
                    b.x = bTmp.x + boxWidth + padding;
                    b.movin = false;
                } else if(!bTmp.movin && bTmp.value == this.items.get(i).value){
                    bTmp.value += this.items.get(i).value;
                    this.items.get(i).movin = false;
                    removeWithScore(i);
                } else {
                    this.movinItems.add(this.items.get(i));
                }
            } else if (this.direction.y > 0) {
                if (!bTmp.movin && bTmp.value != this.items.get(i).value) {
                    Box b = this.items.get(i);
                    b.y = bTmp.y - boxWidth - padding;
                    b.movin = false;
                } else if(!bTmp.movin && bTmp.value == this.items.get(i).value){
                    bTmp.value += this.items.get(i).value;
                    removeWithScore(i);
                } else {
                    this.movinItems.add(this.items.get(i));
                }
            } else if (this.direction.y < 0) {
                if (!bTmp.movin && bTmp.value != this.items.get(i).value) {
                    Box b = this.items.get(i);
                    b.y = bTmp.y + boxWidth + padding;
                    b.movin = false;
                } else if(!bTmp.movin && bTmp.value == this.items.get(i).value){
                    bTmp.value += this.items.get(i).value;
                    removeWithScore(i);
                } else {
                    this. movinItems.add(this.items.get(i));
                }
            }
        }
    }

    public Box findBoxPartner(int startIndex, Box b, long diff){
        for (int i = startIndex -1; i >= 0; i--
        ) {
            Box tmp = items.get(i);
            if(this.direction.x > 0 && b.x + boxWidth + padding >=  tmp.x - (diff * tmp.speed) &&
                    b.x + boxWidth + padding <= tmp.x + (diff * tmp.speed) && tmp.y == b.y )
                return tmp;
            if(this.direction.x < 0 && b.x - padding - boxWidth >= tmp.x - (diff * tmp.speed) &&
                    b.x - padding - boxWidth <= tmp.x + (diff * tmp.speed) && tmp.y == b.y)
                return tmp;
            if(this.direction.y > 0 && b.y + boxWidth + padding >=  tmp.y - (diff * tmp.speed) &&
                    b.y + boxWidth + padding < tmp.y + (diff * tmp.speed) && tmp.x == b.x )
                return tmp;
            if(this.direction.y < 0 && b.y - padding - boxWidth >= tmp.y - (diff * tmp.speed) &&
                    b.y - padding - boxWidth <= tmp.y + (diff * tmp.speed) && tmp.x == b.x)
                return tmp;
        }
        return null;
    }

    public void removeWithScore(int i){
        this.score += this.items.get(i).value;
        this.items.remove(i);
        GameActivity ga = getGameActivity();
        ga.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ga.setTextScore(score);
                    Log.d("score", ""+score);
                }
            }
        );
    }

    private GameActivity getGameActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof GameActivity) {
                return (GameActivity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

}