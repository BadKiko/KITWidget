package com.kikogames.kitwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.widget.TextView
import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.IOException
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.opengl.Visibility
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

        checkOnFirstLaunch() // Проверка на первый запуск

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

    fun checkOnFirstLaunch(){
        if(!mainHTMLFile?.exists()!!){ //Если файл еще не существует
            Log.d("KITWidget", "temp.html is not founded")
            views?.setViewVisibility(R.id.textView0, View.INVISIBLE)
            views?.setViewVisibility(R.id.textView1, View.INVISIBLE)
            views?.setTextViewText(R.id.textView2, "Перейдите в приложение и нажмите обновить!")
            views?.setViewVisibility(R.id.textView3, View.INVISIBLE)
            views?.setViewVisibility(R.id.textView4, View.INVISIBLE)
        }
        else{
            views?.setTextViewText(R.id.textView1, Jsoup.parse(mainHTMLFile?.readText()).select("#dni-1090 > b:nth-child(2)").text())
            Log.d("[PARSE LOG]", Jsoup.parse(mainHTMLFile?.readText()).select("#dni-1090 > b:nth-child(2)").text())
            views?.setTextViewText(R.id.textView2, Jsoup.parse(mainHTMLFile?.readText()).select("#dni-1090 > b:nth-child(8)").text())
            Log.d("[PARSE LOG]", Jsoup.parse(mainHTMLFile?.readText()).select("#dni-1090 > b:nth-child(8)").text())
            views?.setTextViewText(R.id.textView3, Jsoup.parse(mainHTMLFile?.readText()).select("#dni-1090 > b:nth-child(14)").text())
            Log.d("[PARSE LOG]", Jsoup.parse(mainHTMLFile?.readText()).select("#dni-1090 > b:nth-child(14)").text())
            views?.setTextViewText(R.id.textView4, Jsoup.parse(mainHTMLFile?.readText()).select("#dni-1090 > b:nth-child(20)").text())
            Log.d("[PARSE LOG]", Jsoup.parse(mainHTMLFile?.readText()).select("#dni-1090 > b:nth-child(20)").text())

        }
    }
}


