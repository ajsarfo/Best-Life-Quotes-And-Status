package com.sarftec.lifequotesandstatus.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.lifequotesandstatus.databinding.LayoutDetailPagerBinding
import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.presentation.viewmodel.PanelState

class DetailPagerAdapter(
    private val onClick: () -> Unit
) : RecyclerView.Adapter<DetailPagerViewHolder>() {

    private var items: List<Quote> = emptyList()

    private val panelStateListeners = hashSetOf<PagerHolderListener>()

    private var panelState: PanelState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailPagerViewHolder {
        val layoutBinding = LayoutDetailPagerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DetailPagerViewHolder(layoutBinding, onClick).also {
            panelStateListeners.add(it)
        }
    }

    override fun onBindViewHolder(holder: DetailPagerViewHolder, position: Int) {
        panelStateListeners.add(holder)
        holder.bind(items[position])
        panelState?.let {
            holder.notifyPanelStateChanged(it)
        }
    }

    override fun getItemCount(): Int = items.size

    fun changePanelState(state: PanelState) {
        panelState = state
        panelStateListeners.forEach {
            it.notifyPanelStateChanged(state)
        }
    }

    fun submitData(items: List<Quote>) {
        this.items = items
        notifyDataSetChanged()
    }

    interface PagerHolderListener {
        fun notifyPanelStateChanged(state: PanelState)
    }
}