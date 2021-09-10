package com.sarftec.lifequotesandstatus.data.repository

import com.sarftec.lifequotesandstatus.data.disk.Database
import com.sarftec.lifequotesandstatus.model.Category
import com.sarftec.lifequotesandstatus.model.Quote
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val database: Database
) : Repository {

    private var todayQuote: Quote? = null

    override suspend fun getCategories(): List<Category> {
       return database.categoryDao().categories()
    }

    override suspend fun getCategoryQuotes(categoryId: Int): List<Quote> {
        return database.quoteDao().quotes(categoryId)
    }

    override suspend fun getFavoriteQuotes(): List<Quote> {
        return database.quoteDao().favorites()
    }

    override suspend fun saveFavoriteQuote(quoteId: Int, isFavorite: Boolean) {
       return database.quoteDao().updateFavorite(quoteId, isFavorite)
    }

    override suspend fun getTodayQuote(): Quote {
        return todayQuote ?: database.quoteDao().randomQuote().also {
            todayQuote = it
        }
    }
}