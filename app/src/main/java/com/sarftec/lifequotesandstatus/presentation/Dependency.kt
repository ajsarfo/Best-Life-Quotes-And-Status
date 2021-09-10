package com.sarftec.lifequotesandstatus.presentation

import android.content.Context
import com.sarftec.lifequotesandstatus.presentation.image.ImageStore
import com.sarftec.lifequotesandstatus.presentation.sound.SoundManager
import com.sarftec.lifequotesandstatus.presentation.utils.isSoundEnabled
import com.sarftec.lifequotesandstatus.readSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class Dependency(
    val context: Context,
    val coroutineScope: CoroutineScope,
    val imageStore: ImageStore,
    private val soundManager: SoundManager
) {
    fun playSound(sound: SoundManager.Sound) {
        coroutineScope.launch {
            if(context.readSettings(isSoundEnabled, false).first()) {
                soundManager.playSound(sound)
            }
        }
    }
}