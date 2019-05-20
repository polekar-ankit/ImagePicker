package com.gipl.imagepicker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * USE TO HANDLE MEDIA SUCH AS AUDIO,VIDEO,IMAGE
 */

public class MediaUtility {
    public static final int REQUEST_VIDEO_CAPTURE = 345;
    public static final int CAMERA_REQUEST = 12;
    public static final int PROFILE_PHOTO = 122;
    public static final int STORAGE_PERMISSION_REQUEST = 124;



    public static String getFilePathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();

            return picturePath != null ? picturePath : "";
        } else return "";
    }


    public static class FILE {

        public static void deleteMediaFile(File mFile) {
            if (mFile.exists()) {
                mFile.delete();
            }
        }

        static File createImageFile(String SECURE_DIR, String IMAGE_PATH) throws IOException {
            String sImageFileName = "JPEG_" + UUID.randomUUID().toString() + "_";
            File storageDir = new File(Environment.getExternalStorageDirectory() + "/" + SECURE_DIR + "/" + IMAGE_PATH);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            return File.createTempFile(
                    sImageFileName,  /* prefix */
                    EXTENSIONS.IMAGE,         /* suffix */
                    storageDir      /* directory */
            );
        }


        public static String getRealPathFromUri(Context context, Uri contentUri) {
            Cursor cursor = null;
            try {
                String[] imgData = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, imgData, null, null, null);
                int nColumnIndex;
                if (cursor != null) {
                    nColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(nColumnIndex);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return "";
        }
    }


    static class EXTENSIONS {
        static final String AUDIO = ".3gp";
        static final String IMAGE = ".jpg";
        static final String VIDEO = ".mp4";

    }

    public static class MediaType {
        public static final String IMAGE = "image/*";
        public static final String AUDIO = "audio/*";
        static final String VIDEO = "video/mp4";
    }

}
