package com.sarftec.lifequotesandstatus.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.lifequotesandstatus.databinding.LayoutDetailImageBinding
import com.sarftec.lifequotesandstatus.presentation.Dependency
import com.sarftec.lifequotesandstatus.presentation.image.ImageHolder

class DetailImageAdapter(
    private val dependency: Dependency,
    private val onClick: (ImageHolder) -> Unit
) : RecyclerView.Adapter<DetailImageViewHolder>() {

    private val items by lazy {
        dependency.imageStore.getQuoteImages()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailImageViewHolder {
        val layoutBinding = LayoutDetailImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DetailImageViewHolder(layoutBinding.root, onClick)
    }

    override fun onBindViewHolder(holder: DetailImageViewHolder, position: Int) {
     holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}