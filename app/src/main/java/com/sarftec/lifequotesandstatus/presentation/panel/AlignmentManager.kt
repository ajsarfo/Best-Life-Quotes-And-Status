package com.sarftec.lifequotesandstatus.presentation.panel

import com.sarftec.lifequotesandstatus.databinding.LayoutTextPanelBinding

class AlignmentManager(
    private val panelListener: PanelListener,
    layout: LayoutTextPanelBinding
) {
    enum class Position { CENTER, LEFT, RIGHT }
    enum class Option { YES, NO }

    data class State(
        var position: Position = Position.CENTER,
        var isAllCaps: Option = Option.NO,
        var isUnderlined: Option = Option.NO
    )

    init {
        layout.readerTextAlignemntLeftId.setOnClickListener { _->
            if(panelListener.getAlignment() == Position.LEFT) return@setOnClickListener
            panelListener.setAlignment(Position.LEFT)
        }
        layout.readerTextAlignemntRightId.setOnClickListener { _->
            if(panelListener.getAlignment() == Position.RIGHT) return@setOnClickListener
            panelListener.setAlignment(Position.RIGHT)
        }
        layout.readerTextAlignemntCenterId.setOnClickListener { _->
            if(panelListener.getAlignment() == Position.CENTER) return@setOnClickListener
            panelListener.setAlignment(Position.CENTER)
        }
        layout.readerTextAlignemntAllcapsId.setOnClickListener { _->
            val option = if(panelListener.isAllCaps() == Option.YES) Option.NO else Option.YES
            panelListener.setAllCaps(option)
        }
        layout.readerTextAlignemntUnderlineId.setOnClickListener { _->
            val option = if(panelListener.isUnderlined() == Option.YES) Option.NO else Option.YES
            panelListener.setUnderlined(option)
            /*
            textViews.forEach {
                it.paintFlags = if (isUnderline) Paint.UNDERLINE_TEXT_FLAG
                else it.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            }
             */
        }
    }
}