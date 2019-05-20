package com.gipl.imagepicker;

import android.os.Parcel;

/**
 * Creted by User on 18-Apr-19
 */
public class PickerListener implements ImagePicker.IPickerDialogListener {
    public PickerListener() {
    }

    public static final Creator<PickerListener> CREATOR = new Creator<PickerListener>() {
        @Override
        public PickerListener createFromParcel(Parcel in) {
            return new PickerListener(in);
        }

        @Override
        public PickerListener[] newArray(int size) {
            return new PickerListener[size];
        }
    };

    private PickerListener(Parcel in) {
    }

    @Override
    public void onCancelClick() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
