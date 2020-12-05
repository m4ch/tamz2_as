package com.example.gameapp;

import java.util.Comparator;

public class XSorter implements Comparator<Box> {

    @Override
    public int compare(Box box, Box box2) {
            return (int)( box.x - box2.x);
    }
}
