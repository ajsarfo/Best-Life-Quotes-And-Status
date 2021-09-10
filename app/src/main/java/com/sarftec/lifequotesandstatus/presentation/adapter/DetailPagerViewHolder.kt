package com.sarftec.lifequotesandstatus.presentation.adapter

import android.graphics.Paint
import android.graphics.Typeface
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.lifequotesandstatus.databinding.LayoutDetailPagerBinding
import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.presentation.viewmodel.PanelState
import com.sarftec.lifequotesandstatus.presentation.viewmodel.QuoteAlignment

class DetailPagerViewHolder(
    private val layoutBinding: LayoutDetailPagerBinding,
    private val onClick: () -> Unit
) : RecyclerView.ViewHolder(layoutBinding.root), DetailPagerAdapter.PagerHolderListener {

    fun bind(quote: Quote) {
        layoutBinding.pagerQuote.text = quote.message
        layoutBinding.clickView.setOnClickListener {
            onClick()
        }
    }

    override fun notifyPanelStateChanged(state: PanelState) {
        with(layoutBinding.pagerQuote) {
            setTextColor(state.color)
            if(state.size != -1f) textSize = state.size
            if(state.fontLocation.isNotEmpty()) typeface = Typeface.createFromAsset(itemView.context.assets, state.fontLocation)
            gravity = when(state.alignment) {
                QuoteAlignment.END -> Gravity.END
                QuoteAlignment.START ->  Gravity.START
                QuoteAlignment.CENTER -> Gravity.CENTER
            }
            isAllCaps = state.isAllCaps
            paintFlags = if (state.isUnderlined) Paint.UNDERLINE_TEXT_FLAG
            else paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
        }
    }
}