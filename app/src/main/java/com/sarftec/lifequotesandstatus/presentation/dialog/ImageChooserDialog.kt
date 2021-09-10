package com.sarftec.lifequotesandstatus.presentation.dialog

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.sarftec.lifequotesandstatus.databinding.LayoutDetailImageDialogBinding
import com.sarftec.lifequotesandstatus.presentation.adapter.DetailImageAdapter

class ImageChooserDialog(
    parent: ViewGroup,
    imageAdapter: DetailImageAdapter,
) : AlertDialog(parent.context) {

    init {
        val layoutBinding = LayoutDetailImageDialogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).apply {
            imageRecycler.apply {
                layoutManager = GridLayoutManager(parent.context, 3)
                setHasFixedSize(true)
                adapter = imageAdapter
            }
        }
        setView(layoutBinding.root)
    }
}