package com.kikogames.kitwidget

import android.app.Notification
import android.graphics.drawable.Icon
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.os.IBinder
import android.widget.ImageView
import androidx.annotation.RequiresApi

import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette

import android.appwidget.AppWidgetManager
import android.content.*
import android.content.pm.PackageManager

import android.util.Log
import android.widget.RemoteViews
import androidx.palette.graphics.Palette.PaletteAsyncListener
import java.io.File
import java.io.IOException

class MusicArt() : NotificationListenerService() {

    lateinit var iw: ImageView

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val bundle = sbn!!.notification.extras
        if(bundle.containsKey(Notification.EXTRA_LARGE_ICON)) {
            try {
                if ((bundle["android.largeIcon"] as Icon?)?.loadDrawable(baseContext)
                    ?.toBitmap()?.density == 440
                ) {
                    Palette.generateAsync(
                        (bundle["android.largeIcon"] as Icon).loadDrawable(
                            baseContext
                        ).toBitmap(), PaletteAsyncListener {
                            val dColor = it?.getDominantColor(0) as Int
                            val mColor = it.getVibrantColor(0) as Int
                            val appWidgetManager = AppWidgetManager.getInstance(this)
                            val thisWidget = ComponentName(this, MainWidget::class.java)
                            val remoteViews = RemoteViews(packageName, R.layout.main_widget)
                            val file: File
                            if (File("@/temp.html").exists()) {
                                file = File("${getDirectory()}/temp.html")
                            } else {
                                File("${getDirectory()}/temp.html").createNewFile()
                                file = File("${getDirectory()}/temp.html")
                                Log.d("[FS]", "Can't delete file, file isn't exists")
                            }

                            val fileReplacement: File
                            if (File("@/temp.html").exists()) {
                                fileReplacement = File("${getDirectory()}/replacementTemp.html")
                            } else {
                                File("${getDirectory()}/temp.html").createNewFile()
                                fileReplacement = File("${getDirectory()}/replacementTemp.html")
                                Log.d("[FS]", "Can't delete file, file isn't exists")
                            }


                            updateOnce(
                                appWidgetManager,
                                thisWidget,
                                remoteViews,
                                file,
                                fileReplacement,
                                dColor,
                                mColor
                            )
                        })
                }
            }
            catch (e: IOException){
                Log.e("[ERROR]", e.toString())
            }
        }
    }

    private fun getDirectory() : String {
        val pManager: PackageManager = packageManager
        val pName: String = packageName
        try {
            val p = pManager.getPackageInfo(pName, 0)
            return applicationInfo.dataDir
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w("KITWidget", "Error Package name not found ", e)
            return "null"
        }
    }

     fun updateOnce(appWidgetManager: AppWidgetManager, thisWidget: ComponentName, remoteViews: RemoteViews, file: File, fileReplacement: File, colorD: Int, colorM: Int) {
        val widgetUpdater: WidgetDataUpdater = WidgetDataUpdater()
         widgetUpdater.update(appWidgetManager, thisWidget, remoteViews, file, fileReplacement, applicationContext, colorD, colorM)
    }
}
