package com.gipl.imagepicker.listener

import com.gipl.imagepicker.models.ImageResult
import com.gipl.imagepicker.exceptions.ImageErrors

interface IImageResult {
    /**
     * for single image you  will receive result from this listener
     *
     * @param imageResult:object of image path and bitmap
     */
    fun onImageGet(imageResult: ImageResult?)
}