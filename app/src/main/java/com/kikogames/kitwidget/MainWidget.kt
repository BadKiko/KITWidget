package com.kikogames.kitwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import java.io.File
import android.content.pm.PackageManager

import android.util.Log
import android.view.View


/**
 * Implementation of App Widget functionality.
 */
class MainWidget : AppWidgetProvider() {
    var views: RemoteViews? = null
    var directory: String? = null
    var mainHTMLFile: File? = null
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

        checkOnFirstLaunch(appWidgetManager, ComponentName(context, this::class.java),
            mainHTMLFile!!, context
        ) // Проверка на первый запуск

        Log.d("WIDGET INFO *M*", appWidgetId.toString())
        Log.d("WIDGET INFO *M*", views.toString())

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
        context: Context, ){
        if(!mainHTMLFile?.exists()!!){ //Если файл еще не существует
            Log.d("KITWidget", "temp.html is not founded")
            views?.setViewVisibility(R.id.textView0, View.INVISIBLE)
            views?.setViewVisibility(R.id.textView1, View.INVISIBLE)
            views?.setTextViewText(R.id.textView2, "Перейдите в приложение и нажмите обновить!")
            views?.setViewVisibility(R.id.textView3, View.INVISIBLE)
            views?.setViewVisibility(R.id.textView4, View.INVISIBLE)
        }
        else{
            val widgetDataUpdater = WidgetDataUpdater()

            views?.let {
                widgetDataUpdater.update(appWidgetManager, componentName,
                    it,file, context)
            }

        }
    }
}
