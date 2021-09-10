package com.sarftec.lifequotesandstatus.presentation.binding

class ShareDialogBinding(
    val title: String,
    val option1: String,
    val option2: String,
    val onOption1: () -> Unit,
    val onOption2: () -> Unit
)