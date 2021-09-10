package com.sarftec.lifequotesandstatus.data.disk

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sarftec.lifequotesandstatus.data.disk.dao.CategoryDao
import com.sarftec.lifequotesandstatus.data.disk.dao.QuoteDao
import com.sarftec.lifequotesandstatus.model.Category
import com.sarftec.lifequotesandstatus.model.Quote

@Database(entities = [Category::class, Quote::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun quoteDao() : QuoteDao
    abstract fun categoryDao() : CategoryDao
}