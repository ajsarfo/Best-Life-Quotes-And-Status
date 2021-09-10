package com.sarftec.lifequotesandstatus.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sarftec.lifequotesandstatus.databinding.LayoutQuoteBinding
import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.presentation.Dependency
import com.sarftec.lifequotesandstatus.presentation.handler.ReadWriteHandler
import com.sarftec.lifequotesandstatus.presentation.image.ImageHolder
import com.sarftec.lifequotesandstatus.presentation.viewmodel.BaseListViewModel

class FavoriteAdapter(
    dependency: Dependency,
    viewModel: BaseListViewModel,
    readWriteHandler: ReadWriteHandler,
    onClick: (Quote, ImageHolder) -> Unit
) : QuoteAdapter(dependency, viewModel,readWriteHandler, onClick) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val layoutBinding = LayoutQuoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FavoriteViewHolder(layoutBinding, adapterDependency) { quote ->
            val index = items.indexOfFirst { quote.id == it.id }
            if(index != -1) {
                notifyItemRemoved(index)
            }
        }
    }

    override fun resetQuoteFavorites(indexedFavorites: Set<Map.Entry<Int, Boolean>>) {

    }
}