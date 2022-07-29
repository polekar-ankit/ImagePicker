package com.gipl.imagepicker.listener;

import com.gipl.imagepicker.models.ImageResult;

import java.util.ArrayList;

public interface IImageListResult {

    /**
     * for muti select you will receive result from this listener
     *
     * @param imageResults :array of ImageResult(image path and bitmap)
     */
    void onReceiveImageList(ArrayList<ImageResult> imageResults);
}
