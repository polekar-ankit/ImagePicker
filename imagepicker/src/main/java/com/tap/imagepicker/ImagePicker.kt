package com.tap.imagepicker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.tap.gallary.helpers.ConstantsCustomGallery
import com.tap.gallary.models.Image
import com.tap.imagepicker.exceptions.ImageErrors
import com.tap.imagepicker.listener.IImageListResult
import com.tap.imagepicker.listener.IImagePickerErrorListener
import com.tap.imagepicker.listener.IImageResult
import com.tap.imagepicker.models.ImageResult
import com.tap.imagepicker.resultwatcher.PickerResultObserver
import com.tap.imagepicker.utility.MediaUtility.FILE.createImageFile
import com.tap.imagepicker.utility.MediaUtility.PROFILE_PHOTO
import com.tap.imagepicker.utility.MediaUtility.getBitmap
import com.tap.imagepicker.utility.MediaUtility.getFilePathFromUri
import com.tap.imagepicker.utility.MediaUtility.insertImage
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors

/**
 * Creted by Ankit on 17-Jan-19
 */
class ImagePicker internal constructor(private val activity: Context) {
    private var fStoreInMyPath = false
    private var IMAGE_PATH = ""
    private var iImageResult: IImageResult? = null
    private var iImagePickerErrorListener: IImagePickerErrorListener? = null
    private var iImageListResult: IImageListResult? = null
    private var sImgPath = ""
    private var isEnableMultiSelect = false
    private var nMultiSelectCount = 1
    private var pickerResultObserver: PickerResultObserver? = null
    val closeDialog = MutableLiveData<Boolean>()

    fun setImagePickerError(iImagePickerErrorListener: IImagePickerErrorListener?) {
        this.iImagePickerErrorListener = iImagePickerErrorListener
    }

    fun setImageListResult(iImageListResult: IImageListResult?) {
        this.iImageListResult = iImageListResult
    }

    fun setMultiSelectCount(nMultiSelectCount: Int) {
        this.nMultiSelectCount = nMultiSelectCount
    }

    fun setStoreInMyPath(fStoreInMyPath: Boolean): ImagePicker {
        this.fStoreInMyPath = fStoreInMyPath
        return this
    }

    fun setIMAGE_PATH(IMAGE_PATH: String): ImagePicker {
        this.IMAGE_PATH = IMAGE_PATH
        return this
    }

    /**
     * If you pass directory name and image path then your can get String path as return
     * value other wise you will get BITMAP in onActivityResult
     *
     * @return String :  return complete image path if image path data is provided
     */
    fun openCamera() {
        try {
            if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED)
//                &&
//                ContextCompat.checkSelfPermission(
//                    activity,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//                == PackageManager.PERMISSION_GRANTED
            ) {
                startCameraIntent()
            } else {
                pickerResultObserver!!.startPermissionForCamera()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun startCameraIntent() {

//        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (fStoreInMyPath) {
                if (isDirAndPathProvided) {
                    val photoFile: File
                    photoFile = createImageFile(activity, IMAGE_PATH)
                    sImgPath = photoFile.absolutePath
                    val photoURI: Uri
                    photoURI = if (Build.VERSION.SDK_INT >= 24) {
                        FileProvider.getUriForFile(
                            activity,
                            activity.packageName + ".provider",
                            photoFile
                        )
                    } else {
                        Uri.fromFile(photoFile)
                    }
                    // Continue only if the File was successfully created
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                } else {
                    iImagePickerErrorListener!!.onError(
                        ImageErrors(
                            "Please provide Image Directory and Image path",
                            ImageErrors.DIR_ERROR
                        )
                    )
                }
            }
            openCamera(takePictureIntent)
//        }
    }

    fun setPikcerResultOberver(pickerResultObserver: PickerResultObserver?) {
        this.pickerResultObserver = pickerResultObserver
    }

    fun startGallary() {
//        if (ContextCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//          ) == PackageManager.PERMISSION_GRANTED) {
//            if (isEnableMultiSelect) {
//                pickerResultObserver!!.startCustomGallery(nMultiSelectCount)
//            } else {
                val intent = Intent(Intent.ACTION_PICK)
//                if (intent.resolveActivity(activity.packageManager) == null) {
//                    nMultiSelectCount = 1
//                    pickerResultObserver?.startCustomGallery(nMultiSelectCount)
//                } else {
                    intent.type = "image/*"
                    pickerResultObserver?.startSystemGallery(intent)
//                }
//            }
//        } else {
//            pickerResultObserver?.startPermissionForGallery()
//        }
    }

