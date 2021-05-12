package com.gipl.imagepicker.listener;

import android.os.Parcel;

import com.gipl.imagepicker.exceptions.ImageErrors;
import com.gipl.imagepicker.models.ImageResult;

import java.util.ArrayList;

/**
 * Created by Ankit on 20-May-19.
 */
public class PickerResult implements IImagePickerResult {


    public static final  Creator<PickerResult>CREATOR =new Creator<PickerResult>() {
        @Override
        public PickerResult createFromParcel(Parcel source) {
            return new PickerResult();
        }

        @Override
        public PickerResult[] newArray(int size) {
            return new PickerResult[0];
        }
    };

    public PickerResult() {
    }

    @Override
    public void onImageGet(ImageResult imageResult) {

    }

    @Override
    public void onReceiveImageList(ArrayList<ImageResult> sFilePath) {

    }

    @Override
    public void onError(ImageErrors imageErrors) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
