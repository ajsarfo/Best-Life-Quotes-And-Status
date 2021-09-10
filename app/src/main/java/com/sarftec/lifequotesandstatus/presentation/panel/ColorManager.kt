package com.sarftec.lifequotesandstatus.presentation.panel

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.databinding.LayoutColorCardBinding
import com.sarftec.lifequotesandstatus.databinding.LayoutTextPanelBinding
import kotlinx.coroutines.CoroutineScope

class ColorManager(
    private val coroutineScope: CoroutineScope,
    private val panelListener: PanelListener,
    layout: LayoutTextPanelBinding
) {

    init {
        layout.readersettingsThemeRvId.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = ColorItemAdapter(
                coroutineScope,
                context.resources.getStringArray(R.array.reader_colors).map {
                    Color.parseColor(it)
                }
            ) {
                if (it != null) {
                  panelListener.setColor(it)
                }
            }
        }
    }

    class ColorItemAdapter(
        coroutineScope: CoroutineScope,
        items: List<Int>,
        listener: (Int?) -> Unit
    ) : ItemAdapter<Int, Int?>(coroutineScope, items, listener) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ItemViewHolder<Int, Int?> {
            val binding = LayoutColorCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ColorItemViewHolder(binding, itemBox).also { it.init() }
        }

        inner class ColorItemViewHolder(
            private val binding: LayoutColorCardBinding,
            colorBox: ItemBox<Int?>
        ) : ItemViewHolder<Int, Int?>(colorBox, binding.root) {

            override fun init() {
                binding.colorView.setOnClickListener {
                    //call listener callback with value in item box here
                    binding.colorView.background?.let {
                        itemBox.listener((it as ColorDrawable).color)
                    }
                    clicked(adapterPosition) //renamed from bindingAdapterPosition
                }
                super.init()
            }

            override fun bind(item: Int) {
                binding.colorView.setBackgroundColor(item)
            }

            override fun switch(isOn: Boolean) {
                binding.colorCheck.visibility = if (isOn) View.VISIBLE else View.GONE
            }
        }
    }
}