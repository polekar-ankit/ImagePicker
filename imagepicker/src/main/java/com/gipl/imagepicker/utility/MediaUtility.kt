package com.gipl.imagepicker.utility

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.gipl.imagepicker.utility.MediaUtility.EXTENSIONS
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

/**
 * USE TO HANDLE MEDIA SUCH AS AUDIO,VIDEO,IMAGE
 */
object MediaUtility {
    const val REQUEST_VIDEO_CAPTURE = 345
    const val CAMERA_REQUEST = 12
    const val PROFILE_PHOTO = 122
    const val STORAGE_PERMISSION_REQUEST = 124

    fun getBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                contentResolver,
                uri
            )

        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }

    @JvmStatic
    fun insertImage(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val cv = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
            val uri =
                inContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
            val fos = uri?.let { inContext.contentResolver.openOutputStream(it) }
            fos?.use { inImage.compress(Bitmap.CompressFormat.JPEG, 70, it) }
            cv.clear()
            cv.put(MediaStore.Video.Media.IS_PENDING, 0)
            uri?.let { inContext.contentResolver.update(it, cv, null, null) }
            return uri
        } else {
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path =
                MediaStore.Images.Media.insertImage(
                    inContext.contentResolver,
                    inImage,
                    "title",
                    null
                )
            return Uri.parse(path)
        }
    }

    @JvmStatic
    fun getFilePathFromUri(context: Context, uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(projection[0])
            val picturePath = cursor.getString(columnIndex) // returns null
            cursor.close()
            picturePath ?: ""
        } else ""
    }

    object FILE {
        fun deleteMediaFile(mFile: File) {
            if (mFile.exists()) {
                mFile.delete()
            }
        }

        // Below method SECURE_DIR is remove because from android version 10 for app storage privacy
        // change app image store location has been change to app private directory
        @JvmStatic
        @Throws(IOException::class)
        fun createImageFile(
            context: Context,  /*String SECURE_DIR,*/
            IMAGE_PATH: String
        ): File {
            val sImageFileName = "JPEG_" + UUID.randomUUID().toString() + "_"
            //            File storageDir = new File(Environment.getExternalStorageDirectory() + "/" + SECURE_DIR + "/" + IMAGE_PATH);
            val storageDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) /*+ "/" + SECURE_DIR*/
                    .toString() + "/" + IMAGE_PATH
            )
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            return File.createTempFile(
                sImageFileName,  /* prefix */
                EXTENSIONS.IMAGE,  /* suffix */
                storageDir /* directory */
            )
        }

        fun getRealPathFromUri(context: Context, contentUri: Uri?): String {
            var cursor: Cursor? = null
            try {
                val imgData = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(contentUri!!, imgData, null, null, null)
                val nColumnIndex: Int
                if (cursor != null) {
                    nColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    return cursor.getString(nColumnIndex)
                }
            } finally {
                cursor?.close()
            }
            return ""
        }
    }

    internal object EXTENSIONS {
        const val AUDIO = ".3gp"
        const val IMAGE = ".jpg"
        const val VIDEO = ".mp4"
    }

    object MediaType {
        const val IMAGE = "image/*"
        const val AUDIO = "audio/*"
        const val VIDEO = "video/mp4"
    }
}