package com.sarftec.lifequotesandstatus.presentation.advertisement

import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.InterstitialCallbacks
import com.sarftec.lifequotesandstatus.presentation.manager.NetworkManager

class InterstitialManager(
    private val activity: AppCompatActivity,
    private val networkManager: NetworkManager,
    pattern: List<Int>
) {
    private val adCounter = AdCountManager(pattern)
    private var callback: (() -> Unit)? = null

    init {
        config()
    }

    fun showAd(callback: (() -> Unit)?) {
        this.callback = callback
        if(!Appodeal.isPrecache(Appodeal.INTERSTITIAL)) Appodeal.cache(activity, Appodeal.INTERSTITIAL)
        if (!networkManager.isNetworkAvailable() || !adCounter.canShow()) {
            callback?.invoke()
        } else {
            if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) Appodeal.show(activity, Appodeal.INTERSTITIAL)
            else callback?.invoke()
        }
    }

    fun customShowAd(callback: (() -> Unit)) {
        this.callback = callback
        if(!Appodeal.isPrecache(Appodeal.INTERSTITIAL)) Appodeal.cache(activity, Appodeal.INTERSTITIAL)
        if (!networkManager.isNetworkAvailable()) {
            callback.invoke()
        } else {
            if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) Appodeal.show(activity, Appodeal.INTERSTITIAL)
            else callback.invoke()
        }
    }

    private fun config() {
        Appodeal.setInterstitialCallbacks(
            object : InterstitialCallbacks {
                override fun onInterstitialLoaded(p0: Boolean) {}

                override fun onInterstitialFailedToLoad() {
                }

                override fun onInterstitialShown() {}

                override fun onInterstitialShowFailed() {
                    callback?.invoke()
                }

                override fun onInterstitialClicked() {
                    callback?.invoke()
                }

                override fun onInterstitialClosed() {
                    callback?.invoke()
                }

                override fun onInterstitialExpired() {
                    callback?.invoke()
                }
            }
        )
    }

    companion object {
        fun runAppodealConfiguration() {
            Appodeal.disableWriteExternalStoragePermissionCheck()
            Appodeal.disableLocationPermissionCheck()
        }
    }
}