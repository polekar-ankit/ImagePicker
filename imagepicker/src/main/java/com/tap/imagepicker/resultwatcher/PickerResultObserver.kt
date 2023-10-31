package com.tap.imagepicker.resultwatcher

import android.Manifest
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.activity.result.ActivityResultLauncher
import android.content.Intent
import com.tap.imagepicker.ImagePicker
import androidx.lifecycle.LifecycleOwner
import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.tap.imagepicker.utility.MediaUtility
import com.tap.gallary.helpers.ConstantsCustomGallery

class PickerResultObserver(private val mRegistry: ActivityResultRegistry) :
    DefaultLifecycleObserver {
    private var mGetContent: ActivityResultLauncher<Intent>? = null
    private var mGetCameraPermission: ActivityResultLauncher<Array<String>>? = null
    private var mGetCameraContent: ActivityResultLauncher<Intent>? = null
    private var mGetFromCustom: ActivityResultLauncher<Int>? = null
    private var mGetGalleryPermission: ActivityResultLauncher<String>? = null
    private var imagePicker: ImagePicker? = null
    private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null
    fun setImagePicker(imagePicker: ImagePicker?) {
        this.imagePicker = imagePicker
    }

    override fun onCreate(owner: LifecycleOwner) {
        mGetContent = mRegistry.register(
            "key", owner,
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (imagePicker != null) imagePicker!!.onActivityResult(
                    MediaUtility.PROFILE_PHOTO,
                    result.resultCode,
                    result.data
                )
            }
        }
        mGetCameraContent = mRegistry.register(
            "keyCamera", owner,
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (imagePicker != null) imagePicker!!.onActivityResult(
                    MediaUtility.CAMERA_REQUEST,
                    result.resultCode,
                    result.data
                )
            }
        }
        mGetFromCustom = mRegistry.register(
            "keyCustom", owner, PickerIntent()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagePicker!!.onActivityResult(
                    ConstantsCustomGallery.REQUEST_CODE,
                    result.resultCode,
                    result.data
                )
            }
        }
        mGetCameraPermission = mRegistry.register(
            "keyPermission",
            owner,
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result: Map<String, Boolean> ->
            val isCameraPermissionGranted = result[Manifest.permission.CAMERA]!!
            val isWriteToExternal = result[Manifest.permission.WRITE_EXTERNAL_STORAGE]!!
            imagePicker!!.onRequestPermissionsResult(
                arrayOf(
                    isCameraPermissionGranted, isWriteToExternal
                ), ImagePicker.CAMERA_PERMISSION_REQUEST
            )
        }
        mGetGalleryPermission = mRegistry.register(
            "keyGalleryPermission", owner,
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            imagePicker!!.onRequestPermissionsResult(
                arrayOf(
                    isGranted
                ), ImagePicker.STORAGE_ACCESS_PERMISSION_REQUEST
            )
        }

        pickMedia = mRegistry.register(
            "keyGallery", ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            val intent = Intent()
            intent.data = uri
            imagePicker?.onActivityResult(
                MediaUtility.PROFILE_PHOTO,
                Activity.RESULT_OK,
                intent
            )
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    }


    fun startSystemGallery(intent: Intent) {
//        mGetContent?.launch(intent)
        pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun startCamera(intent: Intent) {
        mGetCameraContent?.launch(intent)
    }

    fun startCustomGallery(nMultiSelectCount: Int) {
        mGetFromCustom?.launch(nMultiSelectCount)
    }

    fun startPermissionForCamera() {
        mGetCameraPermission?.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    fun startPermissionForGallery() {
        mGetGalleryPermission?.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}