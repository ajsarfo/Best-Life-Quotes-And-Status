package com.sarftec.lifequotesandstatus.presentation.utils

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.content.ContextCompat

fun Context.toast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, length).show()
}

fun Context.copy(text: String, label: String = "Copy") {
    ContextCompat.getSystemService(this, ClipboardManager::class.java)?.apply {
        val clip = ClipData.newPlainText(label, text)
        setPrimaryClip(clip)
    }
}

fun Context.share(text: String, header: String, newTask: Boolean = false) {

    ContextCompat.getSystemService(this, ClipboardManager::class.java)?.apply {
        val clip = ClipData.newPlainText("label", text)
        setPrimaryClip(clip)
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    Intent.createChooser(intent, header).apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(Intent()))
        if(newTask) { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        startActivity(this)
    }
}

fun Context.moreApps() {
    val webIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://search?q=sarftec&c=apps")
    )
    startActivity(webIntent)
}

fun Context.rateApp() {
    val appId = packageName
    val rateIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$appId")
    )
    var marketFound = false
    // find all applications able to handle our rateIntent
    // find all applications able to handle our rateIntent
    val otherApps = packageManager
        .queryIntentActivities(rateIntent, 0)
    for (otherApp in otherApps) {
        // look for Google Play application
        if (otherApp.activityInfo.applicationInfo.packageName
            == "com.android.vending"
        ) {
            val otherAppActivity = otherApp.activityInfo
            val componentName = ComponentName(
                otherAppActivity.applicationInfo.packageName,
                otherAppActivity.name
            )
            // make sure it does NOT open in the stack of your activity
            rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // task repeating if needed
            rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            // if the Google Play was already open in a search result
            //  this make sure it still go to the app page you requested
            rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // this make sure only the Google Play app is allowed to
            // intercept the intent
            rateIntent.component = componentName
            startActivity(rateIntent)
            marketFound = true
            break
        }
    }

    // if GP not present on device, open web browser

    // if GP not present on device, open web browser
    if (!marketFound) {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$appId")
        )
        startActivity(webIntent)
    }
}

fun Context.vibrate(milliseconds: Long = 12, amplitude: Int = 12) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, amplitude))
    } else {
        vibrator.vibrate(milliseconds)
    }
}