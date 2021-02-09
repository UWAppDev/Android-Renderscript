package com.example.renderscript;

public class ComputeInput {

    public MainActivity main;
    public int whichBitmap;
    public int adjust;

    public ComputeInput(MainActivity main, int whichBitmap, int adjust) {
        this.main = main;
        this.whichBitmap = whichBitmap;
        this.adjust = adjust;
    }

}
