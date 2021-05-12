package com.gipl.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.gipl.gallary.helpers.ConstantsCustomGallery;
import com.gipl.gallary.models.Image;
import com.gipl.imagepicker.exceptions.ImageErrors;
import com.gipl.imagepicker.listener.IImagePickerResult;
import com.gipl.imagepicker.models.ImageResult;
import com.gipl.imagepicker.resultwatcher.PikcerResultOberver;
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

    private static final int CAMERA_REQUEST = 12;
    public static final int CAMERA_PERMISSION_REQUEST = 123;
    public static final int STORAGE_ACCESS_PERMISSION_REQUEST = 1234;
    private boolean fStoreInMyPath = false;
    //    private String DIRECTORY = "";
    private String IMAGE_PATH = "";
    private Context activity;
    private IImagePickerResult iImagePickerResult;
    private String sImgPath = "";
    private boolean isEnableMultiSelect;
    private int nMultiSelectCount = 1;
    private PikcerResultOberver pikcerResultOberver;

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
                pikcerResultOberver.startPermissionForCamera();
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

    public void setPikcerResultOberver(PikcerResultOberver pikcerResultOberver) {
        this.pikcerResultOberver = pikcerResultOberver;
    }

    void startGallary() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if (isEnableMultiSelect) {
                pikcerResultOberver.startCustomGallery(nMultiSelectCount);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                if (intent.resolveActivity(activity.getPackageManager()) == null) {
                    nMultiSelectCount = 1;
                    pikcerResultOberver.startCustomGallery(nMultiSelectCount);
                } else {
                    intent.setType("image/*");
                    pikcerResultOberver.startSystemGallery(intent);
//                    ((AppCompatActivity) activity).startActivityForResult(intent, PROFILE_PHOTO);
                }
            }
        } else {

            pikcerResultOberver.startPermissionForGallery();
        }
    }


    private boolean isDirAndPathProvided() {
        return /*!DIRECTORY.isEmpty() &&*/ !IMAGE_PATH.isEmpty();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        String sPath;
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Executors.newSingleThreadExecutor().submit(() -> {
                    Bitmap photo = BitmapFactory.decodeFile(new File(sImgPath).getAbsolutePath());
                    ((AppCompatActivity) activity).runOnUiThread(() -> {
                        if (iImagePickerResult != null)
                            iImagePickerResult.onImageGet(new ImageResult(sImgPath, photo));
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
                            iImagePickerResult.onImageGet(new ImageResult(sPath, bitmap));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        iImagePickerResult.onError(new ImageErrors("Unable get image try again", ImageErrors.IMAGE_ERROR));
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
                                    iImagePickerResult.onReceiveImageList(images);
                                if (images.size() == 1)
                                    iImagePickerResult.onImageGet(images.get(0));
                            });

                        } catch (Exception e) {
                            ((AppCompatActivity) activity).runOnUiThread(() -> iImagePickerResult.onError(new ImageErrors(e.getMessage(), ImageErrors.IMAGE_PICK_CANCEL)));
                        }
                    });
                }
            }

        } else {
            iImagePickerResult.onError(new ImageErrors("Unable get image try again", ImageErrors.IMAGE_PICK_CANCEL));
        }


    }


    public void onRequestPermissionsResult(Boolean[] grantResults, int requestCode, String[] permissions) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0]
                    && grantResults[1]) {
                openCamera();
            } else {
                iImagePickerResult.onError(new ImageErrors("Permission is disable by user", ImageErrors.PERMISSION_ERROR));
            }
        } else if (requestCode == STORAGE_ACCESS_PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0]) {
                startGallary();
            } else
                iImagePickerResult.onError(new ImageErrors("Permission is disable by user", ImageErrors.PERMISSION_ERROR));
        }

    }


    private void openCamera(Intent takePictureIntent) {
        pikcerResultOberver.startCamera(takePictureIntent);
    }


    void setImagePickerResult(IImagePickerResult iImagePickerResult) {
        this.iImagePickerResult = iImagePickerResult;
    }

    void setEnableMultiSelect(boolean enableMultiSelect) {
        isEnableMultiSelect = enableMultiSelect;
    }

}
