package com.gipl.imagepicker.listener;

import android.os.Parcelable;

import com.gipl.imagepicker.exceptions.ImageErrors;
import com.gipl.imagepicker.models.ImageResult;

import java.util.ArrayList;

public interface IImageResult {
    /**
     * for single image you  will receive result from this listener
     *
     * @param imageResult:object of image path and bitmap
     */
    void onImageGet(ImageResult imageResult);

}
