package com.sarftec.lifequotesandstatus.presentation.binding

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sarftec.lifequotesandstatus.BR
import com.sarftec.lifequotesandstatus.model.Category
import com.sarftec.lifequotesandstatus.presentation.Dependency
import com.sarftec.lifequotesandstatus.presentation.sound.SoundManager
import com.sarftec.lifequotesandstatus.presentation.utils.bindable

class CategoryBinding(
    val category: Category,
    private val dependency: Dependency,
    private val onClick: (Category) -> Unit
) : BaseObservable() {

    val text = category.name.replace(" ", "\n")

    @get:Bindable
    val holder by bindable(dependency.imageStore.getCategoryImage(category.name), BR.holder)

    fun clicked() {
        dependency.playSound(SoundManager.Sound.TAP)
        onClick(category)
    }
}