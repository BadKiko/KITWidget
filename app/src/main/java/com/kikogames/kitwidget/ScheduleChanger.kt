package com.kikogames.kitwidget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.animation.TranslateAnimation
import android.widget.RemoteViews
import android.widget.Toast
import java.io.File

class ScheduleChanger : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(context!=null) {
            val mSharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val editR = mSharedPrefs.edit()
            val views = RemoteViews(context.packageName, R.layout.main_widget)

            editR.putBoolean("schFull", !mSharedPrefs.getBoolean("schFull", false))
            editR.apply()
            val mainHTMLFile = File("${takeDirectory(context)}/temp.html")
            val replacementHTMLFile = File("${takeDirectory(context)}/replacementTemp.html")

            val widgetDataUpdater = WidgetDataUpdater()

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, MainWidget::class.java)
            val remoteViews = RemoteViews(context.packageName, R.layout.main_widget)

            widgetDataUpdater.update(
                appWidgetManager,
                thisWidget,
                remoteViews,
                mainHTMLFile,
                replacementHTMLFile,
                context
            )
        }
    }

    fun takeDirectory(context: Context) : String {
        val pManager: PackageManager = context.packageManager
        val pName: String = context.packageName
        try {
            val p = pManager.getPackageInfo(pName, 0)
            return p.applicationInfo.dataDir
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w("KITWidget", "Error Package name not found ", e)
            return ""
        }
    }
}