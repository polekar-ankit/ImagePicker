package com.gipl.imagepicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.gipl.imagepicker.listener.IImagePickerResult;
import com.gipl.imagepicker.listener.IPickerDialogListener;
import com.gipl.imagepicker.models.PickerConfiguration;
import com.gipl.imagepicker.resultwatcher.PickerResultObserver;

/**
 * Created by User on 25-Jan-19
 */
public class ImagePickerDialog extends DialogFragment {
    private ImagePicker imagePicker;
    private IImagePickerResult iImagePickerResult;
    private IPickerDialogListener pickerDialogListener;
    private PickerConfiguration pickerConfiguration;


    /**
     * User component activity context eg.FragmentActivity,AppCompactActivity.
     * don't pass Activity context it will throw class cast exception
     * @param context
     */

    public ImagePickerDialog(Context context) {
        imagePicker = new ImagePicker(context)
                .setIMAGE_PATH("AppImages")
                .setStoreInMyPath(true);
        FragmentActivity activity = (FragmentActivity) context;
        if(activity==null)
            throw new NullPointerException("Activity not found for attach dialog");
        PickerResultObserver pickerResultObserver= new PickerResultObserver(activity.getActivityResultRegistry());
        imagePicker.setPikcerResultOberver(pickerResultObserver);
        pickerResultObserver.setImagePicker(imagePicker);
        activity.getLifecycle().addObserver(pickerResultObserver);
    }

    public void display(PickerConfiguration pickerConfiguration) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("pickerConfig", pickerConfiguration);
        this.setArguments(bundle);
        show(((AppCompatActivity)imagePicker.getActivity()).getSupportFragmentManager(),"");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        pickerConfiguration = getArguments().getParcelable("pickerConfig");

        iImagePickerResult = pickerConfiguration.getImagePickerResult();
        imagePicker.setImagePickerResult(iImagePickerResult);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        pickerDialogListener = pickerConfiguration.getPickerDialogListener();
        imagePicker.setEnableMultiSelect(pickerConfiguration.isEnableMultiSelect());
        imagePicker.setnMultiSelectCount(pickerConfiguration.getMultiSelectImageCount());
        builder.setCancelable(pickerConfiguration.isfIsDialogCancelable());
        this.setCancelable(pickerConfiguration.isfIsDialogCancelable());


        if (pickerConfiguration.isIsSetCustomDialog()) {
            View view = inflater.inflate(R.layout.layout_custom_image_picker, null);
            builder.setView(view);
            setCustomView(view);
            setViewConfig(view);
        } else
            builder.setTitle("Image Picker")
                    .setItems(R.array.dialog_menus_image_picker,
                            (dialogInterface, position) -> {
                                switch (position) {
                                    case 0:
                                        imagePicker.openCamera();
                                        break;
                                    case 1:
                                        imagePicker.startGallary();
                                        break;
                                    case 2:
                                        ImagePickerDialog.this.dismiss();
                                        if (pickerDialogListener != null)
                                            pickerDialogListener.onCancelClick();
                                        break;
                                }
                            });

        return builder.create();
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
