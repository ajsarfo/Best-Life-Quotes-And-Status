package com.sarftec.lifequotesandstatus.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.lifequotesandstatus.databinding.LayoutCategoryBinding
import com.sarftec.lifequotesandstatus.model.Category
import com.sarftec.lifequotesandstatus.presentation.Dependency

class CategoryAdapter(
    private val dependency: Dependency,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryViewHolder>() {

    private var categories = emptyList<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
       val layoutBinding = LayoutCategoryBinding.inflate(
           LayoutInflater.from(parent.context),
           parent,
           false
       )
        return CategoryViewHolder(layoutBinding, dependency, onClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
      holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun submitData(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }
}