package com.sarftec.lifequotesandstatus.presentation.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    override fun onBackPressed() {
        super.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val item = SplashManager(this).getItem()
        binding.splashMainText.apply {
            setTextColor(item.textColor)
            text = item.title
            item.typeface?.let {
                typeface = it
            }
        }
        binding.splashBottomText.apply {
            setTextColor(item.textColor)
            text = item.subtitle
            item.typeface?.let {
                typeface = it
            }
        }
        binding.splashImage.setBackgroundColor(item.backgroundColor)
        statusColor(item.backgroundColor)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            },
            3500L
        )
    }

    private class SplashManager(private val context: Context) {

        val fonts = arrayOf("bebas.otf", "comfortaa.ttf", "dephion.otf", "limerock.ttf")

        val message = arrayOf(
            "You can never plan the future by the past" to "Wisdom Quotes",
            "Speak when you are angry -- and you will make the best speech you'll ever regret" to "Anger Quotes",
            "The greatest difficulty always comes right before the birth of a dream" to "Difficulty Quotes",
            "Education is not the filling of a pail, but the lighting of a fire" to "Education Quotes",
            "Fake people will wish you the best just as long as the best benefits them" to "Fake People Quotes",
            "There is no revenge so complete as forgiveness" to "Forgiveness Quotes",
            "You will fail at 100% of the goals you don't set" to "Goal Setting Quotes",
            "The rich invest in time, the poor invest in money" to "Investment Quotes"
        )

        class Item(
            val typeface: Typeface?,
            val title: String,
            val subtitle: String,
            val textColor: Int,
            val backgroundColor: Int
        )

        fun getItem(): Item {
            val split = f23727b.random().split("@")
            val typeface = Typeface.createFromAsset(context.assets, "fonts/" + fonts.random())
            val pair = message.random()
            return Item(
                typeface = typeface,
                title = "“${pair.first}”",
                subtitle = "-${pair.second}-",
                textColor = m19660d(split[1]),
                backgroundColor = m19660d(split[0])
            )
        }

        /* renamed from: b */
        var f23727b = arrayOf(
            "#fdcd00@#26231c",
            "#1c1b21@#ffffff",
            "#3D155F@#DF678C",
            "#4831D4@#CCF381",
            "#317773@#E2D1F9",
            "#121c37@#ffa937",
            "#79bbca@#39324b",
            "#ffadb1@#202f34",
            "#373a3c@#e3b94d",
            "#e38285@#fbfdea",
            "#eebb2c@#6c2c4e",
            "#170e35@#94daef"
        )

        /* renamed from: c */
        fun m19659c(str: String): String {
            return if (str.contains("#")) str else "#$str"
        }

        /* renamed from: d */
        fun m19660d(str: String): Int {
            return Color.parseColor(m19659c(str))
        }
    }
}