package com.sarftec.lifequotesandstatus.presentation.parcel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CategoryToQuote(
    val id: Int,
    val name: String
) : Parcelable