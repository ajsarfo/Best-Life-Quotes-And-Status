package com.sarftec.lifequotesandstatus.presentation.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.presentation.Dependency
import com.sarftec.lifequotesandstatus.presentation.advertisement.AdCountManager
import com.sarftec.lifequotesandstatus.presentation.advertisement.InterstitialManager
import com.sarftec.lifequotesandstatus.presentation.image.ImageStore
import com.sarftec.lifequotesandstatus.presentation.manager.NetworkManager
import com.sarftec.lifequotesandstatus.presentation.sound.SoundManager
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var imageStore: ImageStore

    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var networkManager: NetworkManager

    protected val dependency by lazy {
        Dependency(this,lifecycleScope, imageStore, soundManager)
    }

    protected val adRequestBuilder: AdRequest by lazy {
        AdRequest.Builder().build()
    }

    protected var interstitialManager: InterstitialManager? = null

    protected open fun canShowInterstitial() : Boolean = true

    protected open fun createAdCounterManager() : AdCountManager {
        return AdCountManager(listOf(1, 4, 3))
    }

    override fun onStart() {
        super.onStart()
        imageStore.reload()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Load interstitial if required by extending activity
        if(!canShowInterstitial()) return
        interstitialManager = InterstitialManager(
            this,
            getString(R.string.admob_interstitial_id),
            networkManager,
            createAdCounterManager(),
            adRequestBuilder
        )
        interstitialManager?.load()
    }

    protected fun <T> navigateTo(
        klass: Class<T>,
        finish: Boolean = false,
        slideIn: Int = R.anim.slide_in_right,
        slideOut: Int = R.anim.slide_out_left,
        parcel: Parcelable? = null
    ) {
        val intent = Intent(this, klass).also {
            it.putExtra(ACTIVITY_PARCEL, parcel)
        }
        startActivity(intent)
        if (finish) finish()
        overridePendingTransition(slideIn, slideOut)
    }

    fun statusColor(color: Int) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = color
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        const val ACTIVITY_PARCEL = "activity_parcel"
        val modifiedQuoteList = hashMapOf<Int, Boolean>()
    }
}