package com.gipl.imagepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.DialogFragment
import com.gipl.imagepicker.listener.IImageResult
import com.gipl.imagepicker.listener.IPickerDialogListener
import com.gipl.imagepicker.models.PickerConfiguration
import com.gipl.imagepicker.resultwatcher.PickerResultObserver

/**
 * Created by Ankit on 25-Jan-19
 */
class ImagePickerDialog : DialogFragment() {
    private var imagePicker: ImagePicker? = null
    private var pickerDialogListener: IPickerDialogListener? = null
    private lateinit var pickerConfiguration: PickerConfiguration
    fun setPickerConfiguration(pickerConfiguration: PickerConfiguration) {
        this.pickerConfiguration = pickerConfiguration
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pickerResultObserver = PickerResultObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(pickerResultObserver)
        imagePicker = ImagePicker(requireContext())
            .setIMAGE_PATH("AppImages")
            .setStoreInMyPath(true)
        imagePicker?.setPikcerResultOberver(pickerResultObserver)
        pickerResultObserver.setImagePicker(imagePicker)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_custom_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val iImageResult: IImageResult? = pickerConfiguration.imagePickerResult
        imagePicker?.setImagePickerResult(iImageResult)
        imagePicker?.setImageListResult(pickerConfiguration.imageListResult)
        imagePicker?.setImagePickerError(pickerConfiguration.imagePickerError)
        pickerDialogListener = pickerConfiguration.pickerDialogListener
        imagePicker?.setEnableMultiSelect(pickerConfiguration.isEnableMultiSelect)
        imagePicker?.setMultiSelectCount(pickerConfiguration.multiSelectImageCount)
        isCancelable = pickerConfiguration.isfIsDialogCancelable() == true

        setCustomView(view)
        setViewConfig(view)

        imagePicker?.closeDialog?.observe(this) { dismiss() }
    }

    private fun setViewConfig(view: View) {
        val ivCamera = view.findViewById<ImageView>(R.id.iv_camera)
        val ivGallery = view.findViewById<ImageView>(R.id.iv_galray)
        val tvGallery = view.findViewById<TextView>(R.id.tv_gallery)
        val tvCamera = view.findViewById<TextView>(R.id.tv_camera)
        if (pickerConfiguration.cameraImageId != -1) {
            ivCamera.setImageResource(pickerConfiguration.cameraImageId)
        } else {
            DrawableCompat.setTint(ivCamera.drawable, pickerConfiguration.iconColor)
        }
        if (pickerConfiguration.galleryImageId != -1) {
            ivGallery.setImageResource(
                pickerConfiguration.galleryImageId
            )
        } else {
            DrawableCompat.setTint(ivGallery.drawable, pickerConfiguration.iconColor)
        }
        if (pickerConfiguration.galleryTitle.isNotEmpty()) {
            tvGallery.text =
                pickerConfiguration.galleryTitle
        }
        if (pickerConfiguration.cameraTitle.isNotEmpty()) {
            tvCamera.text =
                pickerConfiguration.cameraTitle
        }
        tvGallery.setTextColor(pickerConfiguration.textColor)
        tvCamera.setTextColor(pickerConfiguration.textColor)
        (view.findViewById<View>(R.id.tv_cancel) as TextView).setTextColor(pickerConfiguration.textColor)
        view.setBackgroundColor(pickerConfiguration.backGroundColor)
    }

    private fun setCustomView(view: View) {
        val llOpenCamera = view.findViewById<LinearLayout>(R.id.ll_camera)
        val llOpenGallery = view.findViewById<LinearLayout>(R.id.ll_gallery)
        val tvCancel = view.findViewById<TextView>(R.id.tv_cancel)
        llOpenGallery.setOnClickListener { view13: View? -> imagePicker?.startGallary() }
        llOpenCamera.setOnClickListener { view12: View? -> imagePicker?.openCamera() }
        tvCancel.setOnClickListener {
            pickerDialogListener?.onCancelClick()
            dismiss()
        }
    }
}