package com.gipl.imagepicker;

import android.graphics.Bitmap;
import android.os.Parcel;

/**
 * Created by Ankit on 20-May-19.
 */
public class PickerResult implements ImagePicker.IImagePickerResult{


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
    public void onImageGet(String sPath, Bitmap bitmap) {

    }

    @Override
    public void onError(ImagePicker.CameraErrors cameraErrors) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
