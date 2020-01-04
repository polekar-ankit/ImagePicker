package com.gipl.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.gipl.gallary.activities.AlbumSelectActivity;
import com.gipl.gallary.helpers.ConstantsCustomGallery;
import com.gipl.gallary.models.Image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;
import static com.gipl.imagepicker.MediaUtility.PROFILE_PHOTO;

/**
 * Creted by User on 17-Jan-19
 */
public class ImagePicker {

    private static final int CAMERA_REQUEST = 12;
    private static final int CAMERA_PERMISSION_REQUEST = 123;
    private static final int STORAGE_ACCESS_PERMISSION_REQUEST = 1234;
    private boolean fStoreInMyPath = false;
//    private String DIRECTORY = "";
    private String IMAGE_PATH = "";
    private Context activity;
    private Fragment fragment;
    private IImagePickerResult iImagePickerResult;
    private String sImgPath = "";
    private boolean isEnableMultiSelect;
    private int nMultiSelectCount = 1;
    ImagePicker(Context activity) {
        this.activity = activity;
    }


    void setnMultiSelectCount(int nMultiSelectCount) {
        this.nMultiSelectCount = nMultiSelectCount;
    }

    ImagePicker setStoreInMyPath(boolean fStoreInMyPath) {
        this.fStoreInMyPath = fStoreInMyPath;
        return this;
    }

//    ImagePicker setDIRECTORY(String DIRECTORY) {
//        this.DIRECTORY = DIRECTORY;
//        return this;
//    }

    ImagePicker setIMAGE_PATH(String IMAGE_PATH) {
        this.IMAGE_PATH = IMAGE_PATH;
        return this;
    }

    /**
     * If you pass directory name and image path then your can get String path as return
     * value other wise you will get BITMAP in onActivityResult
     *
     * @return String :  return complete image path if image path data is provided
     */
    void openCamera() {
        try {
            if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)
                    &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED
            ) {
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
                    photoFile = MediaUtility.FILE.createImageFile(activity, IMAGE_PATH);
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
                    iImagePickerResult.onError(new ImageErrors("Please provide Image Directory and Image path", ImageErrors.DIR_ERROR));
                }
            }
            openCamera(takePictureIntent);

        }
    }

    void startGallary() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if (isEnableMultiSelect) {
                Intent intent = new Intent(activity, AlbumSelectActivity.class);
                intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, nMultiSelectCount);
                ((AppCompatActivity) activity).startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                ((AppCompatActivity) activity).startActivityForResult(intent, PROFILE_PHOTO);
            }
        } else {
            startWriteStorageAccessPersmissionRequest();
        }
    }

    private void startWriteStorageAccessPersmissionRequest() {

        ActivityCompat.requestPermissions((Activity) activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_ACCESS_PERMISSION_REQUEST);
    }

    private boolean isDirAndPathProvided() {
        return /*!DIRECTORY.isEmpty() &&*/ !IMAGE_PATH.isEmpty();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        String sPath;
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = BitmapFactory.decodeFile(new File(sImgPath).getAbsolutePath());

                if (iImagePickerResult != null)
                    iImagePickerResult.onImageGet(new ImageResult(sImgPath, photo));
                return;

            }
            if (requestCode == PROFILE_PHOTO) {
                try {
                    if (data != null) {
                        sPath = MediaUtility.getFilePathFromUri(activity, data.getData());
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());
                        if (sPath.trim().isEmpty()) {
                            sPath = MediaUtility.getFilePathFromUri(activity, MediaUtility.insertImage(activity, bitmap));
                        }
                        iImagePickerResult.onImageGet(new ImageResult(sPath, bitmap));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    iImagePickerResult.onError(new ImageErrors("Unable get image try again", ImageErrors.IMAGE_ERROR));
                }
            }
            if (requestCode == ConstantsCustomGallery.REQUEST_CODE && data != null) {
                //The array list has the image paths of the selected images
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap;
                            Uri uri;
                            ArrayList<Image> imagesList = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
                            ArrayList<ImageResult> images = new ArrayList<>();
                            for (int i = 0; i < imagesList.size(); i++) {
                                uri = Uri.fromFile(new File(imagesList.get(i).path));
                                bitmap = BitmapFactory.decodeFile(new File(imagesList.get(i).path).getAbsolutePath());
//                            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                                images.add(new ImageResult(String.valueOf(uri), bitmap));
                            }

                            ((AppCompatActivity) activity).runOnUiThread(() -> iImagePickerResult.onReceiveImageList(images));

                        } catch (Exception e) {
                            ((AppCompatActivity) activity).runOnUiThread(() -> iImagePickerResult.onError(new ImageErrors(e.getMessage(), ImageErrors.IMAGE_PICK_CANCEL)));
                        }
                    }
                });
            }

        } else {
            iImagePickerResult.onError(new ImageErrors("Unable get image try again", ImageErrors.IMAGE_PICK_CANCEL));
        }


    }

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) activity, permissions[0])
                        || !ActivityCompat.shouldShowRequestPermissionRationale((Activity) activity, permissions[1])) {
                    iImagePickerResult.onError(new ImageErrors("Permission is disable by user", ImageErrors.PERMISSION_ERROR));

                } else {
                    openCamera();
                }
            }
        } else if (requestCode == STORAGE_ACCESS_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGallary();
            }
        }

    }

    void setFragment(Fragment fragment) {
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

    void setImagePickerResult(IImagePickerResult iImagePickerResult) {
        this.iImagePickerResult = iImagePickerResult;
    }

    void setEnableMultiSelect(boolean enableMultiSelect) {
        isEnableMultiSelect = enableMultiSelect;
    }

    protected interface IPickerDialogListener extends Parcelable {
        void onCancelClick();
    }

    public interface IImagePickerResult extends Parcelable {
        /**
         * for single image you  will receive result from this listener
         *
         * @param imageResult:object of image path and bitmap
         */
        void onImageGet(ImageResult imageResult);

        /**
         * for muti select you will receive result from this listener
         *
         * @param imageResults :array of ImageResult(image path and bitmap)
         */
        void onReceiveImageList(ArrayList<ImageResult> imageResults);

        void onError(ImageErrors imageErrors);

    }

    public class ImageErrors extends Exception {
        public static final int PERMISSION_ERROR = 1231;
        public static final int DIR_ERROR = 1232;
        public static final int IMAGE_ERROR = 1233;
        public static final int IMAGE_PICK_CANCEL = 1234;
        private int nErrorType;

        public ImageErrors(String message) {
            super(message);
        }

        ImageErrors(String message, int nErrorType) {
            super(message);
            this.nErrorType = nErrorType;
        }

        public int getErrorType() {
            return nErrorType;
        }
    }
}
