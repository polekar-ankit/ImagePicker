package com.gipl.imagepicker.exceptions;

public class ImageErrors extends Exception {
    public static final int PERMISSION_ERROR = 1231;
    public static final int DIR_ERROR = 1232;
    public static final int IMAGE_ERROR = 1233;
    public static final int IMAGE_PICK_CANCEL = 1234;
    private int nErrorType;

    public ImageErrors(String message) {
        super(message);
    }

    public ImageErrors(String message, int nErrorType) {
        super(message);
        this.nErrorType = nErrorType;
    }

    public int getErrorType() {
        return nErrorType;
    }
}
