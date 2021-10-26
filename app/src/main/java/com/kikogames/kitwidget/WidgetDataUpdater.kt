package com.kikogames.kitwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import org.jsoup.Jsoup
import java.io.File
import java.util.*

class WidgetDataUpdater{
    fun getDate() : Int{
        val calendar: Calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK) - 2
    }

    fun update(appWidgetManager: AppWidgetManager, thisWidget: ComponentName, views: RemoteViews, file: File) {
        Log.d("[DATE]", getDate().toString())
        views?.setViewVisibility(R.id.textView0, View.VISIBLE)
        views?.setViewVisibility(R.id.textView1, View.VISIBLE)
        views?.setViewVisibility(R.id.textView3, View.VISIBLE)
        views?.setViewVisibility(R.id.textView4, View.VISIBLE)
        views?.setTextViewText(R.id.textView1, Jsoup.parse(file.readText()).select("#dni-109" + getDate().toString() + "> b:nth-child(2)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() + "> b:nth-child(2)").text())
        views?.setTextViewText(R.id.textView2, Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(8)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(8)").text())
        views?.setTextViewText(R.id.textView3, Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(14)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(14)").text())
        views?.setTextViewText(R.id.textView4, Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(20)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(20)").text())

        appWidgetManager?.updateAppWidget(appWidgetManager?.getAppWidgetIds(thisWidget)!![0], views)
    }
}