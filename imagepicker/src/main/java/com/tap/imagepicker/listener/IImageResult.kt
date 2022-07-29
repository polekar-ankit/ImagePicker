package com.tap.imagepicker.listener

import com.tap.imagepicker.models.ImageResult

interface IImageResult {
    /**
     * for single image you  will receive result from this listener
     *
     * @param imageResult:object of image path and bitmap
     */
    fun onImageGet(imageResult: ImageResult?)
}