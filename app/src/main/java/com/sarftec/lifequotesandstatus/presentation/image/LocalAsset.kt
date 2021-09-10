package com.sarftec.lifequotesandstatus.presentation.image

import android.content.Context
import android.net.Uri
import java.lang.Exception

class LocalAsset(context: Context) : Repository(context, "file:///android_asset") {

    override suspend fun load(
        folder: String
    ): Result<Unit> {
        return try {
            storeImageNames(folder)
            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(Exception("Cannot load images for folder => $folder"))
        }
    }

    override fun loadImagesNames(folder: String): List<String> {
        return context.assets.list(folder)!!.toList()
    }

    override fun getImageHolder(uri: Uri): ImageHolder {
        return ImageHolder.AssetImage(uri)
    }
}