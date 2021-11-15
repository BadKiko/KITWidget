package com.kikogames.kitwidget

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.io.File
import android.content.pm.PackageManager

import android.util.Log
import android.view.View
import android.app.PendingIntent
import android.content.res.ColorStateList
import android.graphics.*
import android.os.Build
import android.os.SystemClock
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children


/**
 * Implementation of App Widget functionality.
 */
class MainWidget : AppWidgetProvider() {
    lateinit var views: RemoteViews
    lateinit var directory: String
    lateinit var mainHTMLFile: File
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    )
    {

        // Construct the RemoteViews object
        views = RemoteViews(context.packageName, R.layout.main_widget)

        takeDirectory(context) //Берем основную директорию

        mainHTMLFile = File("$directory/temp.html")

        context.startService(Intent(context, UpdateWiget::class.java))

        val alarmM = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        alarmM.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 10000, pendingIntent)

        val mSharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if(mSharedPrefs.contains("color_background")){
            views.setInt(R.id.imageView4, "setColorFilter", mSharedPrefs.getInt("color_background", 0))
            views.setInt(R.id.separator0, "setColorFilter", mSharedPrefs.getInt("color_background", 0))
            views.setInt(R.id.separator1, "setColorFilter", mSharedPrefs.getInt("color_background", 0))
            views.setInt(R.id.separator2, "setColorFilter", mSharedPrefs.getInt("color_background", 0))
            views.setInt(R.id.separator3, "setColorFilter", mSharedPrefs.getInt("color_background", 0))
        }
        if(mSharedPrefs.contains("color_text")){
            views.setTextColor(R.id.textView0, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView1, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView2, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView3, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView4, mSharedPrefs.getInt("color_text", 0));
        }

        checkOnFirstLaunch(appWidgetManager, ComponentName(context, this::class.java),
            mainHTMLFile, context
        ) // Проверка на первый запуск

        Log.d("WIDGET INFO *M*", appWidgetId.toString())
        Log.d("WIDGET INFO *M*", views.toString())

        views.setOnClickPendingIntent(R.id.mainwidget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }


    fun takeDirectory(context: Context){
        val pManager: PackageManager = context.packageManager
        val pName: String = context.packageName
        try {
            val p = pManager.getPackageInfo(pName, 0)
            directory = p.applicationInfo.dataDir
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w("KITWidget", "Error Package name not found ", e)
        }
    }

    fun checkOnFirstLaunch(
        appWidgetManager: AppWidgetManager,
        componentName: ComponentName,
        file: File,
        context: Context,
    ){
        if(!mainHTMLFile.exists()){ //Если файл еще не существует
            Log.d("KITWidget", "temp.html is not founded")
            views.setViewVisibility(R.id.textView0, View.INVISIBLE)
            views.setViewVisibility(R.id.textView1, View.INVISIBLE)
            views.setTextViewText(R.id.textView2, "Перейдите в приложение и нажмите обновить!")
            views.setViewVisibility(R.id.textView3, View.INVISIBLE)
            views.setViewVisibility(R.id.textView4, View.INVISIBLE)
        }
        else{
            val widgetDataUpdater = WidgetDataUpdater()
                widgetDataUpdater.update(appWidgetManager, componentName, views,file, context)
        }
    }
}
