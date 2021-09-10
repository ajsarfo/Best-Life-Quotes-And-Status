package com.sarftec.lifequotesandstatus.presentation.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.sarftec.lifequotesandstatus.presentation.adapter.QuoteAdapter
import com.sarftec.lifequotesandstatus.presentation.advertisement.InterstitialManager
import com.sarftec.lifequotesandstatus.presentation.parcel.CategoryToQuote
import com.sarftec.lifequotesandstatus.presentation.parcel.QuoteToDetail
import com.sarftec.lifequotesandstatus.presentation.viewmodel.QuoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuoteActivity : BaseListActivity() {

    override val viewModel by viewModels<QuoteViewModel>()

    private val interstitialManager by lazy {
        InterstitialManager(
            this,
            networkManager,
            listOf(2, 3, 3)
        )
    }

    override val quoteAdapter by lazy {
        QuoteAdapter(dependency, viewModel, readWriteHandler) { quote, imageHolder ->
            interstitialManager.showAd {
                navigateTo(
                    DetailActivity::class.java,
                    parcel = QuoteToDetail(
                        quote.id,
                        quote.categoryId,
                        viewModel.getToolbarTitle() ?: "",
                        imageHolder.getImageName(),
                        viewModel.getSeed(),
                        QuoteToDetail.ORIGIN_QUOTE
                    )
                )
            }
        }
    }

    override fun runInit(savedInstanceState: Bundle?) {
        savedInstanceState ?: kotlin.run {
            intent?.getParcelableExtra<CategoryToQuote>(ACTIVITY_PARCEL)?.let {
                viewModel.setParcel(it)
            }
        }
    }

    override fun runOnResume() {
        quoteAdapter.resetQuoteFavorites(modifiedQuoteList.entries)
        modifiedQuoteList.clear()
    }
}