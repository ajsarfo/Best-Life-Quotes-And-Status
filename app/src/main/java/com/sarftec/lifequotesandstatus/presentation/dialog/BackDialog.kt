package com.sarftec.lifequotesandstatus.presentation.dialog

import android.app.AlertDialog
import android.view.View
import com.sarftec.lifequotesandstatus.databinding.LayoutBackDialogBinding

class BackDialog(
    parent: View,
    val binding: LayoutBackDialogBinding,
    private val onYes: () -> Unit,
    private val onNo: () -> Unit
) : AlertDialog(parent.context) {

    init {
        with(binding) {
            cvYes.setOnClickListener {
                onYes()
            }
            cvNo.setOnClickListener {
                onNo()
            }
        }
       /*
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        */
        setView(binding.root)
    }

    fun showBackDialog() = show()
}