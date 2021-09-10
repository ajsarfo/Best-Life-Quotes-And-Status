package com.sarftec.lifequotesandstatus.presentation.panel

import com.sarftec.lifequotesandstatus.databinding.LayoutTextPanelBinding


class SizeManager(
    private val panelListener: PanelListener,
    layout: LayoutTextPanelBinding
) {

    private val counter = Counter(16, 48, 24)

    init {
        val textCounter = layout.readersettingsTextSize
        textCounter.text = counter.getValue().toString()
        panelListener.setSize(
            counter.getValue().toFloat()
        )

        layout.readersettingsSmallerText.setOnClickListener { _ ->
            counter.decrement()
            textCounter.text = counter.getValue().toString()
            panelListener.setSize(
                counter.getValue().toFloat()
            )
        }

        layout.readersettingsLargerText.setOnClickListener { _ ->
            counter.increment()
            textCounter.text = counter.getValue().toString()
            panelListener.setSize(
                counter.getValue().toFloat()
            )
        }
    }

    private class Counter(private var min: Int, private var max: Int, private var start: Int) {

        fun getValue(): Int = start

        fun increment() {
            if (start >= max) return
            start++
        }

        fun decrement() {
            if (start <= min) return
            start--
        }
    }
}