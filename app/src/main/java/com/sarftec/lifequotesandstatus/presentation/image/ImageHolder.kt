package com.sarftec.lifequotesandstatus.presentation.image

import android.net.Uri

sealed class ImageHolder(val uri: Uri) {

    fun getImageName() : String {
        return uri.toString().substringAfterLast("/")
    }

    class AssetImage(
        uri: Uri,
        val allowPlaceHolder: Boolean = false
    ) : ImageHolder(uri)


     class FileImage(
         uri: Uri,
         val allowPlaceHolder: Boolean = false
     ) : ImageHolder(uri)


    object Empty : ImageHolder(Uri.parse("custom://"))
}