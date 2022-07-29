package com.tap.imagepicker.models

import android.graphics.Color
import com.tap.imagepicker.listener.IPickerDialogListener
import com.tap.imagepicker.listener.IImageResult
import com.tap.imagepicker.listener.IImageListResult
import com.tap.imagepicker.listener.IImagePickerErrorListener

/**
 * Creted by User on 25-Jan-19
 */
class PickerConfiguration private constructor() {
    var textColor: Int
        private set
    var iconColor = 0
        private set
    private var fIsDialogCancelable: Boolean
    var backGroundColor: Int
        private set
    var cameraImageId: Int
        private set
    var galleryImageId: Int
        private set
    var isEnableMultiSelect: Boolean
        private set
    var pickerDialogListener: IPickerDialogListener? = null
        private set
    var imagePickerResult: IImageResult? = null
        private set
    var imageListResult: IImageListResult? = null
        private set
    var imagePickerError: IImagePickerErrorListener? = null
        private set
    var cameraTitle: String
        private set
    var galleryTitle: String
        private set
    var multiSelectImageCount: Int
        private set

    fun setImageListResult(iImageListResult: IImageListResult?): PickerConfiguration {
        imageListResult = iImageListResult
        return this
    }

    fun setImagePickerErrorListener(iImagePickerErrorListener: IImagePickerErrorListener?): PickerConfiguration {
        imagePickerError = iImagePickerErrorListener
        return this
    }

    fun setMultiSelectImageCount(multiSelectImageCount: Int): PickerConfiguration {
        this.multiSelectImageCount = multiSelectImageCount
        return this
    }

    fun enableMultiSelect(enableMultiSelect: Boolean): PickerConfiguration {
        isEnableMultiSelect = enableMultiSelect
        return this
    }

    fun setImagePickerResult(imagePickerResult: IImageResult?): PickerConfiguration {
        this.imagePickerResult = imagePickerResult
        return this
    }

    fun setPickerDialogListener(pickerDialogListener: IPickerDialogListener?): PickerConfiguration {
        this.pickerDialogListener = pickerDialogListener
        return this
    }

    fun setBackGroundColor(nBackGroundColor: Int): PickerConfiguration {
        backGroundColor = nBackGroundColor
        return this
    }

    fun setTextColor(colorCode: Int): PickerConfiguration {
        textColor = colorCode
        return this
    }

    fun isfIsDialogCancelable(): Boolean {
        return fIsDialogCancelable
    }

    fun setIsDialogCancelable(fIsDialogCancelable: Boolean): PickerConfiguration {
        this.fIsDialogCancelable = fIsDialogCancelable
        return this
    }

    fun setIconColor(colorCodeIcon: Int): PickerConfiguration {
        iconColor = colorCodeIcon
        return this
    }

    fun setCameraImageId(cameraImageId: Int): PickerConfiguration {
        this.cameraImageId = cameraImageId
        return this
    }

    fun setGalleryImageId(galleryImageId: Int): PickerConfiguration {
        this.galleryImageId = galleryImageId
        return this
    }

    fun setCameraTitle(sCameraTitle: String): PickerConfiguration {
        cameraTitle = sCameraTitle
        return this
    }

    fun setGalleryTitle(sGalleryTitle: String): PickerConfiguration {
        galleryTitle = sGalleryTitle
        return this
    }

    companion object {
        @JvmStatic
        fun build(): PickerConfiguration {
            return PickerConfiguration()
        }
    }

    init {
        textColor = Color.BLACK
        backGroundColor = Color.WHITE
        fIsDialogCancelable = true
        cameraImageId = -1
        galleryImageId = -1
        cameraTitle = ""
        galleryTitle = ""
        isEnableMultiSelect = false
        multiSelectImageCount = 1
    }
}