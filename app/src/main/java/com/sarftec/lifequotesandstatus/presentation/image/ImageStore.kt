package com.sarftec.lifequotesandstatus.presentation.image

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val assetImages: Repository = LocalAsset(context)

    private val imageFolders = listOf(
        CATEGORY_FOLDER,
        QUOTES_FOLDER,
        EXTRA_FOLDER
    )

    suspend fun loadImages() {
        imageFolders.forEach { folder ->
            assetImages.load(folder).onFailure { throw it }
        }
    }

    fun reload() {
        assetImages.reloadImages(*imageFolders.toTypedArray())
    }

    fun getCategoryImage(category: String): ImageHolder {
        return getResult(assetImages.getImage(category, CATEGORY_FOLDER)) {
            ImageHolder.Empty
        }
    }

    fun getHeaderImage(): ImageHolder {
        return getResult(assetImages.getImage("header.jpg", EXTRA_FOLDER)) {
            ImageHolder.Empty
        }
    }

    fun getRandomQuoteImage(): ImageHolder {
        return getResult(assetImages.getImages(QUOTES_FOLDER)) {
            listOf(ImageHolder.Empty)
        }.random()
    }

    fun getQuoteImages(): List<ImageHolder> {
        return getResult(assetImages.getImages(QUOTES_FOLDER)) {
            listOf(ImageHolder.Empty)
        }
    }

    fun getQuoteImage(imageName: String): ImageHolder {
        return getResult(assetImages.getImageForName(QUOTES_FOLDER, imageName)) {
            ImageHolder.Empty
        }
    }

    private fun <T> getResult(result: Result<T>, onFailure: (Exception) -> T): T {
        var value: T? = null
        result.onSuccess { value = it }
        result.onFailure { value = onFailure(Exception(it.message)) }
        return value!!
    }

    companion object {
        const val CATEGORY_FOLDER = "images_category"
        const val QUOTES_FOLDER = "images_quote"
        const val EXTRA_FOLDER = "images_extra"
    }
}