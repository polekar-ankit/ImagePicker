package com.tap.imagepicker.listener

import com.tap.imagepicker.models.ImageResult
import java.util.ArrayList

interface IImageListResult {
    /**
     * for muti select you will receive result from this listener
     *
     * @param imageResults :array of ImageResult(image path and bitmap)
     */
    fun onReceiveImageList(imageResults: ArrayList<ImageResult?>?)
}