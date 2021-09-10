package com.sarftec.lifequotesandstatus.presentation.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import com.sarftec.lifequotesandstatus.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(@ApplicationContext private val context: Context) {

    enum class Sound(val resId: Int = -10, val volume: Float = 1f, val rate: Float = 1f) {
        TAP(R.raw.button_tap, 0.8f),
        SECONDARY_TAP(R.raw.tap),
        FAVORITE(R.raw.sound, 0.7f),
        CHANGE(R.raw.all, 0.9f)
    }

    private val soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val builder = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()
        SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(builder)
            .build()
    } else {
        SoundPool(2, AudioManager.STREAM_MUSIC, 0)
    }

    private val loadedSounds = EnumMap<Sound, Int>(Sound::class.java)

    init {
        Sound.values()
            .filter { it.resId != -10 }
            .forEach {sound ->
                loadedSounds[sound] = soundPool.load(context, sound.resId, 1)
            }
    }

    fun playSound(sound: Sound) {
        loadedSounds[sound]?.let {
            soundPool.play(it, sound.volume, sound.volume, sound.ordinal, 0, sound.rate)
        }
    }
}