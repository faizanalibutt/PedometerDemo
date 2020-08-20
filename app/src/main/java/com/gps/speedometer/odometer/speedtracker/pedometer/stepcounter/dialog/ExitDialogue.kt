package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.loadNativeAd
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.NetworkUtils
import kotlinx.android.synthetic.main.layout_exit_rating_dialog.view.*

class ExitDialogue(val activity: Activity, private val isMenu: Boolean) :
    AlertDialog(activity) {

    init {
        kotlin.runCatching {
            val mRootView =
                LayoutInflater.from(activity).inflate(R.layout.layout_exit_rating_dialog, null)

            setView(mRootView)

            mRootView.negative_text.isSelected = true
            if (!isMenu && NetworkUtils.isOnline(activity)) {
                mRootView.exit_views.visibility = View.VISIBLE
                mRootView.rate_views.visibility = View.GONE
                activity.loadNativeAd(mRootView.ad_container_exit, R.layout.ad_unified_exit, ADUnitPlacements.EXIT_NATIVE_AD, true)
            } else {
                mRootView.rate_views.visibility = View.VISIBLE
                mRootView.exit_views.visibility = View.GONE
                mRootView.rating_bar_value.visibility = View.VISIBLE
            }

            if (isMenu) {
                mRootView.positive_text.text = activity.getString(R.string.txt_feedback)
                mRootView.negative_text.text = activity.getString(R.string.text_thanks)
            }
            else {
                mRootView.positive_text.text = activity.getString(R.string.butn_exit)
                mRootView.negative_text.text = activity.getString(R.string.butn_cancel)
            }

            mRootView.positive_text.setOnClickListener {
                if (!isMenu) {
                    activity.finish()
                } else {
                    val url = activity.getString(R.string.app_link)
                    val builderTab = CustomTabsIntent.Builder()
                    val customTabsIntent = builderTab.build()
                    customTabsIntent.launchUrl(context, Uri.parse(url))
                }
            }
            /*setPositiveButton(title) { _, _ -> }*/

            mRootView.negative_text.setOnClickListener {
                // see about it.
                dismiss()
            }

            //setNegativeButton("Cancel", null)

            mRootView.rating_bar_value.rating = 4f
            changeDialogState(4f, isMenu, mRootView)

            if (!isMenu && AppUtils.getDefaultPreferences(activity as AppCompatActivity)
                    .getBoolean("hide_rating", false)
            ) {
                mRootView.rating_bar_value.visibility = View.GONE
                mRootView.dialog_desc.visibility = View.GONE
                mRootView.exit_desc.visibility = View.VISIBLE
            }

            mRootView.rating_bar_value.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                    changeDialogState(rating, isMenu, mRootView)
                }

        }.onFailure {
            // here you can send developer message
        }
    }

    private fun changeDialogState(
        rating: Float,
        isMenu: Boolean,
        mRootView: View
    ) {

        if (isMenu) {
            if (rating == 0f) {
                mRootView.positive_text.text = activity.getString(R.string.text_rate)
                if (isShowing) {
                    mRootView.positive_text.setOnClickListener {
                        dismiss()
                    }
                }
            } else if (rating < 4f) {
                mRootView.positive_text.text = activity.getString(R.string.txt_feedback)
                if (isShowing) {
                    mRootView.positive_text.setOnClickListener {
                        composeEmail(
                            arrayOf("appswingstudio@gmail.com"),
                            context.getString(R.string.text_share_feedback)
                        )
                        dismiss()
                    }
                }
            } else {
                mRootView.positive_text.text = activity.getString(R.string.text_rate)
                if (isShowing) {
                    mRootView.positive_text.setOnClickListener {
                        val url = activity.getString(R.string.app_link)
                        val customTabsIntent =
                            CustomTabsIntent.Builder().build()
                        customTabsIntent.launchUrl(
                            activity as Context,
                            Uri.parse(url)
                        )
                        dismiss()
                    }
                }
            }
        } else {
            if (rating == 0f) {
                mRootView.negative_text.text = activity.getString(R.string.butn_cancel)
                if (isShowing) {
                    mRootView.negative_text.setOnClickListener {
                        dismiss()
                    }
                }
            } else if (rating < 4f) {
                mRootView.negative_text.text = activity.getString(R.string.txt_feedback)
                if (isShowing) {
                    mRootView.negative_text.setOnClickListener {
                        composeEmail(
                            arrayOf("appswingstudio@gmail.com"),
                            mRootView.context.getString(R.string.text_share_feedback)
                        )
                        dismiss()
                    }
                }
            } else {
                mRootView.negative_text.text = activity.getString(R.string.text_rate)
                if (isShowing) {
                    mRootView.negative_text.setOnClickListener {
                        AppUtils.getDefaultPreferences(activity as AppCompatActivity).edit()
                            .putBoolean("hide_rating", true).apply()
                        val url = activity.getString(R.string.app_link)
                        val customTabsIntent =
                            CustomTabsIntent.Builder().build()
                        customTabsIntent.launchUrl(
                            activity as Context,
                            Uri.parse(url)
                        )
                        dismiss()
                    }
                }
            }
        }
    }

    private fun composeEmail(
        addresses: Array<String?>?,
        subject: String?
    ) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        }
    }

}