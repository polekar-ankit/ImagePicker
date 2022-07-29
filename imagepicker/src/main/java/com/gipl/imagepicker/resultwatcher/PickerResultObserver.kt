package com.gipl.imagepicker.resultwatcher;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.gipl.gallary.helpers.ConstantsCustomGallery;
import com.gipl.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;

import static com.gipl.imagepicker.ImagePicker.CAMERA_PERMISSION_REQUEST;
import static com.gipl.imagepicker.ImagePicker.STORAGE_ACCESS_PERMISSION_REQUEST;
import static com.gipl.imagepicker.utility.MediaUtility.CAMERA_REQUEST;
import static com.gipl.imagepicker.utility.MediaUtility.PROFILE_PHOTO;

public class PickerResultObserver implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<Intent> mGetContent;
    private ActivityResultLauncher<String[]> mGetCameraPermission;
    private ActivityResultLauncher<Intent> mGetCameraContent;
    private ActivityResultLauncher<Integer> mGetFromCustom;
    private ActivityResultLauncher<String> mGetGalleryPermission;
    private ImagePicker imagePicker;

    public PickerResultObserver(ActivityResultRegistry registry) {
        this.mRegistry = registry;
    }

    public void setImagePicker(ImagePicker imagePicker) {
        this.imagePicker = imagePicker;
    }

    @Override
    public void onCreate(@NonNull @NotNull LifecycleOwner owner) {
        mGetContent = mRegistry.register("key", owner,
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (imagePicker != null)
                            imagePicker.onActivityResult(PROFILE_PHOTO, result.getResultCode(), result.getData());
                    }
                });
        mGetCameraContent = mRegistry.register("keyCamera", owner,
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (imagePicker != null)
                            imagePicker.onActivityResult(CAMERA_REQUEST, result.getResultCode(), result.getData());
                    }
                });
        mGetFromCustom = mRegistry.register("keyCustom", owner, new PickerIntent(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePicker.onActivityResult(ConstantsCustomGallery.REQUEST_CODE, result.getResultCode(), result.getData());
                    }
                });

        mGetCameraPermission = mRegistry.register("keyPermission",
                owner,
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean isCameraPermissionGranted = result.get(Manifest.permission.CAMERA);
                    boolean isWriteToExternal = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    imagePicker.onRequestPermissionsResult(new Boolean[]{
                                    isCameraPermissionGranted, isWriteToExternal
                            }, CAMERA_PERMISSION_REQUEST
                    );
                }
        );
        mGetGalleryPermission = mRegistry.register("keyGalleryPermission", owner,
                new ActivityResultContracts.RequestPermission(),
                isGranted -> imagePicker.onRequestPermissionsResult(new Boolean[]{
                                isGranted
                        }, STORAGE_ACCESS_PERMISSION_REQUEST
                ));

    }

    public void startSystemGallery(Intent intent) {
        mGetContent.launch(intent);
    }

    public void startCamera(Intent intent) {
        mGetCameraContent.launch(intent);
    }

    public void startCustomGallery(int nMultiSelectCount) {
        mGetFromCustom.launch(nMultiSelectCount);
    }

    public void startPermissionForCamera() {
        mGetCameraPermission.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    public void startPermissionForGallery() {
        mGetGalleryPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}
