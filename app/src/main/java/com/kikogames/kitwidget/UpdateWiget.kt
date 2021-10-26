package com.kikogames.kitwidget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import org.jsoup.Jsoup
import java.io.File
import java.util.*

class UpdateWiget : Service() {
var directory: String = ""

    override fun onCreate() {
        super.onCreate()
        var appWidgetManager: AppWidgetManager? = null
        var views: RemoteViews? = null
        var thisWidget: ComponentName? = null
        getDirectory()
        val file =  File("$directory/temp.html")
        val widgetUpdater: WidgetDataUpdater =  WidgetDataUpdater()

        appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        thisWidget = ComponentName(applicationContext, MainWidget::class.java)
        views = RemoteViews(packageName, R.layout.main_widget)
        Log.d("[WIDGET INFO]", appWidgetManager.toString())
        Log.d("[WIDGET INFO]", thisWidget.className)
        Log.d("[WIDGET INFO]", views.toString())
        widgetUpdater.update(appWidgetManager, thisWidget, views, file)

        val period = 15000L
        val timer: Timer = Timer()
        val timerTask: TimerTask = object : TimerTask(){
            override fun run(){
                Log.d("[TIMER INFO]", "UPDATE WIDGET")
                widgetUpdater.update(appWidgetManager, thisWidget, views, file)
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, period)
    }
    private fun getDirectory(){
        val pManager: PackageManager = packageManager
        val pName: String = packageName
        try {
            val p = pManager.getPackageInfo(pName, 0)
            directory = p.applicationInfo.dataDir
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w("KITWidget", "Error Package name not found ", e)
        }
    }
    override fun onBind(intent: Intent): IBinder {

        TODO("Return the communication channel to the service.")
    }
}