package com.gipl.pickersample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.gipl.imagepicker.ImagePickerDialog;
import com.gipl.imagepicker.exceptions.ImageErrors;
import com.gipl.imagepicker.listener.IImageListResult;
import com.gipl.imagepicker.listener.IImagePickerError;
import com.gipl.imagepicker.listener.IImageResult;
import com.gipl.imagepicker.listener.IPickerDialogListener;
import com.gipl.imagepicker.models.ImageResult;
import com.gipl.imagepicker.models.PickerConfiguration;
import com.gipl.pickersample.databinding.FragmentFirstBinding;

import java.io.File;
import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ImagePickerDialog imagePickerDialog;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerDialog = new ImagePickerDialog();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final PickerConfiguration pickerConfiguration = PickerConfiguration.build()
                .setTextColor(Color.parseColor("#000000"))
                .setIconColor(Color.parseColor("#000000"))
                .setBackGroundColor(Color.parseColor("#ffffff"))
                .setIsDialogCancelable(false)
                .enableMultiSelect(false)
//                .setMultiSelectImageCount(3)
                .setPickerDialogListener(() -> Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show())
                .setImagePickerError(this::setError)
                .setImageListResult(imageResults -> {
                    int count = imageResults.size();
                    setImagesList(imageResults);
                    Toast.makeText(requireContext(), "Found image list with " + count + " images Successfully added", Toast.LENGTH_SHORT).show();
                })
                .setImagePickerResult(imageResult -> setImage(imageResult.getsImagePath(), imageResult.getImageBitmap()));


        binding.buttonFirst.setOnClickListener(view1 -> {
            if (imagePickerDialog != null && imagePickerDialog.isVisible())
                imagePickerDialog.dismiss();
            pickerConfiguration.enableMultiSelect(false);
            imagePickerDialog.setPickerConfiguration(pickerConfiguration);
            imagePickerDialog.show(getChildFragmentManager(),"");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setImagesList(ArrayList<ImageResult> imagesList) {
        for (ImageResult imageResult : imagesList) {
            File file = new File(imageResult.getsImagePath());
            if (file.exists()) {
                Log.d("Files", "Exits" + imageResult.getsImagePath());
            }
        }
    }

    public void setImage(String sPath, Bitmap bitmap) {
        Toast.makeText(requireContext(),sPath,Toast.LENGTH_SHORT).show();
    }

    public void setError(ImageErrors imageErrors) {
        if (imageErrors.getErrorType() == ImageErrors.PERMISSION_ERROR) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
            alertDialog.setTitle("Camera permission deny!");
            alertDialog.setMessage("Camera will be available after enabling Camera and Storage permission from setting");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent();
                }
            });
            alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {

            });

            alertDialog.show();
        }
        Toast.makeText(requireContext(), imageErrors.getMessage(), Toast.LENGTH_SHORT).show();
    }

}