    /*!DIRECTORY.isEmpty() &&*/
    private val isDirAndPathProvided: Boolean
        get() =/*!DIRECTORY.isEmpty() &&*/IMAGE_PATH.isNotEmpty()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var sPath: String
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Executors.newSingleThreadExecutor().submit {
                    val imageFile = File(sImgPath);
                    val photo = BitmapFactory.decodeFile(imageFile.absolutePath)
                    (activity as AppCompatActivity).runOnUiThread {
                        if (iImageResult != null) iImageResult!!.onImageGet(
                            ImageResult(sImgPath, photo,Uri.fromFile(imageFile))
                        )
                    }
                }
            } else {
                if (requestCode == PROFILE_PHOTO) {
                    try {
                        if (data?.data != null) {
                            sPath = getFilePathFromUri(activity, data.data)
                            val bitmap = getBitmap(
                                activity.contentResolver,
                                data.data!!
                            )
//                            if (sPath.trim { it <= ' ' }.isEmpty()) {
//                                sPath = getFilePathFromUri(
//                                    activity, insertImage(
//                                        activity, bitmap
//                                    )
//                                )
//                            }
                            iImageResult?.onImageGet(ImageResult(sPath, bitmap,data.data))
                        } else {
                            iImagePickerErrorListener?.onError(
                                ImageErrors(
                                    "Unable to select image",
                                    ImageErrors.IMAGE_ERROR
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        iImagePickerErrorListener?.onError(
                            ImageErrors(
                                "Unable get image try again",
                                ImageErrors.IMAGE_ERROR
                            )
                        )
                    }
                }
                if (requestCode == ConstantsCustomGallery.REQUEST_CODE && data != null) {
                    //The array list has the image paths of the selected images
                    Executors.newSingleThreadExecutor().submit {
                        try {
                            var bitmap: Bitmap?

                            val imagesList =
                                data.getParcelableArrayListExtra<Image>(ConstantsCustomGallery.INTENT_EXTRA_IMAGES)
                            val images = ArrayList<ImageResult?>()
                            if (imagesList != null) {
                                for (i in imagesList.indices) {
                                    bitmap =
                                        BitmapFactory.decodeFile(imagesList[i].path?.let { File(it).absolutePath })
                                    images.add(imagesList[i].path?.let { ImageResult(it, bitmap) })
                                }
                            }
                            (activity as AppCompatActivity).runOnUiThread {
                                if (isEnableMultiSelect)
                                    iImageListResult?.onReceiveImageList(images)
                                else
                                    iImageResult?.onImageGet(images[0])
                            }
                        } catch (e: Exception) {
                            (activity as AppCompatActivity).runOnUiThread {
                                iImagePickerErrorListener?.onError(
                                    ImageErrors(e.message, ImageErrors.IMAGE_PICK_CANCEL)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            iImagePickerErrorListener?.onError(
                ImageErrors(
                    "Unable get image try again",
                    ImageErrors.IMAGE_PICK_CANCEL
                )
            )
        }
        closeDialog.setValue(true)
    }

    fun onRequestPermissionsResult(grantResults: Array<Boolean>, requestCode: Int) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0]
                && grantResults[1]
            ) {
                openCamera()
            } else {
                iImagePickerErrorListener?.onError(
                    ImageErrors(
                        "Permission is disable by user",
                        ImageErrors.PERMISSION_ERROR
                    )
                )
            }
        } else if (requestCode == STORAGE_ACCESS_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() &&
                grantResults[0]
            ) {
                startGallary()
            } else iImagePickerErrorListener?.onError(
                ImageErrors(
                    "Permission is disable by user",
                    ImageErrors.PERMISSION_ERROR
                )
            )
        }
    }

    private fun openCamera(takePictureIntent: Intent) {
        pickerResultObserver?.startCamera(takePictureIntent)
    }

    fun setImagePickerResult(iImageResult: IImageResult?) {
        this.iImageResult = iImageResult
    }

    fun setEnableMultiSelect(enableMultiSelect: Boolean) {
        isEnableMultiSelect = enableMultiSelect
    }

    companion object {
        const val CAMERA_PERMISSION_REQUEST = 123
        const val STORAGE_ACCESS_PERMISSION_REQUEST = 1234
        private const val CAMERA_REQUEST = 12
    }
}