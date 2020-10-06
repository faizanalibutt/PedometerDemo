package com.nined.fcm.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nined.fcm.BuildConfig
import com.nined.fcm.R
import com.nined.fcm.utils.TinyDB
import com.squareup.picasso.Picasso
import java.util.concurrent.atomic.AtomicInteger

open class FcmFireBaseMessagingService : FirebaseMessagingService() {

    companion object {

        val ICON_KEY = "icon"
        val APP_TITLE_KEY = "title"
        val SHORT_DESC_KEY = "short_desc"
        val LONG_DESC_KEY = "long_desc"
        val APP_FEATURE_KEY = "feature"
        val APP_URL_KEY = "app_url"
        private const val RELEASE_TOPIC = "live_earth_map_imagineloft"
        private const val DEBUG_TOPIC = "test_app"
        const val IS_PREMIUM = "is_premium"

        private val seed = AtomicInteger()

        fun getNextInt(): Int {
            return seed.incrementAndGet()
        }

        @JvmStatic
        fun subscribeToTopic(): Boolean {
            try {
                if (!FirebaseInstanceId.getInstance().id.isEmpty()) {
                    if (BuildConfig.DEBUG)
                        FirebaseMessaging.getInstance().subscribeToTopic(DEBUG_TOPIC)
                    else {
                        FirebaseMessaging.getInstance().subscribeToTopic(RELEASE_TOPIC)
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(DEBUG_TOPIC)
                    }
                    return true
                }
                return false
            } catch (e: Exception) {
                Log.e("FirebaseMessaging", e.toString())
                return false
            }
        }
    }


    override fun onNewToken(p0: String) {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        if (BuildConfig.DEBUG) Log.d("Refreshed token: %s", refreshedToken)
        subscribeToTopic()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data: Map<String, String>? = remoteMessage.data

        data?.let {
            val iconURL = data[ICON_KEY]
            val title = data[APP_TITLE_KEY]
            val shortDesc = data[SHORT_DESC_KEY]
            val longDesc = data[LONG_DESC_KEY]
            val feature = data[APP_FEATURE_KEY]
            val appURL = data[APP_URL_KEY]
            val notificationID = getNextInt()

            if (iconURL != null && title != null && shortDesc != null && feature != null && appURL != null) {
                val standard = "https://play.google.com/store/apps/details?id="

                try {
                    val id = appURL.substring(standard.length)
                    if (BuildConfig.DEBUG) Log.e("package sent ", id)

                    if (!isAppInstalled(
                            id,
                            this
                        ) && !TinyDB.getInstance(this).getBoolean(IS_PREMIUM)
                    )
                        Handler(this.mainLooper).post {
                            customNotification(
                                iconURL,
                                title, shortDesc, longDesc,
                                feature, appURL, notificationID
                            )

//                            testNotify()
                        }

                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) Log.e("FcmFireBase", "package not valid")
                }
            }

        }



        if (BuildConfig.DEBUG) Log.e("From: ", remoteMessage.from)
        if (remoteMessage.notification != null) {
            if (BuildConfig.DEBUG) Log.e("Message  Body:", remoteMessage.notification!!.body)
        }
    }


    //    custom view
    private fun customNotification(
        iconURL: String, title: String, shortDesc: String, longDesc: String?,
        feature: String?, appURL: String, notificationID: Int
    ) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appURL))
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0 /* request code */,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val remoteViews = RemoteViews(packageName, R.layout.notification_app)

        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_short_desc, shortDesc)
        remoteViews.setTextViewText(R.id.tv_long_desc, longDesc)
        remoteViews.setViewVisibility(
            R.id.tv_long_desc,
            if (longDesc != null && !longDesc.isEmpty()) View.VISIBLE else View.GONE
        )

        val builder = NotificationCompat.Builder(this, title)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ic_ad_small)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)

        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                title,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager.createNotificationChannel(channel)
        }

        mNotificationManager.notify(notificationID, builder.build())

        Picasso.get()
            .load(iconURL)
            .into(remoteViews, R.id.iv_icon, notificationID, builder.build())
        Picasso.get()
            .load(feature)
            .into(remoteViews, R.id.iv_feature, notificationID, builder.build())


    }


    private fun isAppInstalled(uri: String, context: Context): Boolean {
        val pm = context.packageManager
        return try {
            val applicationInfo = pm.getApplicationInfo(uri, 0)
            //            packageInfo
            applicationInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }
}