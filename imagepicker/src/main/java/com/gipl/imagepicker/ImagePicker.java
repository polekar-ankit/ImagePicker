package com.gipl.imagepicker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;

import com.gipl.gallary.helpers.ConstantsCustomGallery;
import com.gipl.gallary.models.Image;
import com.gipl.imagepicker.exceptions.ImageErrors;
import com.gipl.imagepicker.listener.IImageListResult;
import com.gipl.imagepicker.listener.IImagePickerError;
import com.gipl.imagepicker.listener.IImageResult;
import com.gipl.imagepicker.models.ImageResult;
import com.gipl.imagepicker.resultwatcher.PickerResultObserver;
import com.gipl.imagepicker.utility.MediaUtility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;
import static com.gipl.imagepicker.utility.MediaUtility.PROFILE_PHOTO;

/**
 * Creted by User on 17-Jan-19
 */
public class ImagePicker {

    public static final int CAMERA_PERMISSION_REQUEST = 123;
    public static final int STORAGE_ACCESS_PERMISSION_REQUEST = 1234;
    private static final int CAMERA_REQUEST = 12;
    private final Context activity;
    private boolean fStoreInMyPath = false;
    private String IMAGE_PATH = "";
    private IImageResult iImageResult;
    private IImagePickerError iImagePickerError;
    private IImageListResult iImageListResult;
    private String sImgPath = "";
    private boolean isEnableMultiSelect;
    private int nMultiSelectCount = 1;
    private PickerResultObserver pickerResultObserver;
    private final MutableLiveData<Boolean> closeDialog = new MutableLiveData<>();

    ImagePicker(Context activity) {
        this.activity = activity;
    }

    public MutableLiveData<Boolean> getCloseDialog() {
        return closeDialog;
    }

    public void setImagePickerError(IImagePickerError iImagePickerError) {
        this.iImagePickerError = iImagePickerError;
    }

    public void setImageListResult(IImageListResult iImageListResult) {
        this.iImageListResult = iImageListResult;
    }

    void setMultiSelectCount(int nMultiSelectCount) {
        this.nMultiSelectCount = nMultiSelectCount;
    }

    ImagePicker setStoreInMyPath(boolean fStoreInMyPath) {
        this.fStoreInMyPath = fStoreInMyPath;
        return this;
    }

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
                pickerResultObserver.startPermissionForCamera();
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
                    iImagePickerError.onError(new ImageErrors("Please provide Image Directory and Image path", ImageErrors.DIR_ERROR));
                }
            }
            openCamera(takePictureIntent);

        }
    }

    public void setPikcerResultOberver(PickerResultObserver pickerResultObserver) {
        this.pickerResultObserver = pickerResultObserver;
    }

    void startGallary() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if (isEnableMultiSelect) {
                pickerResultObserver.startCustomGallery(nMultiSelectCount);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                if (intent.resolveActivity(activity.getPackageManager()) == null) {
                    nMultiSelectCount = 1;
                    pickerResultObserver.startCustomGallery(nMultiSelectCount);
                } else {
                    intent.setType("image/*");
                    pickerResultObserver.startSystemGallery(intent);
//                    ((AppCompatActivity) activity).startActivityForResult(intent, PROFILE_PHOTO);
                }
            }
        } else {

            pickerResultObserver.startPermissionForGallery();
        }
    }


    private boolean isDirAndPathProvided() {
        return /*!DIRECTORY.isEmpty() &&*/ !IMAGE_PATH.isEmpty();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        String sPath;
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Executors.newSingleThreadExecutor().submit(() -> {
                    Bitmap photo = BitmapFactory.decodeFile(new File(sImgPath).getAbsolutePath());
                    ((AppCompatActivity) activity).runOnUiThread(() -> {
                        if (iImageResult != null)
                            iImageResult.onImageGet(new ImageResult(sImgPath, photo));
                    });
                });

            } else {
                if (requestCode == PROFILE_PHOTO) {
                    try {
                        if (data != null) {
                            sPath = MediaUtility.getFilePathFromUri(activity, data.getData());
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());
                            if (sPath.trim().isEmpty()) {
                                sPath = MediaUtility.getFilePathFromUri(activity, MediaUtility.insertImage(activity, bitmap));
                            }
                            iImageResult.onImageGet(new ImageResult(sPath, bitmap));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        iImagePickerError.onError(new ImageErrors("Unable get image try again", ImageErrors.IMAGE_ERROR));
                    }
                }
                if (requestCode == ConstantsCustomGallery.REQUEST_CODE && data != null) {
                    //The array list has the image paths of the selected images
                    Executors.newSingleThreadExecutor().submit(() -> {
                        try {
                            Bitmap bitmap;
                            Uri uri;
                            ArrayList<Image> imagesList = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
                            ArrayList<ImageResult> images = new ArrayList<>();
                            if (imagesList != null) {
                                for (int i = 0; i < imagesList.size(); i++) {
                                    uri = Uri.fromFile(new File(imagesList.get(i).path));
                                    bitmap = BitmapFactory.decodeFile(new File(imagesList.get(i).path).getAbsolutePath());
                                    images.add(new ImageResult(imagesList.get(i).path, bitmap));
                                }
                            }

                            ((AppCompatActivity) activity).runOnUiThread(() -> {
                                if (images.size() > 1)
                                    iImageListResult.onReceiveImageList(images);
                                if (images.size() == 1)
                                    iImageResult.onImageGet(images.get(0));
                            });

                        } catch (Exception e) {
                            ((AppCompatActivity) activity).runOnUiThread(() -> iImagePickerError.onError(new ImageErrors(e.getMessage(), ImageErrors.IMAGE_PICK_CANCEL)));
                        }
                    });
                }
            }

        } else {
            iImagePickerError.onError(new ImageErrors("Unable get image try again", ImageErrors.IMAGE_PICK_CANCEL));
        }
        closeDialog.setValue(true);

    }


    public void onRequestPermissionsResult(Boolean[] grantResults, int requestCode) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0]
                    && grantResults[1]) {
                openCamera();
            } else {
                iImagePickerError.onError(new ImageErrors("Permission is disable by user", ImageErrors.PERMISSION_ERROR));
            }
        } else if (requestCode == STORAGE_ACCESS_PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0]) {
                startGallary();
            } else
                iImagePickerError.onError(new ImageErrors("Permission is disable by user", ImageErrors.PERMISSION_ERROR));
        }

    }


    private void openCamera(Intent takePictureIntent) {
        pickerResultObserver.startCamera(takePictureIntent);
    }


    void setImagePickerResult(IImageResult iImageResult) {
        this.iImageResult = iImageResult;
    }

    void setEnableMultiSelect(boolean enableMultiSelect) {
        isEnableMultiSelect = enableMultiSelect;
    }

}
