package com.sarftec.lifequotesandstatus.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.sarftec.lifequotesandstatus.databinding.LayoutQuoteBinding
import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.presentation.binding.QuoteBinding
import com.sarftec.lifequotesandstatus.presentation.utils.saveBitmapToGallery
import com.sarftec.lifequotesandstatus.presentation.utils.toBitmap
import com.sarftec.lifequotesandstatus.presentation.utils.toast
import com.sarftec.lifequotesandstatus.presentation.utils.viewInGallery

open class QuoteViewHolder(
    protected val dependency: QuoteAdapter.AdapterDependency,
    protected val layoutBinding: LayoutQuoteBinding
) : RecyclerView.ViewHolder(layoutBinding.root) {

    open fun bind(quote: Quote) {
        layoutBinding.binding = QuoteBinding(dependency, quote) {
            saveToGallery()
        }
        layoutBinding.executePendingBindings()
    }

    protected open fun saveToGallery(): Boolean {
        var isSaved = false
        isSaved = dependency.readWriteHandler.requestReadWrite {
            layoutBinding.captureFrame.toBitmap { bitmap ->
                val uri = itemView.context.saveBitmapToGallery(bitmap)
                isSaved = true
                itemView.context.toast("Image Saved In Gallery")
                uri?.let {
                    itemView.context.viewInGallery(it)
                }
            }
        }
        return isSaved
    }
}