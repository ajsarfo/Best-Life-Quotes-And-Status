package com.sarftec.lifequotesandstatus.data.disk

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.sarftec.lifequotesandstatus.editSettings
import com.sarftec.lifequotesandstatus.model.Category
import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.readSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class DataSetup @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: Database
) {

    private val quoteFiles by lazy {
        context.assets.list(QUOTE_FOLDER)!!
    }

    private fun getCategory(fileName: String): String {
        return fileName
            .substringBeforeLast("_")
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString()
            }
    }

    private fun getIndex(fileName: String): Int {
        return fileName.substringAfterLast("_").toInt()
    }

    suspend fun setup() {
        database.categoryDao().insert(
            quoteFiles.map {
                Category(
                    id = getIndex(it),
                    name = getCategory(it)
                )
            }
        )
        quoteFiles.forEach { fileName ->
            val quotes = context.assets
                .open("$QUOTE_FOLDER/$fileName")
                .bufferedReader()
                .useLines { sequence ->
                    sequence.map { line ->
                        Quote(
                            categoryId = getIndex(fileName),
                            message = line
                        )
                    }.toList()
                }
            database.quoteDao().insert(quotes)
        }
        //Mark the setup process as finished!!
        context.editSettings(isPrepared, true)
    }

    suspend fun isSetup(): Boolean {
        return context.readSettings(isPrepared, false).first()
    }

    companion object {
        const val QUOTE_FOLDER = "quote_files"
        val isPrepared = booleanPreferencesKey("setup_data_is_prepared")
    }
}