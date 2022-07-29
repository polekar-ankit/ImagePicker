package com.tap.imagepicker.listener

import com.tap.imagepicker.exceptions.ImageErrors

interface IImagePickerErrorListener {
    fun onError(imageErrors: ImageErrors?)
}