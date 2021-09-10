package com.sarftec.lifequotesandstatus.presentation.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import com.sarftec.lifequotesandstatus.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


fun Context.shareImage(bitmap: Bitmap) {
    val randomString = Calendar.getInstance().timeInMillis.toString()
    val file = File(cacheDir, "image_${randomString}.png")
    file.outputStream().buffered(1024).use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        it.flush()
    }
    file.setReadable(true, false)
    val adjustedUri =
        FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            file
        )
    val intent = Intent(Intent.ACTION_SEND).apply {
        action = Intent.ACTION_SEND
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, adjustedUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(Intent.createChooser(intent, "Share On..."))
}

fun View.toBitmap(callback: (Bitmap) -> Unit) {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    callback(bitmap)
}

private fun Context.savePicture(callback: (uri: Uri?, outputStream: OutputStream?) -> Unit) {
    val appName = getString(R.string.app_name).replace(" ", "_")
    val imageName = "${System.currentTimeMillis()}.jpg"
    var fos: OutputStream? = null
    var imageUri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val directory = "${Environment.DIRECTORY_PICTURES}/$appName"
        contentResolver?.let {
            val contentValue = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
            }
            imageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)
            fos = imageUri?.let { contentResolver.openOutputStream(it) }
        }
    } else {
        val directory =
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/$appName")
        if (!directory.exists()) directory.mkdirs()
        val image = File(directory, imageName)
        imageUri = Uri.fromFile(image)
        fos = FileOutputStream(image)
        //Scanning for file in pictures
        imageUri?.let {
            sendBroadcast(
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                    data = it
                }
            )
        }
    }
    callback(imageUri, fos)
    fos?.close()
}

fun Context.saveBitmapToGallery(bitmap: Bitmap): Uri? {
    var imageUri: Uri? = null
    savePicture { uri, outputStream ->
        imageUri = uri
        outputStream?.let {
            val compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            if (compressed) it.flush()
        }
    }
    return imageUri
}

fun Context.viewInGallery(imageUri: Uri) {
    val adjustedUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                imageUri.toString().substringAfterLast(Environment.DIRECTORY_PICTURES)
            )
        )
    } else {
        imageUri
    }

    startActivity(
        Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(adjustedUri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    )
}

/*
fun ImageView.glideLoad(imageHolder: ImageHolder) {
    when(imageHolder) {
        is ImageHolder.FileImage -> {
            val builder = Glide.with(this.context).load(File(imageHolder.uri.path))
           // if(imageHolder.allowPlaceHolder) builder.placeholder(R.drawable.loading)
            builder.into(this)
        }
        is ImageHolder.AssetImage -> {
            val builder = Glide.with(this.context).load(imageHolder.uri)
          //  if(imageHolder.allowPlaceHolder) builder.placeholder(R.drawable.loading)
            builder.into(this)
        }
        else -> {

        }
    }
}
 */