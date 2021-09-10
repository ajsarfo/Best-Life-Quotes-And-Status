package com.sarftec.lifequotesandstatus.presentation.adapter

import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.lifequotesandstatus.presentation.image.ImageHolder
import com.sarftec.lifequotesandstatus.presentation.utils.glideLoad

class DetailImageViewHolder(
    private val imageView: AppCompatImageView,
    private val onClick: (ImageHolder) -> Unit
) : RecyclerView.ViewHolder(imageView) {

    fun bind(uri: ImageHolder) {
        imageView.glideLoad(uri)
        imageView.setOnClickListener {
            onClick(uri)
        }
    }
}