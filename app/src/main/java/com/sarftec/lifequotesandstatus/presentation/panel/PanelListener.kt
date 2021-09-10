package com.sarftec.lifequotesandstatus.presentation.panel

interface PanelListener {
    fun setFont(fontLocation: String)
    fun setColor(color: Int)
    fun setOpacity(color: Int)
    fun setSize(size: Float)
    /*****************************/
    fun getAlignment() : AlignmentManager.Position
    fun isAllCaps() : AlignmentManager.Option
    fun isUnderlined() : AlignmentManager.Option

    fun setAlignment(position: AlignmentManager.Position)
    fun setAllCaps(option: AlignmentManager.Option)
    fun setUnderlined(option: AlignmentManager.Option)

    fun chooseBackground(chooser: BackgroundManager.Chooser)
}