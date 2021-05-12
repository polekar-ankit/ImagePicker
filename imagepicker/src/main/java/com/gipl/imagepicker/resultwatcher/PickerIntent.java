package com.gipl.imagepicker.resultwatcher;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gipl.gallary.activities.AlbumSelectActivity;
import com.gipl.gallary.helpers.ConstantsCustomGallery;

import org.jetbrains.annotations.NotNull;

public class PickerIntent extends ActivityResultContract<Integer, ActivityResult> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Integer nMultiSelectCount) {
        Intent intent = new Intent(context, AlbumSelectActivity.class);
        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, nMultiSelectCount);
//        ((AppCompatActivity) activity).startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
        return intent;
    }

    @Override
    public ActivityResult parseResult(int resultCode, Intent intent) {
        return new ActivityResult(resultCode,intent);
    }
}
