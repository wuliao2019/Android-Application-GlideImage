package com.example.glideimage;

import android.graphics.Bitmap;

public class JigsawItem {
    Bitmap itemBitmap;
    //int order;
    int bitmapId;

    public JigsawItem(int bitmapId, Bitmap itemBitmap) {
        //this.order = order;
        this.bitmapId = bitmapId;
        this.itemBitmap = itemBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.itemBitmap = bitmap;
    }
}
