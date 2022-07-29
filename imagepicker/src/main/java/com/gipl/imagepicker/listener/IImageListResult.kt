package com.gipl.imagepicker.listener

import com.gipl.imagepicker.models.ImageResult
import com.gipl.imagepicker.exceptions.ImageErrors
import java.util.ArrayList

interface IImageListResult {
    /**
     * for muti select you will receive result from this listener
     *
     * @param imageResults :array of ImageResult(image path and bitmap)
     */
    fun onReceiveImageList(imageResults: ArrayList<ImageResult?>?)
}