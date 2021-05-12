package com.gipl.imagepicker.models;

import android.graphics.Bitmap;

/**
 * Created by Ankit on 01-Jun-19.
 */
public class ImageResult {
    private String sImagePath;
    private Bitmap bitmap;

    public ImageResult(String sImagePath, Bitmap bitmap) {
        this.sImagePath = sImagePath;
        this.bitmap = bitmap;
    }

    public String getsImagePath() {
        return sImagePath;
    }

    public Bitmap getImageBitmap() {
        return bitmap;
    }

    public void setImagePath(String sImagePath) {
        this.sImagePath = sImagePath;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
