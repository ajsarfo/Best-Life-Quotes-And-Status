package com.sarftec.lifequotesandstatus.presentation.handler

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sarftec.lifequotesandstatus.presentation.utils.toast

class ReadWriteHandler(
    val activity: AppCompatActivity
) {

    private var permissionAction: (() -> Unit)? = null

    private val permissionRequestLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            isGranted[Manifest.permission.WRITE_EXTERNAL_STORAGE]?.let { writeGranted ->
                isGranted[Manifest.permission.READ_EXTERNAL_STORAGE]?.let { readGranted ->
                    if (writeGranted && readGranted) {
                        permissionAction?.invoke()
                        permissionAction = null
                    } else activity.toast(
                        "Allow storage permission to save images",
                        Toast.LENGTH_LONG
                    )
                }
            }

            /**
             * isGranted[Manifest.permission.CAMERA]?.let { takePictureGranted ->
            if (takePictureGranted) permissionAction?.invoke()
            }
             */
        }

    /**
     * This is used for reference purpose
     ****************************************
    fun requestTakePicture(action: () -> Unit) {
    val takePicturePermission = activity.packageManager.checkPermission(
    Manifest.permission.CAMERA,
    activity.packageName
    )
    if(takePicturePermission == PackageManager.PERMISSION_GRANTED) {
    action.invoke()
    }
    else {
    this.permissionAction = action
    permissionRequestLauncher.launch(
    arrayOf(Manifest.permission.CAMERA)
    )
    }
    }
     *****************************************
     */

    fun requestReadWrite(action: () -> Unit) : Boolean {
        val writePermission = activity.packageManager.checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            activity.packageName
        )
        val readPermission = activity.packageManager.checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            activity.packageName
        )
        return if (writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
            action.invoke()
            true
        } else {
            this.permissionAction = action
            permissionRequestLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
            false
        }
    }
}