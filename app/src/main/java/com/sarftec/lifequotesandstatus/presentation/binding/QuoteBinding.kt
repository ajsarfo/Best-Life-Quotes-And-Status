package com.sarftec.lifequotesandstatus.presentation.binding

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sarftec.lifequotesandstatus.BR
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.presentation.adapter.QuoteAdapter
import com.sarftec.lifequotesandstatus.presentation.sound.SoundManager
import com.sarftec.lifequotesandstatus.presentation.utils.bindable
import com.sarftec.lifequotesandstatus.presentation.utils.copy
import com.sarftec.lifequotesandstatus.presentation.utils.share
import com.sarftec.lifequotesandstatus.presentation.utils.toast

open class QuoteBinding(
    protected val dependency: QuoteAdapter.AdapterDependency,
    protected val quote: Quote,
    private val saveToGallery: () -> Boolean
) : BaseObservable() {

    val text = quote.message

    @get:Bindable
    var saveIcon by bindable(R.drawable.ic_file_download, BR.saveIcon)

    @get:Bindable
    var saveText by bindable("Save", BR.saveText)

    @get:Bindable
    var favoriteIcon by bindable(R.drawable.ic_favorite_border, BR.favoriteIcon)

    @get:Bindable
    var favoriteText by bindable("Like", BR.favoriteText)

    @get:Bindable
    var imageHolder by bindable(dependency.imageHolderStore.getImageHolder(quote), BR.imageHolder)

    init {
        switchFavorite()
        switchSave()
    }

    private fun switchSave() {
        if(dependency.quoteSavedIdentifier.isSaved(quote)) {
            saveText = "Saved"
            saveIcon = R.drawable.ic_check_true
        }
        else {
            saveText = "Save"
            saveIcon = R.drawable.ic_file_download
        }
    }

    private fun switchFavorite() {
        if(quote.isFavorite) {
            favoriteText = "Liked"
            favoriteIcon = R.drawable.ic_favorite_red
        }
        else {
            favoriteText = "Like"
            favoriteIcon = R.drawable.ic_favorite_border
        }
    }

    fun onClicked() {
        dependency.dependency.playSound(SoundManager.Sound.TAP)
        dependency.onClick(quote)
    }

    open fun onFavorite() {
        quote.isFavorite = !quote.isFavorite
        if(quote.isFavorite)  dependency.dependency.playSound(SoundManager.Sound.FAVORITE)
        dependency.dependency.context.toast(
            if(quote.isFavorite)  "Added to favorites" else "Removed from favorites"
        )
        switchFavorite()
        dependency.viewModel.saveQuote(quote)
    }

    fun onSave() {
        dependency.dependency.playSound(SoundManager.Sound.SECONDARY_TAP)
        if(saveToGallery()) {
            dependency.quoteSavedIdentifier.saved(quote)
            switchSave()
        }
    }

    fun onCopy() {
        dependency.dependency.playSound(SoundManager.Sound.SECONDARY_TAP)
        dependency.dependency.context.apply {
            copy(quote.message)
            toast("Copied to clipboard")
        }
    }

    fun onShare() {
        dependency.dependency.playSound(SoundManager.Sound.SECONDARY_TAP)
        dependency.dependency.context.share(quote.message, "Share")
    }
}