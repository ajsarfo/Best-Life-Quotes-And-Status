package com.sarftec.lifequotesandstatus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

const val QUOTE_TABLE = "quote_table"

@Entity(
    tableName = QUOTE_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"]
        )
    ]
)
data class Quote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(index = true) val categoryId: Int,
    val message: String,
    var isFavorite: Boolean = false
)