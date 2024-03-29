package com.tap.pickersample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tap.imagepicker.ImagePickerDialog;
import com.tap.imagepicker.exceptions.ImageErrors;
import com.tap.imagepicker.listener.IPickerDialogListener;
import com.tap.imagepicker.models.ImageResult;
import com.tap.imagepicker.models.PickerConfiguration;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ImageView cropImageView;
    private final ImagePickerDialog imagePickerDialog = new ImagePickerDialog();

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
                .enableMultiSelect(false)
//                .setMultiSelectImageCount(3)
                .setPickerDialogListener(new IPickerDialogListener() {
                    @Override
                    public void onCancelClick() {

                    }
                })
                .setImagePickerErrorListener(this::setError)
                .setImageListResult(imageResults -> {
                    int count = imageResults.size();
                    setImagesList(imageResults);
                    Toast.makeText(MainActivity.this, "Found image list with " + count + " images Successfully added", Toast.LENGTH_SHORT).show();
                })
                .setImagePickerResult(imageResult -> setImage(imageResult.getsImagePath(), imageResult.getImageBitmap()));


        findViewById(R.id.btn_open_camera).setOnClickListener(view -> {
            pickerConfiguration.enableMultiSelect(true);
            imagePickerDialog.show(getSupportFragmentManager(), "");
        });


        findViewById(R.id.btn_open_picker).setOnClickListener(view -> {
            if (imagePickerDialog != null && imagePickerDialog.isVisible())
                imagePickerDialog.dismiss();
            pickerConfiguration.enableMultiSelect(false);
            imagePickerDialog.show(getSupportFragmentManager(), "");
        });
    }


    public void setImagesList(ArrayList<ImageResult> imagesList) {
        for (ImageResult imageResult : imagesList) {
            File file = new File(imageResult.getsImagePath());
            if (file.exists()) {
                Log.d("Files", "Exits" + imageResult.getsImagePath());
            }
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        imagePickerDialog.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    public void setImage(String sPath, Bitmap bitmap) {
        if (!sPath.isEmpty()) {
            cropImageView.setImageURI(Uri.fromFile(new File(sPath)));
        } else
            cropImageView.setImageBitmap(bitmap);
    }

    public void setError(ImageErrors imageErrors) {
        if (imageErrors.getErrorType() == ImageErrors.PERMISSION_ERROR) {
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
