package com.example.gameapp;

import java.util.Comparator;

public class YSorter implements Comparator<Box> {

    @Override
    public int compare(Box box, Box box2) {
        return (int)( box.y - box2.y);
    }
}
