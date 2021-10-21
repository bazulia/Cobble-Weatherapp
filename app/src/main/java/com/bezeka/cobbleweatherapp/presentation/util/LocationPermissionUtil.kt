package com.bezeka.cobbleweatherapp.presentation.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import com.bezeka.cobbleweatherapp.R

private const val REQUEST_CODE_FOREGROUND = 1

object LocationPermissionUtil {

    private fun Context.isPermissionGranted(permission: String): Boolean = ActivityCompat
        .checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private val Context.isFineLocationPermissionGranted
        get() = isPermissionGranted(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    private fun Activity.checkFineLocationPermission() {
        if (isFineLocationPermissionGranted) return

        val shouldShowFineLocationPermissionRationale = ActivityCompat
            .shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        if (shouldShowFineLocationPermissionRationale) {
            presentAlertDialog(
                R.string.dialog_fine_location_rationale_title,
                R.string.dialog_fine_location_rationale_description,
                R.string.yes_action,
            ) {
                requestLocationPermissions()
            }
        } else {
            requestLocationPermissions()
        }
    }

    private fun Activity.requestLocationPermissions() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_FOREGROUND
        )

    fun checkLocationPermissions(activity: Activity, action: () -> Unit) = with(activity) {
        if (isFineLocationPermissionGranted) {
            action()
            return
        }

        checkFineLocationPermission()
    }

    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        action: () -> Unit
    ) = with(activity) {
        when (requestCode) {
            REQUEST_CODE_FOREGROUND -> {
                if (!isFineLocationPermissionGranted) {
                    checkFineLocationPermission()
                    return
                }

                action()
            }
        }
    }

    private fun Activity.presentAlertDialog(
        @StringRes titleRes: Int,
        @StringRes messageRes: Int,
        @StringRes actionRes: Int,
        action: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveButton(
                actionRes
            ) { _, _ ->
                action.invoke()
            }
            .create()
            .show()
    }
}