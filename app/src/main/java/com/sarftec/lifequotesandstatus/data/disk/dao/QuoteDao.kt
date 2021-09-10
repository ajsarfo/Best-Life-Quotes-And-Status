package com.sarftec.lifequotesandstatus.data.disk.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarftec.lifequotesandstatus.model.QUOTE_TABLE
import com.sarftec.lifequotesandstatus.model.Quote

@Dao
interface QuoteDao {

    @Query("select * from $QUOTE_TABLE where categoryId = :categoryId")
    suspend fun quotes(categoryId: Int) : List<Quote>

    @Query("select * from $QUOTE_TABLE order by random() limit 1")
    suspend fun randomQuote() : Quote

    @Query("select * from $QUOTE_TABLE where isFavorite = 1")
    suspend fun favorites() : List<Quote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quotes: List<Quote>)

    @Query("update $QUOTE_TABLE set isFavorite = :isFavorite where id = :quoteId")
    suspend fun updateFavorite(quoteId: Int, isFavorite: Boolean)
}