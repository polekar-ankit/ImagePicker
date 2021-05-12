package com.gipl.imagepicker.listener;

import android.os.Parcelable;

import com.gipl.imagepicker.exceptions.ImageErrors;
import com.gipl.imagepicker.models.ImageResult;

import java.util.ArrayList;

public interface IImagePickerResult extends Parcelable {
    /**
     * for single image you  will receive result from this listener
     *
     * @param imageResult:object of image path and bitmap
     */
    void onImageGet(ImageResult imageResult);

    /**
     * for muti select you will receive result from this listener
     *
     * @param imageResults :array of ImageResult(image path and bitmap)
     */
    void onReceiveImageList(ArrayList<ImageResult> imageResults);

    void onError(ImageErrors imageErrors);

}
