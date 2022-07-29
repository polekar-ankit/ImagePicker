package com.gipl.imagepicker.exceptions

import java.lang.Exception

class ImageErrors : Exception {
    var errorType = 0
        private set

    private constructor(message: String?) : super(message) {}
    constructor(message: String?, nErrorType: Int) : super(message) {
        errorType = nErrorType
    }

    companion object {
        const val PERMISSION_ERROR = 1231
        const val DIR_ERROR = 1232
        const val IMAGE_ERROR = 1233
        const val IMAGE_PICK_CANCEL = 1234
    }
}