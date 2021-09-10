package com.sarftec.lifequotesandstatus.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.data.disk.DataSetup
import com.sarftec.lifequotesandstatus.presentation.dialog.PreparationDialog
import com.sarftec.lifequotesandstatus.presentation.utils.isDarkMode
import com.sarftec.lifequotesandstatus.readSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : BaseActivity() {

    @Inject
    lateinit var databaseSetup: DataSetup

    private val preparationDialog by lazy {
        PreparationDialog(this)
    }

    override fun onBackPressed() {
        super.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            switchNightMode()
            if (!databaseSetup.isSetup()) {
                preparationDialog.show()
                databaseSetup.setup()
            }
            imageStore.loadImages()
            startActivity(Intent(this@StartActivity, SplashActivity::class.java))
            finish()
            overridePendingTransition(R.anim.no_anim, R.anim.no_anim)
        }
    }

    private suspend fun switchNightMode() {
        val constant =
            if (readSettings(isDarkMode, false).first()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(constant)
    }
}