package com.sarftec.lifequotesandstatus.data.repository

import com.sarftec.lifequotesandstatus.model.Category
import com.sarftec.lifequotesandstatus.model.Quote

interface Repository {
    suspend fun getCategories() : List<Category>
    suspend fun getCategoryQuotes(categoryId: Int) : List<Quote>
    suspend fun getFavoriteQuotes() : List<Quote>
    suspend fun saveFavoriteQuote(quoteId: Int, isFavorite: Boolean)
    suspend fun getTodayQuote() : Quote
}