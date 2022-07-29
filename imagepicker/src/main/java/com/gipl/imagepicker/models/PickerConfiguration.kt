package com.gipl.imagepicker.models;

import android.graphics.Color;

import com.gipl.imagepicker.listener.IImageListResult;
import com.gipl.imagepicker.listener.IImagePickerError;
import com.gipl.imagepicker.listener.IImageResult;
import com.gipl.imagepicker.listener.IPickerDialogListener;

/**
 * Creted by User on 25-Jan-19
 */

public class PickerConfiguration {

    private int colorCodeText;
    private int colorCodeIcon;
    private boolean fIsDialogCancelable;
    private int nBackGroundColor;
    private int cameraImageId;
    private int galleryImageId;
    private boolean isEnableMultiSelect;
    private IPickerDialogListener pickerDialogListener;
    private IImageResult imagePickerResult;
    private IImageListResult iImageListResult;
    private IImagePickerError iImagePickerError;
    private String sCameraTitle;
    private String sGalleryTitle;
    private int multiSelectImageCount;

    private PickerConfiguration() {
        colorCodeText = Color.BLACK;
        nBackGroundColor = Color.WHITE;
        fIsDialogCancelable = true;
        cameraImageId = -1;
        galleryImageId = -1;
        sCameraTitle = "";
        sGalleryTitle = "";
        isEnableMultiSelect = false;
        multiSelectImageCount = 1;

    }

    public static PickerConfiguration build() {
        return new PickerConfiguration();
    }

    public IImageListResult getImageListResult() {
        return iImageListResult;
    }

    public PickerConfiguration setImageListResult(IImageListResult iImageListResult) {
        this.iImageListResult = iImageListResult;
        return this;
    }

    public IImagePickerError getImagePickerError() {
        return iImagePickerError;
    }

    public PickerConfiguration setImagePickerError(IImagePickerError iImagePickerError) {
        this.iImagePickerError = iImagePickerError;
        return this;
    }

    public int getMultiSelectImageCount() {
        return multiSelectImageCount;
    }

    public PickerConfiguration setMultiSelectImageCount(int multiSelectImageCount) {
        this.multiSelectImageCount = multiSelectImageCount;
        return this;
    }

    public boolean isEnableMultiSelect() {
        return isEnableMultiSelect;
    }

    public PickerConfiguration enableMultiSelect(boolean enableMultiSelect) {
        isEnableMultiSelect = enableMultiSelect;
        return this;
    }

    public IImageResult getImagePickerResult() {
        return imagePickerResult;
    }

    public PickerConfiguration setImagePickerResult(IImageResult imagePickerResult) {
        this.imagePickerResult = imagePickerResult;
        return this;
    }

    public IPickerDialogListener getPickerDialogListener() {
        return pickerDialogListener;
    }

    public PickerConfiguration setPickerDialogListener(IPickerDialogListener pickerDialogListener) {
        this.pickerDialogListener = pickerDialogListener;
        return this;
    }

    public int getBackGroundColor() {
        return nBackGroundColor;
    }

    public PickerConfiguration setBackGroundColor(int nBackGroundColor) {
        this.nBackGroundColor = nBackGroundColor;
        return this;
    }


    public int getTextColor() {
        return colorCodeText;
    }

    public PickerConfiguration setTextColor(int colorCode) {
        this.colorCodeText = colorCode;
        return this;
    }

    public boolean isfIsDialogCancelable() {
        return fIsDialogCancelable;
    }

    public PickerConfiguration setIsDialogCancelable(boolean fIsDialogCancelable) {
        this.fIsDialogCancelable = fIsDialogCancelable;
        return this;
    }


    public int getIconColor() {
        return colorCodeIcon;
    }

    public PickerConfiguration setIconColor(int colorCodeIcon) {
        this.colorCodeIcon = colorCodeIcon;
        return this;
    }

    public int getCameraImageId() {
        return cameraImageId;
    }

    public PickerConfiguration setCameraImageId(int cameraImageId) {
        this.cameraImageId = cameraImageId;
        return this;
    }

    public int getGalleryImageId() {
        return galleryImageId;
    }

    public PickerConfiguration setGalleryImageId(int galleryImageId) {
        this.galleryImageId = galleryImageId;
        return this;
    }

    public String getCameraTitle() {
        return sCameraTitle;
    }

    public PickerConfiguration setCameraTitle(String sCameraTitle) {
        this.sCameraTitle = sCameraTitle;
        return this;
    }

    public String getGalleryTitle() {
        return sGalleryTitle;
    }

    public PickerConfiguration setGalleryTitle(String sGalleryTitle) {
        this.sGalleryTitle = sGalleryTitle;
        return this;
    }


}
