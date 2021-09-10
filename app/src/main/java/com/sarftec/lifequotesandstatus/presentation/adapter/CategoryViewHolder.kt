package com.sarftec.lifequotesandstatus.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.sarftec.lifequotesandstatus.databinding.LayoutCategoryBinding
import com.sarftec.lifequotesandstatus.model.Category
import com.sarftec.lifequotesandstatus.presentation.Dependency
import com.sarftec.lifequotesandstatus.presentation.binding.CategoryBinding

class CategoryViewHolder(
    private val layoutBinding: LayoutCategoryBinding,
    private val dependency: Dependency,
    private val onClick: (Category) -> Unit
) : RecyclerView.ViewHolder(layoutBinding.root){

    fun bind(category: Category) {
        val binding = CategoryBinding(category,dependency, onClick)
        layoutBinding.binding = binding
        layoutBinding.executePendingBindings()
    }
}