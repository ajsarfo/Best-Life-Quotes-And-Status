package com.sarftec.lifequotesandstatus.presentation.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.presentation.adapter.FavoriteAdapter
import com.sarftec.lifequotesandstatus.presentation.advertisement.AdCountManager
import com.sarftec.lifequotesandstatus.presentation.advertisement.BannerManager
import com.sarftec.lifequotesandstatus.presentation.parcel.QuoteToDetail
import com.sarftec.lifequotesandstatus.presentation.viewmodel.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteActivity : BaseListActivity() {

    override val viewModel by viewModels<FavoriteViewModel>()

    override fun createAdCounterManager(): AdCountManager {
        return AdCountManager(listOf(3, 2))
    }

    override val quoteAdapter by lazy {
        FavoriteAdapter(dependency, viewModel, readWriteHandler) { quote, imageHolder ->
            interstitialManager?.showAd {
                navigateTo(
                    DetailActivity::class.java,
                    parcel = QuoteToDetail(
                        quote.id,
                        quote.categoryId,
                        "Favorite Quotes",
                        imageHolder.getImageName(),
                        0,
                        QuoteToDetail.ORIGIN_FAVORITE
                    )
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*************** Admob Configuration ********************/
        BannerManager(this, adRequestBuilder).attachBannerAd(
            getString(R.string.admob_banner_favorite),
            layoutBinding.mainBanner
        )
        /**********************************************************/
    }

    override fun runInit(savedInstanceState: Bundle?) {

    }

    override fun runOnResume() {
        viewModel.resetQuoteFavorites()
    }
}