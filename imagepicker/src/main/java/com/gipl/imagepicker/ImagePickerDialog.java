package com.gipl.imagepicker;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;

import com.gipl.imagepicker.listener.IImageResult;
import com.gipl.imagepicker.listener.IPickerDialogListener;
import com.gipl.imagepicker.models.PickerConfiguration;
import com.gipl.imagepicker.resultwatcher.PickerResultObserver;

/**
 * Created by User on 25-Jan-19
 */
public class ImagePickerDialog extends DialogFragment {
    private ImagePicker imagePicker;
    private IPickerDialogListener pickerDialogListener;
    private PickerConfiguration pickerConfiguration;

    public void setPickerConfiguration(PickerConfiguration pickerConfiguration) {
        this.pickerConfiguration = pickerConfiguration;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePicker = new ImagePicker(requireContext())
                .setIMAGE_PATH("AppImages")
                .setStoreInMyPath(true);
        PickerResultObserver pickerResultObserver = new PickerResultObserver(requireActivity().getActivityResultRegistry());
        imagePicker.setPikcerResultOberver(pickerResultObserver);
        pickerResultObserver.setImagePicker(imagePicker);
        getLifecycle().addObserver(pickerResultObserver);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_custom_image_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IImageResult iImageResult = pickerConfiguration.getImagePickerResult();
        imagePicker.setImagePickerResult(iImageResult);
        imagePicker.setiImageListResult(pickerConfiguration.getImageListResult());
        imagePicker.setiImagePickerError(pickerConfiguration.getImagePickerError());


        pickerDialogListener = pickerConfiguration.getPickerDialogListener();
        imagePicker.setEnableMultiSelect(pickerConfiguration.isEnableMultiSelect());
        imagePicker.setnMultiSelectCount(pickerConfiguration.getMultiSelectImageCount());


        setCancelable(pickerConfiguration.isfIsDialogCancelable());
        setCancelable(pickerConfiguration.isfIsDialogCancelable());

        setCustomView(view);
        setViewConfig(view);
    }


    private void setViewConfig(View view) {
        ImageView ivCamera = view.findViewById(R.id.iv_camera);
        ImageView ivGallery = view.findViewById(R.id.iv_galray);
        TextView tvGallery = view.findViewById(R.id.tv_gallery);
        TextView tvCamera = view.findViewById(R.id.tv_camera);

        if (pickerConfiguration.getCameraImageId() != -1) {
            ivCamera.setImageResource(pickerConfiguration.getCameraImageId());
        } else
            DrawableCompat.setTint(ivCamera.getDrawable(), pickerConfiguration.getIconColor());

        if (pickerConfiguration.getGalleryImageId() != -1)
            ivGallery.setImageResource(pickerConfiguration.getGalleryImageId());
        else
            DrawableCompat.setTint(ivGallery.getDrawable(), pickerConfiguration.getIconColor());

        if (!pickerConfiguration.getGalleryTitle().isEmpty())
            tvGallery.setText(pickerConfiguration.getGalleryTitle());
        if (!pickerConfiguration.getCameraTitle().isEmpty())
            tvCamera.setText(pickerConfiguration.getCameraTitle());

        tvGallery.setTextColor(pickerConfiguration.getTextColor());
        tvCamera.setTextColor(pickerConfiguration.getTextColor());
        ((TextView) view.findViewById(R.id.tv_cancel)).setTextColor(pickerConfiguration.getTextColor());

        view.setBackgroundColor(pickerConfiguration.getBackGroundColor());
    }


    private void setCustomView(View view) {
        LinearLayout llOpenCamera = view.findViewById(R.id.ll_camera);
        LinearLayout llOpenGallery = view.findViewById(R.id.ll_gallery);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);

        llOpenGallery.setOnClickListener(view13 -> {
            imagePicker.startGallary();
            dismiss();
        });
        llOpenCamera.setOnClickListener(view12 -> {
            imagePicker.openCamera();
            dismiss();
        });
        tvCancel.setOnClickListener(view1 -> {
            if (pickerDialogListener != null)
                pickerDialogListener.onCancelClick();
            dismiss();
        });
    }

}
