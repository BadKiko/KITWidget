package com.kikogames.kitwidget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import java.io.File

class UpdateWidget : BroadcastReceiver() {
    var directory: String = ""

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("[SERVICE]","CREATED");

        val mSharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

        var appWidgetManager: AppWidgetManager? = null
        var views: RemoteViews? = null
        var thisWidget: ComponentName? = null
        getDirectory(context)
        val file =  File("$directory/temp.html")
        val fileReplacement =  File("$directory/replacementTemp.html")

        val widgetUpdater: WidgetDataUpdater =  WidgetDataUpdater()

        appWidgetManager = AppWidgetManager.getInstance(context)
        thisWidget = ComponentName(context, MainWidget::class.java)
        views = RemoteViews(context.packageName, R.layout.main_widget)
        Log.d("[WIDGET INFO]", appWidgetManager.toString())
        Log.d("[WIDGET INFO]", thisWidget.className)
        Log.d("[WIDGET INFO]", views.toString())
        widgetUpdater.update(appWidgetManager, thisWidget, views, file, fileReplacement, context)
    }
    private fun getDirectory(context: Context){
        val pManager: PackageManager = context.packageManager
        val pName: String = context.packageName
        try {
            val p = pManager.getPackageInfo(pName, 0)
            directory = p.applicationInfo.dataDir
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w("KITWidget", "Error Package name not found ", e)
        }
    }
}