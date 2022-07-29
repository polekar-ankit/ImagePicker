package com.tap.imagepicker.resultwatcher

import android.content.Context
import androidx.activity.result.contract.ActivityResultContract
import android.content.Intent
import androidx.activity.result.ActivityResult
import com.tap.gallary.activities.AlbumSelectActivity
import com.tap.gallary.helpers.ConstantsCustomGallery

class PickerIntent : ActivityResultContract<Int, ActivityResult>() {
    override fun createIntent(context: Context, input: Int): Intent {
        val intent = Intent(context, AlbumSelectActivity::class.java)
        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
        return ActivityResult(resultCode, intent)
    }
}