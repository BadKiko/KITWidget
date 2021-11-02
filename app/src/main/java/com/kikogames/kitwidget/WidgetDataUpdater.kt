package com.kikogames.kitwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.DrawableCompat
import org.jsoup.Jsoup
import java.io.File
import java.util.*
import android.graphics.Bitmap

import android.graphics.drawable.BitmapDrawable




class WidgetDataUpdater{
    public fun getDate() : Int{
        val calendar: Calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK) - 2
    }

    fun update(appWidgetManager: AppWidgetManager, thisWidget: ComponentName, views: RemoteViews, file: File, context: Context) {
        val mSharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)



        Log.d("[DATE]", getDate().toString())
        views.setViewVisibility(R.id.textView0, View.VISIBLE)
        views.setViewVisibility(R.id.textView1, View.VISIBLE)
        views.setViewVisibility(R.id.textView3, View.VISIBLE)
        views.setViewVisibility(R.id.textView4, View.VISIBLE)
        views.setTextViewText(R.id.textView0, "Расписание на завтра")
        views.setTextViewText(R.id.textView1, Jsoup.parse(file.readText()).select("#dni-109" + getDate().toString() + "> b:nth-child(2)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() + "> b:nth-child(2)").text())
        views.setTextViewText(R.id.textView2, Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(8)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(8)").text())
        views.setTextViewText(R.id.textView3, Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(14)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(14)").text())
        views.setTextViewText(R.id.textView4, Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(20)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+getDate().toString() +" > b:nth-child(20)").text())
        views.setTextViewTextSize(R.id.textView0, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        views.setTextViewTextSize(R.id.textView1, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        views.setTextViewTextSize(R.id.textView2, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        views.setTextViewTextSize(R.id.textView3, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        views.setTextViewTextSize(R.id.textView4, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        Log.d("[SIZE]", mSharedPrefs.getInt("fontSize", 14).toString())

        if(mSharedPrefs.contains("color_background")){
            views.setInt(R.id.imageView4, "setColorFilter", mSharedPrefs.getInt("color_background", 0))

        }
        if(mSharedPrefs.contains("color_text")){
            views.setTextColor(R.id.textView0, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView1, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView2, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView3, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView4, mSharedPrefs.getInt("color_text", 0));

            views.setInt(R.id.separator0, "setColorFilter", mSharedPrefs.getInt("color_text", 0))
            views.setInt(R.id.separator1, "setColorFilter", mSharedPrefs.getInt("color_text", 0))
            views.setInt(R.id.separator2, "setColorFilter", mSharedPrefs.getInt("color_text", 0))
            views.setInt(R.id.separator3, "setColorFilter", mSharedPrefs.getInt("color_text", 0))
        }

        appWidgetManager?.updateAppWidget(appWidgetManager?.getAppWidgetIds(thisWidget)!![0], views)
    }

}