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
        return calendar.get(Calendar.DAY_OF_WEEK) - 1
    }

    public fun getTimeInSec() : Int{
        val calendar: Calendar = Calendar.getInstance()
        return calendar.get(Calendar.SECOND)
    }

    fun update(appWidgetManager: AppWidgetManager, thisWidget: ComponentName, views: RemoteViews, file: File, context: Context) {

        val mSharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

        updateTimeChunk()

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

        }
        if(mSharedPrefs.contains("color_text")){
            views.setTextColor(R.id.textView0, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView1, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView2, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView3, mSharedPrefs.getInt("color_text", 0));
            views.setTextColor(R.id.textView4, mSharedPrefs.getInt("color_text", 0));
        }

        appWidgetManager?.updateAppWidget(appWidgetManager?.getAppWidgetIds(thisWidget)!![0], views)
    }

    fun updateTimeChunk(){
        Log.d("[DATE]", getTimeInSec().toString())
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}