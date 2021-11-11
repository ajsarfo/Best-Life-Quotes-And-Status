package com.sarftec.lifequotesandstatus.presentation.advertisement

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.sarftec.lifequotesandstatus.presentation.manager.NetworkManager

class InterstitialManager(
    private val activity: AppCompatActivity,
    private val interstitialId: String,
    private val networkManager: NetworkManager,
    private val adCountManager: AdCountManager,
    private val adRequest: AdRequest = AdRequest.Builder().build()
) {

    private var callback: (() -> Unit)? = null

    private var interstitialAd: InterstitialAd? = null

    fun load() {
        if (interstitialAd != null) return
        InterstitialAd.load(activity, interstitialId, adRequest, getInterstitialLoadCallback())
    }

    private fun callCallback() {
        callback?.invoke()
        this.callback = null
    }

    fun customShowAd(callback: () -> Unit) {
        this.callback = callback
        if (networkManager.isNetworkAvailable()) {
            when (interstitialAd) {
                null -> {
                    load()
                    callCallback()
                    return
                }
                else -> interstitialAd?.let {
                    it.show(activity)
                    load()
                } ?: callCallback()
            }
        } else callCallback()
    }

    fun showAd(callback: (() -> Unit)) {
        this.callback = callback
        if (networkManager.isNetworkAvailable()) {
            when {
                interstitialAd == null -> {
                    load()
                    callCallback()
                    return
                }
                adCountManager.canShow() -> interstitialAd?.let {
                    it.show(activity)
                    load()
                } ?: callCallback()

                else -> callCallback()
            }
        } else callCallback()
    }

    private fun getFullScreenCallback(): FullScreenContentCallback {
        return object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                interstitialAd = null
                callback?.invoke()
                callback = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                interstitialAd = null
                Log.v("TAG", "Failed to show full screen content!!")
                callCallback()
            }
        }
    }

    private fun getInterstitialLoadCallback(): InterstitialAdLoadCallback {
        return object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.v("TAG", "Failed to load interstitial!!")
                interstitialAd = null
                callCallback()
            }

            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                interstitialAd = p0
                Log.v("TAG", "Interstitial add  loaded successfully!!")
                interstitialAd?.let { it.fullScreenContentCallback = getFullScreenCallback() }
            }
        }
    }
}