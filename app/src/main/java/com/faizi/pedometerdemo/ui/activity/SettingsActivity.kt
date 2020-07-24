package com.faizi.pedometerdemo.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.browser.customtabs.CustomTabsIntent
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.SensorListener
import com.faizi.pedometerdemo.util.AppUtils
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : Activity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        nav_back.setOnClickListener {
          finish()
        }

        auto_count_switch.isEnabled = AppUtils.getDefaultPreferences(this).getString("pedo_state", null) == "stop"

        auto_count_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppUtils.getDefaultPreferences(this).edit().putString("pedo_state", "stop").apply()
                startService(Intent(this, SensorListener::class.java))
            } else {
                AppUtils.getDefaultPreferences(this).edit().putString("pedo_state", "resume").apply()
                stopService(Intent(this, SensorListener::class.java))
            }
        }

        change_language.setOnClickListener(this)
        privacy_policy.setOnClickListener(this)
        rate_us.setOnClickListener(this)
        share.setOnClickListener(this)
        premium.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.change_language -> {}
            R.id.privacy_policy -> {
                val url = "https://www.freeprivacypolicy.com/privacy/view/50c5621471755f1548917ebbe5e90160"
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
            }
            R.id.rate_us -> {
                dialog = null
                dialog = showRateExitDialogue(this@SettingsActivity, true)
                dialog!!.show()
            }
            R.id.premium -> {}
            R.id.share -> {
                shareIntent()
            }
        }
    }

    private fun shareIntent() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareSubText = this.resources.getString(R.string.great_app)
        // TODO: 7/24/2020 get app link
        val shareBodyText = this.resources.getString(R.string.use_one) + "app_link" + packageName
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubText)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
        startActivity(Intent.createChooser(shareIntent, this.resources.getString(R.string.share_with)))
    }
}