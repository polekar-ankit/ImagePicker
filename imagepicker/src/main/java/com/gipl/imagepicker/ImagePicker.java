package com.gipl.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.gipl.imagepicker.MediaUtility.PROFILE_PHOTO;

/**
 * Creted by User on 17-Jan-19
 */
public class ImagePicker {

    private static final int CAMERA_REQUEST = 12;
    private static final int CAMERA_PERMISSION_REQUEST = 123;
    private boolean fStoreInMyPath = false;
    private String DIRECTORY = "";
    private String IMAGE_PATH = "";
    private Context activity;
    private Fragment fragment;
    private IImagePickerResult iImagePickerResult;
    private String sImgPath = "";
    private static final int STORAGE_ACCESS_PERMISSION_REQUEST = 1234;
    public ImagePicker(Context activity) {
        this.activity = activity;
    }

    public ImagePicker setStoreInMyPath(boolean fStoreInMyPath) {
        this.fStoreInMyPath = fStoreInMyPath;
        return this;
    }

    public ImagePicker setDIRECTORY(String DIRECTORY) {
        this.DIRECTORY = DIRECTORY;
        return this;
    }

    public ImagePicker setIMAGE_PATH(String IMAGE_PATH) {
        this.IMAGE_PATH = IMAGE_PATH;
        return this;
    }

    /**
     * If you pass directory name and image path then your can get String path as return
     * value other wise you will get BITMAP in onActivityResult
     *
     * @return String :  return complete image path if image path data is provided
     */
    public void openCamera() {
        try {
            if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)
                    &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        startCameraIntent();
            } else {
                startPermissonRequest();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startCameraIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (fStoreInMyPath) {
                if (isDirAndPathProvided()) {
                    File photoFile;
                    photoFile = MediaUtility.FILE.createImageFile(DIRECTORY, IMAGE_PATH);
                    sImgPath = photoFile.getAbsolutePath();
                    Uri photoURI;
                    if (Build.VERSION.SDK_INT >= 24) {
                        photoURI = FileProvider.getUriForFile(activity,
                                activity.getPackageName() + ".provider",
                                photoFile);
                    } else {
                        photoURI = Uri.fromFile(photoFile);
                    }
                    // Continue only if the File was successfully created
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } else {
                    iImagePickerResult.onError(new CameraErrors("Please provide Image Directory and Image path", CameraErrors.DIR_ERROR));
                }
            }
            openCamera(takePictureIntent);

        }
    }

    public void startGallary() {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            ((AppCompatActivity) activity).startActivityForResult(intent, PROFILE_PHOTO);
        }else {
            startWriteStorageAccessPersmissionRequest();
        }
    }

    private void startWriteStorageAccessPersmissionRequest(){

        ActivityCompat.requestPermissions((Activity) activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_ACCESS_PERMISSION_REQUEST);
    }
    private boolean isDirAndPathProvided() {
        return !DIRECTORY.isEmpty() && !IMAGE_PATH.isEmpty();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == CAMERA_REQUEST) {
                    Bitmap photo = null;
                    if (data != null)
                        photo = (Bitmap) data.getExtras().get("data");
                    if (iImagePickerResult != null)
                        iImagePickerResult.onImageGet(sImgPath, photo);

                    return;
                }
                if (requestCode == PROFILE_PHOTO) {
                    String sPath = MediaUtility.getFilePathFromUri(activity, data.getData());

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());
                    iImagePickerResult.onImageGet(sPath, bitmap);
                }

            } else {

                iImagePickerResult.onError(new CameraErrors("Unable get image try again", CameraErrors.IMAGE_ERROR));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) activity, permissions[0])
                        || !ActivityCompat.shouldShowRequestPermissionRationale((Activity) activity, permissions[1])) {
                    iImagePickerResult.onError(new CameraErrors("Permission is disable by user", CameraErrors.PERMISSION_ERROR));

                } else {
                    openCamera();
                }
            }
        }
        else if (requestCode == STORAGE_ACCESS_PERMISSION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startGallary();
            }
        }

    }


    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    private void openCamera(Intent takePictureIntent) {
//        if (fragment != null) {
//            fragment.startActivityForResult(takePictureIntent, CAMERA_REQUEST);
//        }
//        else
            ((Activity) activity).startActivityForResult(takePictureIntent, CAMERA_REQUEST);
    }

    private void startPermissonRequest() {
//        if (fragment != null) {
//            fragment.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    CAMERA_PERMISSION_REQUEST);
//            return;
//        }
        ActivityCompat.requestPermissions((Activity) activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CAMERA_PERMISSION_REQUEST);
    }

    void setiImagePickerResult(IImagePickerResult iImagePickerResult) {
        this.iImagePickerResult = iImagePickerResult;
    }



    protected interface IPickerDialogListener extends Parcelable {
        void onCancelClick();
    }


    public interface IImagePickerResult extends Parcelable {
        void onImageGet(String sPath, Bitmap bitmap);

        void onError(CameraErrors cameraErrors);

    }

    public class CameraErrors extends Exception {
        public static final int PERMISSION_ERROR = 1231;
        static final int DIR_ERROR = 1232;
        static final int IMAGE_ERROR = 1233;
        private int nErrorType;

        public CameraErrors(String message) {
            super(message);
        }

        CameraErrors(String message, int nErrorType) {
            super(message);
            this.nErrorType = nErrorType;
        }

        public int getErrorType() {
            return nErrorType;
        }
    }
}
