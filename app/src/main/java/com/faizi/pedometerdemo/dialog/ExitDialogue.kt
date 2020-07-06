package com.faizi.pedometerdemo.dialog

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
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.util.AppUtils
import com.faizi.pedometerdemo.util.NetworkUtils
import kotlinx.android.synthetic.main.layout_exit_rating_dialog.view.*

class ExitDialogue(val activity: Activity, private val isMenu: Boolean) :
    AlertDialog(activity) {

    init {
        kotlin.runCatching {
            val mRootView =
                LayoutInflater.from(activity).inflate(R.layout.layout_exit_rating_dialog, null)

            setView(mRootView)

            if (!isMenu && NetworkUtils.isOnline(activity)) {
                mRootView.rating_group.visibility = View.VISIBLE
                /*val admobUtils = AdmobUtils(activity)
                admobUtils.loadNativeAd(mRootView.fl_adplaceholder, R.layout.ad_unified_3, NativeAdsIdType.EXIT_NATIVE_AM)
                admobUtils.setNativeAdListener(object : AdmobUtils.NativeAdListener {
                    override fun onNativeAdLoaded() {
                        mRootView.rate_exit_ads_view.visibility = View.GONE
                    }
                    override fun onNativeAdError() {

                    }
                })*/
                mRootView.rate_us_image1.visibility = View.GONE
                mRootView.rate_us_image2.visibility = View.GONE
            } else {
                mRootView.dialogTitle.visibility = View.GONE
                mRootView.title_view.visibility = View.GONE
            }

            if (isMenu) {
                mRootView.positive_text.text = activity.getString(R.string.txt_feedback)
                mRootView.negative_text.text = activity.getString(R.string.text_thanks)
            }
            else {
                mRootView.positive_text.text = activity.getString(R.string.butn_exit)
                mRootView.negative_text.text = activity.getString(R.string.butn_cancel)
            }

            mRootView.positive.setOnClickListener {
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

            mRootView.negative.setOnClickListener {
                // see about it.
                dismiss()
            }

            //setNegativeButton("Cancel", null)

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
                            "Share Cloud, Your valuable feedback."
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
                            "Share Cloud, Your valuable feedback."
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

    private fun changeDialogButtonState(
        dialog: AlertDialog?, buttonType: Int, title: String?,
        isDisable: Boolean
    ) {
        if (dialog != null && dialog.isShowing) {
            dialog.getButton(buttonType).text = title
            dialog.getButton(buttonType).isEnabled = isDisable
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