package com.sarftec.lifequotesandstatus.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.lifequotesandstatus.databinding.LayoutQuoteBinding
import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.presentation.Dependency
import com.sarftec.lifequotesandstatus.presentation.handler.ReadWriteHandler
import com.sarftec.lifequotesandstatus.presentation.image.ImageHolder
import com.sarftec.lifequotesandstatus.presentation.image.ImageStore
import com.sarftec.lifequotesandstatus.presentation.viewmodel.BaseListViewModel

open class QuoteAdapter(
    dependency: Dependency,
    viewModel: BaseListViewModel,
    readWriteHandler: ReadWriteHandler,
    onClick: (Quote, ImageHolder) -> Unit
) : RecyclerView.Adapter<QuoteViewHolder>() {

    protected var items: List<Quote> = emptyList()

    private val imageHolderStore = ImageHolderStore(dependency.imageStore)

    protected val adapterDependency = AdapterDependency(
        dependency,
        viewModel,
        readWriteHandler,
        imageHolderStore,
        QuoteSavedIdentifier()
    ) {
        onClick(it, imageHolderStore.getImageHolder(it))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val layoutBinding = LayoutQuoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuoteViewHolder(adapterDependency, layoutBinding)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    open fun resetQuoteFavorites(indexedFavorites: Set<Map.Entry<Int, Boolean>>) {
        indexedFavorites.forEach {
            items[it.key].isFavorite = it.value
            notifyItemChanged(it.key)
        }
    }

    fun submitData(items: List<Quote>) {
        this.items = items;
        notifyDataSetChanged()
    }

    inner class AdapterDependency(
        val dependency: Dependency,
        val viewModel: BaseListViewModel,
        val readWriteHandler: ReadWriteHandler,
        val imageHolderStore: ImageHolderStore,
        val quoteSavedIdentifier: QuoteSavedIdentifier,
        val onClick: (Quote) -> Unit
    )

    class ImageHolderStore(private val imageStore: ImageStore) {

        private val holderMap = HashMap<Int, ImageHolder>()

        fun getImageHolder(quote: Quote): ImageHolder {
            return holderMap.getOrPut(quote.id) {
                imageStore.getRandomQuoteImage()
            }
        }
    }

    class QuoteSavedIdentifier() {

        private val identifierMap = hashMapOf<Int, Boolean>()

        fun isSaved(quote: Quote) : Boolean {
            return identifierMap[quote.id] ?: false
        }

        fun saved(quote: Quote) {
            identifierMap[quote.id] = true
        }
    }
}