package com.sarftec.lifequotesandstatus.presentation.activity

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.appodeal.ads.Appodeal
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.databinding.ActivityDetailBinding
import com.sarftec.lifequotesandstatus.databinding.LayoutTextPanelBinding
import com.sarftec.lifequotesandstatus.editSettings
import com.sarftec.lifequotesandstatus.presentation.adapter.DetailImageAdapter
import com.sarftec.lifequotesandstatus.presentation.adapter.DetailPagerAdapter
import com.sarftec.lifequotesandstatus.presentation.advertisement.AdCountManager
import com.sarftec.lifequotesandstatus.presentation.advertisement.InterstitialManager
import com.sarftec.lifequotesandstatus.presentation.binding.ShareDialogBinding
import com.sarftec.lifequotesandstatus.presentation.dialog.ImageChooserDialog
import com.sarftec.lifequotesandstatus.presentation.dialog.ShareDialog
import com.sarftec.lifequotesandstatus.presentation.handler.ImageHandler
import com.sarftec.lifequotesandstatus.presentation.handler.ReadWriteHandler
import com.sarftec.lifequotesandstatus.presentation.image.ImageHolder
import com.sarftec.lifequotesandstatus.presentation.panel.TextPanelManager
import com.sarftec.lifequotesandstatus.presentation.parcel.QuoteToDetail
import com.sarftec.lifequotesandstatus.presentation.sound.SoundManager
import com.sarftec.lifequotesandstatus.presentation.utils.*
import com.sarftec.lifequotesandstatus.presentation.viewmodel.BackgroundOption
import com.sarftec.lifequotesandstatus.presentation.viewmodel.DetailViewModel
import com.sarftec.lifequotesandstatus.presentation.viewmodel.ToolbarState
import com.sarftec.lifequotesandstatus.readSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class DetailActivity : BaseActivity(), ColorPickerDialogListener {

    private val layoutBinding by lazy {
        ActivityDetailBinding.inflate(
            layoutInflater
        )
    }

    private val interstitialManager by lazy {
        InterstitialManager(
            this,
            networkManager,
            listOf(1, 3)
        )
    }

    private val viewModel by viewModels<DetailViewModel>()

    private val pagerAdapter by lazy {
        DetailPagerAdapter {
            if (viewModel.isPanelShown()) viewModel.toolbarIconClicked()
            else {
                dependency.playSound(SoundManager.Sound.CHANGE)
                viewModel.randomBackground()
            }
        }
    }

    private lateinit var readWriteHandler: ReadWriteHandler

    private lateinit var imageHandler: ImageHandler

    private val textPanelManager by lazy {
        TextPanelManager(
            lifecycleScope,
            viewModel,
            LayoutTextPanelBinding.inflate(
                layoutInflater,
                layoutBinding.textPanelContainer,
                true
            )
        )
    }

    private val shareDialog by lazy {
        ShareDialog(
            layoutBinding.root,
            ShareDialogBinding(
                "Share As",
                "Text",
                "Image",
                onOption1 = {
                    viewModel.getCurrentQuote()?.let {
                        share(it.message, "Share")
                    }
                },
                onOption2 = {
                    interstitialManager.customShowAd {
                        shareBackgroundImage()
                    }
                }
            )
        )
    }

    private val imageAdapter by lazy {
        DetailImageAdapter(dependency) {
            dependency.playSound(SoundManager.Sound.CHANGE)
            viewModel.setBackgroundImage(it)
            closeImageChooser()
        }
    }

    private val imageChooserDialog by lazy {
        ImageChooserDialog(layoutBinding.root, imageAdapter)
    }

    override fun onBackPressed() {
        if(canShowInterstitialAd()) interstitialManager.customShowAd {
            super.onBackPressed()
        }
        else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutBinding.root)
        Appodeal.setBannerViewId(R.id.main_banner)
        imageStore.reload()
        savedInstanceState ?: kotlin.run {
            intent?.getParcelableExtra<QuoteToDetail>(ACTIVITY_PARCEL)?.let {
                viewModel.setParcel(it)
            }
        }
        configureHandlers()
        configureActivity()
        configureViewPager()
        configureLayout()
        viewModel.fetch()
        viewModel.toolbarState.observe(this) { state ->
            when (state) {
                ToolbarState.EDIT -> {
                    layoutBinding.editIcon.setImageResource(R.drawable.ic_close)
                    textPanelManager.show()
                }
                else -> {
                    layoutBinding.editIcon.setImageResource(R.drawable.ic_edit)
                    textPanelManager.dismiss()
                }
            }
        }

        viewModel.panelState.observe(this) { state ->
            if (state.opacity != -1) layoutBinding.detailUpper.view.setBackgroundColor(state.opacity)
            makeBackgroundChanges(state.backgroundOption)
            pagerAdapter.changePanelState(state)
        }

        viewModel.background.observe(this) { state ->
            state.color?.let { color ->
                layoutBinding.detailUpper.background.setImageDrawable(
                    ColorDrawable(color)
                )
            }
            state.image?.let {
                val uri = (Uri.parse(it))
                layoutBinding.detailUpper.background.glideLoad(
                    if (state.isImageAsset) ImageHolder.AssetImage(uri)
                    else ImageHolder.FileImage(uri)
                )
            }
        }
        viewModel.currentQuote.observe(this) {
            layoutBinding.detailLower.ivLike.setImageResource(
                if (it.isFavorite) R.drawable.ic_favorite_red else R.drawable.ic_favorite_border
            )
        }
        viewModel.indexedQuotes.observe(this) {
            pagerAdapter.submitData(it.quotes)
            layoutBinding.detailUpper.pager.setCurrentItem(it.index, false)
        }
    }

    //Trivial
    private fun closeImageChooser() {
        imageChooserDialog.dismiss()
    }

    private fun configureActivity() {
        lifecycleScope.launchWhenCreated {
            val count = readSettings(ENTRANCE_COUNT, 0).first()
            if (count < 2) {
                editSettings(ENTRANCE_COUNT, count + 1)
                toast("TAP to change image")
            }
        }
        layoutBinding.toolbarTitle.text = viewModel.getToolbarTitle()
        layoutBinding.toolbar.setNavigationOnClickListener {
            dependency.playSound(SoundManager.Sound.TAP)
            onBackPressed()
        }
        layoutBinding.edit.setOnClickListener {
            dependency.playSound(SoundManager.Sound.CHANGE)
            viewModel.toolbarIconClicked()
            //chooseDialog.show()
        }
    }


    private fun configureHandlers() {
        readWriteHandler = ReadWriteHandler(this)
        imageHandler = ImageHandler(this)
    }

    private fun configureViewPager() {
        layoutBinding.detailUpper.pager.adapter = pagerAdapter
        layoutBinding.detailUpper.pager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.setCurrentQuoteIndex(position)
                    super.onPageSelected(position)
                }
            }
        )
    }

    private fun makeBackgroundChanges(option: BackgroundOption) {
        when (option) {
            BackgroundOption.COLOR -> {
                ColorPickerDialog
                    .newBuilder()
                    .show(this@DetailActivity)
                viewModel.neutralizeBackgroundOption()
            }
            BackgroundOption.GALLERY -> {
                imageHandler.pickImageFromGallery { isSuccess, uri ->
                    if (isSuccess && uri != null) viewModel.setBackgroundImage(
                        ImageHolder.FileImage(
                            uri
                        )
                    )
                }
                viewModel.neutralizeBackgroundOption()
            }
            BackgroundOption.IMAGES -> {
                imageChooserDialog.show()
                viewModel.neutralizeBackgroundOption()
            }
            else -> {
                //Nothing
            }
        }
    }

    private fun saveBackgroundImage() {
        dependency.playSound(SoundManager.Sound.SECONDARY_TAP)
        readWriteHandler.requestReadWrite {
            layoutBinding.detailUpper.captureFrame.toBitmap { bitmap ->
                saveBitmapToGallery(bitmap)?.let { imageUri ->
                    toast("Saved to gallery")
                    viewInGallery(imageUri)
                }
            }
        }
    }

    private fun shareBackgroundImage() {
        layoutBinding.detailUpper.captureFrame.toBitmap {
            shareImage(it)
        }
    }

    private fun configureLayout() {
        layoutBinding.detailLower.apply {
            ivShare.setOnClickListener {
                dependency.playSound(SoundManager.Sound.SECONDARY_TAP)
                shareDialog.show()
            }
            ivCopy.setOnClickListener {
                dependency.playSound(SoundManager.Sound.SECONDARY_TAP)
                viewModel.getCurrentQuote()?.let {
                    copy(it.message)
                    toast("Copied to clipboard")
                }
            }
            ivLike.setOnClickListener {
                viewModel.getCurrentQuote()?.let {
                    if(!it.isFavorite) dependency.playSound(SoundManager.Sound.FAVORITE)
                    toast(
                        if(!it.isFavorite) "Added to favorites" else "Removed from favorites"
                    )
                }
                viewModel.changeCurrentQuoteFavorite()
            }

            IvSave.setOnClickListener {
                saveBackgroundImage()
            }
        }
    }

    /**
     * Jaredrummler color picker listeners
     */
    override fun onColorSelected(dialogId: Int, color: Int) {
        viewModel.setBackgroundColor(color)
    }

    override fun onDialogDismissed(dialogId: Int) {

    }

    companion object {
        val ENTRANCE_COUNT = intPreferencesKey("detail_entrance_time")

        private val addCounter = AdCountManager(listOf(4))

        fun canShowInterstitialAd() : Boolean {
            return addCounter.canShow()
        }
    }
}