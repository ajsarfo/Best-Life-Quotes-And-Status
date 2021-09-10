package com.sarftec.lifequotesandstatus.presentation.panel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ItemBox<K>(
    val coroutineScope: CoroutineScope,
    val selectionFlow: MutableStateFlow<Int>,
    val listener: (K?) -> Unit
)

/*
*The Adapter class******************************************************
 */
abstract class ItemAdapter<T, K>(
    coroutineScope: CoroutineScope,
    var items: List<T>,
    configure: (K?) -> Unit
) : RecyclerView.Adapter<ItemViewHolder<T, K>>() {

    private val indexFlow = MutableStateFlow(0)

    protected val itemBox = ItemBox(
        coroutineScope,
        indexFlow,
        configure
    )

    private var isNew = true

    override fun onBindViewHolder(holder: ItemViewHolder<T, K>, position: Int) {
        holder.bind(items[position])
        holder.switch(position == indexFlow.value)
    }

    override fun getItemCount(): Int = items.size
}


/*
*The adapter holder class.......................................................
 */
abstract class ItemViewHolder<T, K>(
    private val itemBox: ItemBox<K>,
    view: View
) : RecyclerView.ViewHolder(view) {

    var job: Job? = null

    open fun init() {
        job = itemBox.coroutineScope.launch {
            itemBox.selectionFlow.collect {
                if(it != adapterPosition) switch(false)
            }
        }
    }

    protected fun clicked(position: Int) {
        itemBox.selectionFlow.value = position
        switch(true)
    }

    abstract fun bind(item: T)

    abstract fun switch(isOn: Boolean)
}