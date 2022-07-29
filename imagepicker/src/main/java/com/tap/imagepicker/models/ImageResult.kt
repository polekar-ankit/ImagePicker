package com.tap.imagepicker.models

import android.graphics.Bitmap

/**
 * Created by Ankit on 01-Jun-19.
 */
class ImageResult(private var sImagePath: String, var imageBitmap: Bitmap) {
    fun getsImagePath(): String {
        return sImagePath
    }

    fun setImagePath(sImagePath: String) {
        this.sImagePath = sImagePath
    }

    fun setBitmap(bitmap: Bitmap) {
        imageBitmap = bitmap
    }
}