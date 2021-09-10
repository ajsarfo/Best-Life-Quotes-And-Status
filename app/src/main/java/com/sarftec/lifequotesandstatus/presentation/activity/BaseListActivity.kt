package com.sarftec.lifequotesandstatus.presentation.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.appodeal.ads.Appodeal
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.databinding.ActivityQuoteBinding
import com.sarftec.lifequotesandstatus.presentation.adapter.QuoteAdapter
import com.sarftec.lifequotesandstatus.presentation.handler.ReadWriteHandler
import com.sarftec.lifequotesandstatus.presentation.sound.SoundManager
import com.sarftec.lifequotesandstatus.presentation.viewmodel.BaseListViewModel

abstract class BaseListActivity : BaseActivity() {

    protected val layoutBinding: ActivityQuoteBinding by lazy {
        ActivityQuoteBinding.inflate(layoutInflater)
    }

    protected lateinit var readWriteHandler: ReadWriteHandler

    protected abstract val viewModel: BaseListViewModel

    protected abstract val quoteAdapter: QuoteAdapter

    protected abstract fun runInit(savedInstanceState: Bundle?)

    protected abstract fun runOnResume()

    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
        runOnResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutBinding.root)
        Appodeal.setBannerViewId(R.id.main_banner)
        imageStore.reload()
        readWriteHandler = ReadWriteHandler(this)
        runInit(savedInstanceState)
        configureActivity()
        configureAdapter()
        viewModel.fetch()
        viewModel.quote.observe(this) {
            quoteAdapter.submitData(it)
        }
    }

    private fun configureActivity() {
        layoutBinding.toolbar.setNavigationOnClickListener {
            dependency.playSound(SoundManager.Sound.TAP)
            onBackPressed()
        }
        layoutBinding.toolbar.title = viewModel.getToolbarTitle()
    }

    private fun configureAdapter() {
        layoutBinding.recyclerView.apply {
            adapter = quoteAdapter
            layoutManager = LinearLayoutManager(this@BaseListActivity)
            setHasFixedSize(false)
        }
    }
}