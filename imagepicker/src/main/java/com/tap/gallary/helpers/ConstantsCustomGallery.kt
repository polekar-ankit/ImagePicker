package com.tap.gallary.helpers

/**
 * Created by Ankit on 03-11-2016.
 */
object ConstantsCustomGallery {
    const val PERMISSION_REQUEST_CODE = 1000
    const val PERMISSION_GRANTED = 1001
    const val PERMISSION_DENIED = 1002
    const val REQUEST_CODE = 2000
    const val FETCH_STARTED = 2001
    const val FETCH_COMPLETED = 2002
    const val ERROR = 2005

    /**
     * Request code for permission has to be < (1 << 8)
     * Otherwise throws java.lang.IllegalArgumentException: Can only use lower 8 bits for requestCode
     */
    const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 23
    const val INTENT_EXTRA_ALBUM = "album"
    const val INTENT_EXTRA_IMAGES = "images"
    const val INTENT_EXTRA_LIMIT = "limit"
    const val DEFAULT_LIMIT = 10

    //Maximum number of images that can be selected at a time
    @JvmField
    var limit = 0
}