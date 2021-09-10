package com.sarftec.lifequotesandstatus.presentation.utils

import android.widget.ImageView
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.sarftec.lifequotesandstatus.presentation.image.ImageHolder
import kotlin.reflect.KProperty

class Bindable<T : Any>(private var value: T, private val tag: Int) {
    operator fun <U : BaseObservable> getValue(ref: U, property: KProperty<*>): T = value
    operator fun <U : BaseObservable> setValue(ref: U, property: KProperty<*>, newValue: T) {
        value = newValue
        ref.notifyPropertyChanged(tag)
    }
}

fun <T : Any> bindable(value: T, tag: Int): Bindable<T> = Bindable(value, tag)

@BindingAdapter("icon")
fun setIcon(imageView: ImageView, drawable: Int) {
    imageView.setImageResource(drawable)
}

@BindingAdapter("holder")
fun setImage(imageView: ImageView, imageHolder: ImageHolder) {
    imageView.glideLoad(imageHolder)
}

fun ImageView.glideLoad(imageHolder: ImageHolder) {
    when(imageHolder) {
        /*
        is ImageHolder.FileImage -> {
            val builder = Glide.with(this.context).load(File(imageHolder.uri.path))
            // if(imageHolder.allowPlaceHolder) builder.placeholder(R.drawable.loading)
            builder.into(this)
        }
         */
        is ImageHolder.AssetImage -> {
            val builder = Glide.with(this.context).load(imageHolder.uri)
            //  if(imageHolder.allowPlaceHolder) builder.placeholder(R.drawable.loading)
            builder.into(this)
        }
        else -> {

        }
    }
}