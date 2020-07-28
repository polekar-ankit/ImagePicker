package com.gipl.pickersample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.gipl.imagepicker.ImagePicker;
import com.gipl.imagepicker.ImagePickerDialog;
import com.gipl.imagepicker.ImageResult;
import com.gipl.imagepicker.PickerConfiguration;
import com.gipl.imagepicker.PickerListener;
import com.gipl.imagepicker.PickerResult;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ImageView cropImageView;
    private ImagePickerDialog imagePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cropImageView = findViewById(R.id.cropImageView);


        final PickerConfiguration pickerConfiguration = PickerConfiguration.build()
                .setTextColor(Color.parseColor("#000000"))
                .setIconColor(Color.parseColor("#000000"))
                .setBackGroundColor(Color.parseColor("#ffffff"))
                .setIsDialogCancelable(false)
                .enableMultiSelect(true)
                .setMultiSelectImageCount(2)
                .setPickerDialogListener(new PickerListener() {
                    @Override
                    public void onCancelClick() {
                        super.onCancelClick();
                        Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                })
                .setImagePickerResult(new PickerResult() {

                    @Override
                    public void onImageGet(ImageResult imageResult) {
                        super.onImageGet(imageResult);
                        setImage(imageResult.getsImagePath(), imageResult.getImageBitmap());
                    }

                    @Override
                    public void onError(ImagePicker.ImageErrors cameraErrors) {
                        super.onError(cameraErrors);
                        setError(cameraErrors);
                    }

                    @Override
                    public void onReceiveImageList(ArrayList<ImageResult> imageResults) {
                        super.onReceiveImageList(imageResults);
                        int count =  imageResults.size();
                        setImagesList(imageResults);
                        Toast.makeText(MainActivity.this, "Found image list with " + count+ " images Successfully added", Toast.LENGTH_SHORT).show();
                    }
                })
                .setSetCustomDialog(true);


        findViewById(R.id.btn_open_camera).setOnClickListener(view -> {
            pickerConfiguration.enableMultiSelect(true);
            imagePickerDialog = ImagePickerDialog.display(getSupportFragmentManager(), pickerConfiguration.setSetCustomDialog(true));
        });


        findViewById(R.id.btn_open_picker).setOnClickListener(view -> {
            if (imagePickerDialog != null && imagePickerDialog.isVisible())
                imagePickerDialog.dismiss();
            pickerConfiguration.enableMultiSelect(false);
            imagePickerDialog = ImagePickerDialog.display(getSupportFragmentManager(),
                    pickerConfiguration.setSetCustomDialog(false));
        });
    }


    public void setImagesList(ArrayList<ImageResult> imagesList) {
            for (ImageResult imageResult : imagesList) {
                File file =  new File(imageResult.getsImagePath());
                if (file.exists()){
                    Log.d("Files","Exits" + imageResult.getsImagePath());
                }
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePickerDialog.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePickerDialog.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setImage(String sPath, Bitmap bitmap) {
        if (!sPath.isEmpty()) {
            cropImageView.setImageURI(Uri.fromFile(new File(sPath)));
        } else
            cropImageView.setImageBitmap(bitmap);
    }

    public void setError(ImagePicker.ImageErrors imageErrors) {
        if (imageErrors.getErrorType() == ImagePicker.ImageErrors.PERMISSION_ERROR) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Camera permission deny!");
            alertDialog.setMessage("Camera will be available after enabling Camera and Storage permission from setting");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {

            });

            alertDialog.show();
        }
        Toast.makeText(MainActivity.this, imageErrors.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
