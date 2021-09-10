package com.sarftec.lifequotesandstatus.presentation.dialog

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sarftec.lifequotesandstatus.databinding.LayoutDetialDialogBinding
import com.sarftec.lifequotesandstatus.presentation.binding.ShareDialogBinding

class ShareDialog(
    parent: ViewGroup,
    val binding: ShareDialogBinding
) : AlertDialog(parent.context) {

    init {
        with(
            LayoutDetialDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) {
            binding = ImageSelectionDialogBinding()
            executePendingBindings()
            setView(root)
        }
    }

    inner class ImageSelectionDialogBinding(
        val shareBinding: ShareDialogBinding = binding
    ) {

        fun onOption1() {
            dismiss()
            shareBinding.onOption1()
        }

        fun onOption2() {
            shareBinding.onOption2()
            dismiss()
        }
    }
}