package com.gipl.imagepicker.listener

import com.gipl.imagepicker.models.ImageResult
import com.gipl.imagepicker.exceptions.ImageErrors

interface IImagePickerError {
    fun onError(imageErrors: ImageErrors?)
}