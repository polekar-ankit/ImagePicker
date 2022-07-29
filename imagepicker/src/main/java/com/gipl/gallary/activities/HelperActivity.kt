package com.gipl.gallary.activities

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.gipl.gallary.helpers.ConstantsCustomGallery
import com.google.android.material.snackbar.Snackbar
import com.gipl.imagepicker.R
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View

/**
 * Created by Ankit on 03-11-2016.
 */
open class HelperActivity : AppCompatActivity() {
    private lateinit var helperView: View
    private val maxLines = 4
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    protected fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted()
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                ConstantsCustomGallery.PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            showRequestPermissionRationale()
        } else {
            showAppPermissionSettings()
        }
    }

    private fun showRequestPermissionRationale() {
        val snackbar = Snackbar.make(
            helperView,
            getString(R.string.permission_info),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(R.string.permission_ok)) {
                ActivityCompat.requestPermissions(
                    this@HelperActivity,
                    permissions,
                    ConstantsCustomGallery.PERMISSION_REQUEST_CODE
                )
            }
        (snackbar.view
            .findViewById<View>(R.id.snackbar_text) as TextView).maxLines = maxLines
        snackbar.show()
    }

    private fun showAppPermissionSettings() {
        val snackbar = Snackbar.make(
            helperView,
            getString(R.string.permission_force),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(R.string.permission_settings)) {
                val uri = Uri.fromParts(
                    getString(R.string.permission_package),
                    this@HelperActivity.packageName,
                    null
                )
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.data = uri
                startActivityForResult(intent, ConstantsCustomGallery.PERMISSION_REQUEST_CODE)
            }
        (snackbar.view
            .findViewById<View>(R.id.snackbar_text) as TextView).maxLines = maxLines
        snackbar.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != ConstantsCustomGallery.PERMISSION_REQUEST_CODE || grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            permissionDenied()
        } else {
            permissionGranted()
        }
    }

    protected open fun permissionGranted() {}
    private fun permissionDenied() {
        hideViews()
        requestPermission()
    }

    protected open fun hideViews() {}
    protected fun setView(view: View) {
        this.helperView = view
    }
}