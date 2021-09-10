package com.sarftec.lifequotesandstatus.presentation.binding

import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.presentation.adapter.QuoteAdapter
import com.sarftec.lifequotesandstatus.presentation.sound.SoundManager
import com.sarftec.lifequotesandstatus.presentation.utils.toast

class FavoriteBinding(
    adapterDependency: QuoteAdapter.AdapterDependency,
    quote: Quote,
    private val onDelete: (Quote) -> Unit,
    private val saveToGallery: () -> Boolean,
) : QuoteBinding(adapterDependency, quote, saveToGallery) {

    override fun onFavorite() {
        dependency.dependency.playSound(SoundManager.Sound.SECONDARY_TAP)
        dependency.dependency.context.toast("Removed from favorites")
        onDelete(quote)
        dependency.viewModel.saveAndRemove(quote)
    }
}