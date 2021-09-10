package com.sarftec.lifequotesandstatus.presentation.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appodeal.ads.Appodeal
import com.appodeal.ads.Native
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.data.disk.DataSetup
import com.sarftec.lifequotesandstatus.databinding.ActivityMainBinding
import com.sarftec.lifequotesandstatus.databinding.LayoutBackDialogBinding
import com.sarftec.lifequotesandstatus.editSettings
import com.sarftec.lifequotesandstatus.presentation.adapter.CategoryAdapter
import com.sarftec.lifequotesandstatus.presentation.advertisement.InterstitialManager
import com.sarftec.lifequotesandstatus.presentation.dialog.BackDialog
import com.sarftec.lifequotesandstatus.presentation.manager.AppReviewManager
import com.sarftec.lifequotesandstatus.presentation.parcel.CategoryToQuote
import com.sarftec.lifequotesandstatus.presentation.parcel.QuoteToDetail
import com.sarftec.lifequotesandstatus.presentation.sound.SoundManager
import com.sarftec.lifequotesandstatus.presentation.utils.*
import com.sarftec.lifequotesandstatus.presentation.viewmodel.*
import com.sarftec.lifequotesandstatus.readSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(
            layoutInflater
        ).also {
            it.viewModel = viewModel
            it.executePendingBindings()
        }
    }

    val viewModel by viewModels<MainViewModel>()

    private val categoryAdapter by lazy {
        CategoryAdapter(dependency) {
           interstitialManager.showAd {
               navigateTo(
                   QuoteActivity::class.java,
                   parcel = CategoryToQuote(it.id, it.name)
               )
           }
        }
    }

    private var drawerCallback: (() -> Unit)? = null

    //Trivial and would be removed
    @Inject
    lateinit var dataSetup: DataSetup

    private val interstitialManager by lazy {
        InterstitialManager(
            this,
            networkManager,
            listOf(1, 3, 4, 3)
        )
    }

    private val backDialog by lazy {
        BackDialog(
            binding.root,
            LayoutBackDialogBinding.inflate(layoutInflater),
            onYes = {
                super.finish()
            },
            onNo = {
                closeBackDialog()
            }
        )
    }

    private val appReviewManager by lazy {
        AppReviewManager(this)
    }

    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
        modifiedQuoteList.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        /**************Set up appodeal configuration*****************/
        Appodeal.setBannerViewId(R.id.main_banner)
        Appodeal.initialize(
            this,
            getString(R.string.appodeal_id),
            Appodeal.BANNER_VIEW or Appodeal.INTERSTITIAL or Appodeal.NATIVE
        )
        Appodeal.setNativeAdType(Native.NativeAdType.NoVideo)
        Appodeal.cache(this, Appodeal.NATIVE, 1)
        /*************************************************************/
        setupNavDrawer()
        setupNavView()
        setupRecyclerView()
        setupButtons()
        observeLiveData()
        binding.materialToolbar.setOnMenuItemClickListener {
            if(it.itemId == R.id.review) {
                rateApp()
                true
            }
            else false
        }
        lifecycleScope.launchWhenCreated {
            configureNavViewSettings()
            appReviewManager.triggerReview()
        }
        viewModel.fetch()
    }

    //Trivial
    private fun closeBackDialog() {
       backDialog.dismiss()
    }

    private fun showBackDialog() {
        backDialog.showBackDialog(Appodeal.getNativeAds(1).firstOrNull())
    }

    private fun observeLiveData() {
        fun clearSearchField() {
            binding.categorySearch.apply {
                text.clear()
                clearFocus()
            }
            binding.dummyView.requestFocus()
            getSystemService(Context.INPUT_METHOD_SERVICE).let { service ->
                (service as InputMethodManager).hideSoftInputFromWindow(
                    binding.categorySearch.windowToken, 0
                )
            }
        }
        viewModel.categories.observe(this) {
            categoryAdapter.submitData(it)
        }
        viewModel.floatingVisibility.observe(this) {
            when (it) {
                is FloatingButtonVisibility.Visible -> {
                    if (it.isVisible) binding.fab.show() else binding.fab.hide()
                    viewModel.neutralizeFloatingButton()
                }
                else -> {
                }
            }
        }
        viewModel.searchCloseVisibility.observe(this) {
            when (it) {
                is SearchCloseVisibility.Visible -> {
                    binding.searchClose.visibility = if (it.isVisible) View.VISIBLE else View.GONE
                    viewModel.neutralizeSearchClose()
                }
                else -> {

                }
            }
        }
        viewModel.clearSearchField.observe(this) {
            when (it) {
                ClearSearchField.Clear -> {
                    clearSearchField()
                    viewModel.neutralizeSearchField()
                }
                else -> {
                }
            }
        }
        viewModel.onBackPressed.observe(this) {
            when (it) {
                OnBackPressed.Proceed -> {
                    showBackDialog()
                    viewModel.neutralizeBackPressed()
                }
                OnBackPressed.Clear -> {
                    clearSearchField()
                    viewModel.neutralizeSearchField()
                }
                else -> {
                }
            }
        }
    }

    private fun setupButtons() {
        binding.categorySearch.setOnFocusChangeListener { v, hasFocus ->
            viewModel.onSearchFieldHasFocus(hasFocus)
        }
        binding.searchClose.setOnClickListener {
            viewModel.onCloseSearchQuery()
        }
        binding.fab.setOnClickListener {
            dependency.playSound(SoundManager.Sound.SECONDARY_TAP)
            viewModel.onScrollUp()
            binding.recyclerView.post {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                viewModel.onScrollPositionChanged(oldScrollY)
            }
        } else {
            binding.recyclerView.setOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        viewModel.onScrollPositionChanged(dy)
                        super.onScrolled(recyclerView, dx, dy)
                    }
                }
            )
        }
    }

    private fun setupNavDrawer() {
        binding.navView.menu
        binding.materialToolbar.setNavigationOnClickListener {
            dependency.playSound(SoundManager.Sound.TAP)
            binding.navDrawer.openDrawer(GravityCompat.START)
        }
        binding.navDrawer.addDrawerListener(
            object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

                override fun onDrawerOpened(drawerView: View) {}

                override fun onDrawerClosed(drawerView: View) {
                    drawerCallback?.invoke()
                    drawerCallback = null
                }

                override fun onDrawerStateChanged(newState: Int) {}
            }
        )
    }

    private suspend fun configureNavViewSettings() {
        binding.navView.menu.findItem(R.id.sound)?.let {
            (it.actionView as SwitchCompat).apply {
                isChecked = readSettings(isSoundEnabled, true).first()
                setOnClickListener {
                    dependency.playSound(SoundManager.Sound.CHANGE)
                }
                setOnCheckedChangeListener { _, isChecked ->
                    lifecycleScope.launch {
                        editSettings(isSoundEnabled, isChecked)
                    }
                }
            }
        }
        binding.navView.menu.findItem(R.id.dark_mode)?.let {
            (it.actionView as SwitchCompat).apply {
                isChecked = readSettings(isDarkMode, false).first()
                setOnClickListener {
                    dependency.playSound(SoundManager.Sound.CHANGE)
                }
                setOnCheckedChangeListener { _, isChecked ->
                    lifecycleScope.launch {
                        editSettings(isDarkMode, isChecked)
                        AppCompatDelegate.setDefaultNightMode(
                            if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                            else AppCompatDelegate.MODE_NIGHT_NO
                        )
                        recreate()
                    }
                }
            }
        }
    }

    private fun setupNavView() {
        fun onDrawerCallback(callback: () -> Unit) {
            drawerCallback = callback
            binding.navDrawer.closeDrawer(GravityCompat.START)
        }
        binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.header_image)?.let {
            it.glideLoad(imageStore.getHeaderImage())
        }
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.quote_of_the_day -> {
                    dependency.playSound(SoundManager.Sound.TAP)
                    onDrawerCallback {
                       interstitialManager.customShowAd {
                           navigateTo(
                               DetailActivity::class.java,
                               parcel = QuoteToDetail(
                                   origin = QuoteToDetail.ORIGIN_MAIN,
                                   categoryTitle = "Quote Of The Day"
                               )
                           )
                       }
                    }
                    true
                }
                R.id.favorite -> {
                    dependency.playSound(SoundManager.Sound.TAP)
                    onDrawerCallback {
                        navigateTo(FavoriteActivity::class.java)
                    }
                    true
                }
                R.id.share_app -> {
                    dependency.playSound(SoundManager.Sound.TAP)
                    onDrawerCallback {
                        share(
                            "${getString(R.string.app_share_message)}\n\nhttps://play.google.com/store/apps/details?id=${packageName}",
                            "Share"
                        )
                    }
                    true
                }
                R.id.rate_us -> {
                    dependency.playSound(SoundManager.Sound.TAP)
                    onDrawerCallback {
                        rateApp()
                    }
                    true
                }
                R.id.contact_us -> {
                    dependency.playSound(SoundManager.Sound.TAP)
                    onDrawerCallback {
                        interstitialManager.customShowAd {
                            navigateTo(ContactActivity::class.java)
                        }
                    }
                    true
                }
                R.id.more_apps -> {
                    dependency.playSound(SoundManager.Sound.TAP)
                    onDrawerCallback {
                        moreApps()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }
}