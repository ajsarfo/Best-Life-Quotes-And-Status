package com.sarftec.lifequotesandstatus.presentation.panel

import android.graphics.Color
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.databinding.LayoutTextPanelBinding
import kotlin.math.roundToInt


class OpacityManager(
    private val panelListener: PanelListener,
    private val layout: LayoutTextPanelBinding
) {

    private val blackOverlayArrays = layout.root
        .context
        .resources
        .getStringArray(R.array.black_overlay_array)
        .map {
            Color.parseColor(it)
        }

    private var iterator = 6

    private val increment = (100f / blackOverlayArrays.size.toFloat()).roundToInt()

    init {
        createText()
        layout.apply {
            readerBgOpacityIncreaseId.setOnClickListener {
                previousColor()
            }
            readerBgOpacityDecreaseId.setOnClickListener {
                nextColor()
            }
        }
    }

    private fun createText() {
        val customIterator = iterator + 1
        val value = if (customIterator * increment > 100) 100
        else if (iterator == 0) 0
        else customIterator * increment
        layout.readersettingsOpacityValText.text = "$value%"
    }

    private fun setColor(iterator: Int) {
        panelListener.setOpacity(blackOverlayArrays[iterator])
        createText()
    }

    private fun nextColor() {
        iterator++
        if (iterator >= blackOverlayArrays.size) {
            iterator = blackOverlayArrays.size - 1
        }
        setColor(iterator)
    }

    private fun previousColor() {
        iterator--
        if (iterator < 0) {
            iterator = 0
        }
        setColor(iterator)
    }
}