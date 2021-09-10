package com.sarftec.lifequotesandstatus.presentation.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.sarftec.lifequotesandstatus.databinding.LayoutAppPreparationDialogBinding

class PreparationDialog(context: Context) : AlertDialog(context) {

    init {
        setView(
            LayoutAppPreparationDialogBinding.inflate(
                LayoutInflater.from(context)
            ).root
        )
    }
}