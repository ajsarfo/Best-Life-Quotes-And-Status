package com.sarftec.lifequotesandstatus.presentation.adapter

import com.sarftec.lifequotesandstatus.databinding.LayoutQuoteBinding
import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.presentation.binding.FavoriteBinding

class FavoriteViewHolder(
    layoutBinding: LayoutQuoteBinding,
    dependency: QuoteAdapter.AdapterDependency,
    private val onDelete: (Quote) -> Unit
) : QuoteViewHolder(dependency, layoutBinding) {

    override fun bind(quote: Quote) {
        layoutBinding.binding = FavoriteBinding(dependency, quote, onDelete) {
            saveToGallery()
        }
        layoutBinding.executePendingBindings()
    }
}