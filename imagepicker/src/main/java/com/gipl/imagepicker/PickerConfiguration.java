package com.gipl.imagepicker;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Creted by User on 25-Jan-19
 */

public class PickerConfiguration implements Parcelable {


    private boolean fIsSetCustomDialog;
    private int colorCodeText;
    private int colorCodeIcon;
    private boolean fIsDialogCancelable;
    private int nBackGroundColor;
    private int cameraImageId;
    private int galleryImageId;
    private boolean isEnableMultiSelect;
    private ImagePicker.IPickerDialogListener pickerDialogListener;
    private ImagePicker.IImagePickerResult imagePickerResult;
    private String sCameraTitle;
    private String sGalleryTitle;



    protected PickerConfiguration(Parcel in) {
        fIsSetCustomDialog = in.readByte() != 0;
        colorCodeText = in.readInt();
        colorCodeIcon = in.readInt();
        fIsDialogCancelable = in.readByte() != 0;
        nBackGroundColor = in.readInt();
        cameraImageId = in.readInt();
        galleryImageId = in.readInt();
        isEnableMultiSelect = in.readByte() != 0;
        pickerDialogListener = in.readParcelable(ImagePicker.IPickerDialogListener.class.getClassLoader());
        imagePickerResult = in.readParcelable(ImagePicker.IImagePickerResult.class.getClassLoader());
        sCameraTitle = in.readString();
        sGalleryTitle = in.readString();
        multiSelectImageCount = in.readInt();
    }

    public static final Creator<PickerConfiguration> CREATOR = new Creator<PickerConfiguration>() {
        @Override
        public PickerConfiguration createFromParcel(Parcel in) {
            return new PickerConfiguration(in);
        }

        @Override
        public PickerConfiguration[] newArray(int size) {
            return new PickerConfiguration[size];
        }
    };

    public int getMultiSelectImageCount() {
        return multiSelectImageCount;
    }

    public PickerConfiguration setMultiSelectImageCount(int multiSelectImageCount) {
        this.multiSelectImageCount = multiSelectImageCount;
        return this;
    }

    private int multiSelectImageCount;

    private PickerConfiguration() {
        colorCodeText = Color.BLACK;
        nBackGroundColor = Color.WHITE;
        fIsSetCustomDialog = false;
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

    public boolean isEnableMultiSelect() {
        return isEnableMultiSelect;
    }

    public PickerConfiguration enableMultiSelect(boolean enableMultiSelect) {
        isEnableMultiSelect = enableMultiSelect;
        return this;
    }

    public ImagePicker.IImagePickerResult getImagePickerResult() {
        return imagePickerResult;
    }

    public PickerConfiguration setImagePickerResult(ImagePicker.IImagePickerResult imagePickerResult) {
        this.imagePickerResult = imagePickerResult;
        return this;
    }

    public ImagePicker.IPickerDialogListener getPickerDialogListener() {
        return pickerDialogListener;
    }

    public PickerConfiguration setPickerDialogListener(ImagePicker.IPickerDialogListener pickerDialogListener) {
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

    boolean isIsSetCustomDialog() {
        return fIsSetCustomDialog;
    }

    public PickerConfiguration setSetCustomDialog(boolean fIsSetCustomDialog) {
        this.fIsSetCustomDialog = fIsSetCustomDialog;
        return this;
    }

    int getTextColor() {
        return colorCodeText;
    }

    public PickerConfiguration setTextColor(int colorCode) {
        this.colorCodeText = colorCode;
        return this;
    }

    boolean isfIsDialogCancelable() {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (fIsSetCustomDialog ? 1 : 0));
        dest.writeInt(colorCodeText);
        dest.writeInt(colorCodeIcon);
        dest.writeByte((byte) (fIsDialogCancelable ? 1 : 0));
        dest.writeInt(nBackGroundColor);
        dest.writeInt(cameraImageId);
        dest.writeInt(galleryImageId);
        dest.writeByte((byte) (isEnableMultiSelect ? 1 : 0));
        dest.writeParcelable(pickerDialogListener, flags);
        dest.writeParcelable(imagePickerResult, flags);
        dest.writeString(sCameraTitle);
        dest.writeString(sGalleryTitle);
        dest.writeInt(multiSelectImageCount);
    }
}
