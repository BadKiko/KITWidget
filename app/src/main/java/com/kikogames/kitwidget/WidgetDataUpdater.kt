package com.kikogames.kitwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
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
import android.os.Debug
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import android.graphics.BitmapFactory

import android.content.ContentResolver

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import java.io.FileNotFoundException
import java.io.InputStream
import android.os.ParcelFileDescriptor
import java.io.FileDescriptor
import java.lang.Error
import java.lang.Exception
import android.R.id
import android.annotation.SuppressLint
import android.widget.Toast

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi

class WidgetDataUpdater{
    public fun getDate() : Int{
        val calendar: Calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK) - 2
    }

    private fun getSecondsDay() : Int{
        val calendar: Calendar = Calendar.getInstance()
        return calendar.get(Calendar.SECOND) + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.HOUR_OF_DAY) * 3600
    }

    fun update(appWidgetManager: AppWidgetManager, thisWidget: ComponentName, views: RemoteViews, file: File, context: Context) {
        val mSharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val density: Float = context.getResources().getDisplayMetrics().density

        visibleTextsView(views)

        chooseNeedData(views, file, getSecondsDay())

        changeSizeTextViews(mSharedPrefs, views)
        changePaddingTextViews(mSharedPrefs, views, density)

        changeColor(mSharedPrefs, views, context)

        appWidgetManager.updateAppWidget(appWidgetManager.getAppWidgetIds(thisWidget)!![0], views)
    }

    private fun parseAllToTextViews(views: RemoteViews, file: File, day: Int){
        if (day == 0){
            views.setTextViewText(R.id.textView0, "Расписание на сегодня")
        }
        else if(day == 1){
            views.setTextViewText(R.id.textView0, "Расписание на завтра")
        }

        views.setTextViewText(R.id.textView1, Jsoup.parse(file.readText()).select("#dni-109" + (getDate()+day).toString() + "> b:nth-child(2)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+(getDate()+day).toString() + "> b:nth-child(2)").text())
        views.setTextViewText(R.id.textView2, Jsoup.parse(file.readText()).select("#dni-109"+(getDate()+day).toString() +" > b:nth-child(8)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+(getDate()+day).toString() +" > b:nth-child(8)").text())
        views.setTextViewText(R.id.textView3, Jsoup.parse(file.readText()).select("#dni-109"+(getDate()+day).toString() +" > b:nth-child(14)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+(getDate()+day).toString() +" > b:nth-child(14)").text())
        views.setTextViewText(R.id.textView4, Jsoup.parse(file.readText()).select("#dni-109"+(getDate()+day).toString() +" > b:nth-child(20)").text())
        Log.d("[PARSE LOG]", Jsoup.parse(file.readText()).select("#dni-109"+(getDate()+day).toString() +" > b:nth-child(20)").text())
    }

    private fun setTextInColumns(views: RemoteViews, col1: String, col2: String, col3: String, col4: String, col5: String){
        views.setTextViewText(R.id.textView0, col1)
        views.setTextViewText(R.id.textView1, col2)
        views.setTextViewText(R.id.textView2, col3)
        views.setTextViewText(R.id.textView3, col4)
        views.setTextViewText(R.id.textView4, col5)
    }

    private fun chooseNeedData(views: RemoteViews, file: File, seconds: Int){
        val firstPair = Jsoup.parse(file.readText()).select("#dni-109" + (getDate()).toString() + "> b:nth-child(2)").text()
        val secondPair = Jsoup.parse(file.readText()).select("#dni-109" + (getDate()).toString() + "> b:nth-child(8)").text()
        val thirdPair = Jsoup.parse(file.readText()).select("#dni-109" + (getDate()).toString() + "> b:nth-child(14)").text()
        val fourtPair = Jsoup.parse(file.readText()).select("#dni-109" + (getDate()).toString() + "> b:nth-child(20)").text()
        val foodTime = arrayOf("10:50","ДО","12:20", "11:50", "12:50", "12:20")

        when(seconds){
            // 1 Урок
            in 32400..35100 -> {
                setTextInColumns(views, "1 половина 1 пары / 1 урок", "До конца урока "+(35100-seconds+60+60)/60+" мин", "Сейчас идет пара: $firstPair", "Будет пара: $secondPair", "Обед в: "+foodTime[getDate()])

            }
            // Перемена 2 урока
            in 35100..35400 -> {
                setTextInColumns(views, "Перемена на 2 половину 1 пары", "До конца перемены "+(35400-seconds+60)/60+" мин", "Будет пара: $firstPair", "Обед в: "+foodTime[getDate()], "")
            }
            // 2 урок
            in 35400..38100 -> {
                setTextInColumns(views, "2 половина 1 пары / 2 урок", "До конца урока "+(38100-seconds+60)/60+" мин", "Сейчас идет пара: $firstPair", "Будет пара: $secondPair", "Обед в: "+foodTime[getDate()])
            }
            // Перемена 3 урока
            in 38100..39900 -> {
                setTextInColumns(views, "Перемена на 1 половину 2 пары", "До конца перемены "+(39900-seconds+60)/60+" мин", "Будет пара: $secondPair", "Обед в: "+foodTime[getDate()] ,"")
            }
            // 3 урок
            in 39900..42600 -> {
                setTextInColumns(views, "1 половина 2 пары / 3 урок", "До конца урока "+(42600-seconds+60)/60+" мин", "Сейчас идет пара: $secondPair", "Будет пара: $thirdPair", "Обед в: "+foodTime[getDate()])
            }
            // Перемена 4 урока
            in 42600..42900 -> {
                setTextInColumns(views, "Перемена на 2 половину 2 пары", "До конца перемены "+(42900-seconds+60)/60+" мин", "Будет пара: $secondPair", "Обед в: "+foodTime[getDate()], "")
            }
            // 4 урок
            in 42900..45600 -> {
                setTextInColumns(views, "2 половина 2 пары / 4 урок", "До конца урока "+(45600-seconds+60)/60+" мин", "Сейчас идет пара: $secondPair", "Будет пара: $thirdPair", "Обед в: "+foodTime[getDate()])
            }
            // Перемена 5 урока
            in 45600..47400 -> {
                setTextInColumns(views, "Перемена на 1 половину 3 пары", "До конца перемены "+(47400-seconds+60)/60+" мин", "Будет пара: $thirdPair", "Обед в: "+foodTime[getDate()], "")
            }
            // 5 урок
            in 47400..50100 -> {
                setTextInColumns(views, "1 половина 3 пары / 5 урок", "До конца урока "+(50100-seconds+60)/60+" мин", "Сейчас идет пара: $thirdPair", "Будет пара: $fourtPair", "Обед в: "+foodTime[getDate()])
            }
            // Перемена 6 урока
            in 50100..50400 -> {
                setTextInColumns(views, "Перемена на 2 половину 3 пары", "До конца перемены "+(50400-seconds+60)/60+" мин", "Будет пара: $thirdPair", "Обед в: "+foodTime[getDate()], "")
            }
            // 6 урок
            in 50400..53100 -> {
                setTextInColumns(views, "2 половина 3 пары / 6 урок", "До конца урока "+(53100-seconds+60)/60+" мин", "Сейчас идет пара: $thirdPair", "Будет пара: $fourtPair", "Обед в: "+foodTime[getDate()])
            }
            // Перемена 7 урока
            in 53100..53700 -> {
                setTextInColumns(views, "Перемена на 1 половину 4 пары", "До конца перемены "+(53700-seconds+60)/60+" мин", "Будет пара: $fourtPair", "Обед в: "+foodTime[getDate()], "")
            }
            // 7 урок
            in 53700..56400 -> {
                setTextInColumns(views, "1 половина 4 пары / 7 урок", "До конца урока "+(56400-seconds+60)/60+" мин", "Сейчас идет пара: $fourtPair", "Домой!", "Обед в: "+foodTime[getDate()])
            }
            // Перемена 8 урока
            in 56400..56700 -> {
                setTextInColumns(views, "Перемена на 2 половину 4 пары", "До конца перемены "+(56700-seconds+60)/60+" мин", "Будет пара: $fourtPair", "Обед в: "+foodTime[getDate()], "")
            }
            // 8 урок
            in 56700..59400 -> {
                setTextInColumns(views, "2 половина 4 пары / 8 урок", "До конца урока "+(59400-seconds+60)/60+" мин", "Сейчас идет пара: $fourtPair", "Домой!", "Обед в: "+foodTime[getDate()])
            }

            // Утро до коляги
            in 0..32400 -> {
                parseAllToTextViews(views, file, 0)
            }
            // Вечер после коляги
            in 59400..86400 -> {
                parseAllToTextViews(views, file, 1)
            }
        }
    }

    private fun changePaddingTextViews(mSharedPrefs: SharedPreferences, views: RemoteViews, density: Float){
        views.setViewPadding(R.id.textView0, 0,
            (density * mSharedPrefs.getInt("offset", 16)).toInt(),0,(density * mSharedPrefs.getInt("offset", 16)).toInt())
        views.setViewPadding(R.id.textView1, 0,
            (density * mSharedPrefs.getInt("offset", 16)).toInt(),0,(density * mSharedPrefs.getInt("offset", 16)).toInt())
        views.setViewPadding(R.id.textView2, 0,
            (density * mSharedPrefs.getInt("offset", 16)).toInt(),0,(density * mSharedPrefs.getInt("offset", 16)).toInt())
        views.setViewPadding(R.id.textView3, 0,
            (density * mSharedPrefs.getInt("offset", 16)).toInt(),0,(density * mSharedPrefs.getInt("offset", 16)).toInt())
        views.setViewPadding(R.id.textView4, 0,
            (density * mSharedPrefs.getInt("offset", 16)).toInt(),0,(density * mSharedPrefs.getInt("offset", 16)).toInt())
    }

    private fun changeSizeTextViews(mSharedPrefs: SharedPreferences, views: RemoteViews){
        views.setTextViewTextSize(R.id.textView0, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        views.setTextViewTextSize(R.id.textView1, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        views.setTextViewTextSize(R.id.textView2, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        views.setTextViewTextSize(R.id.textView3, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        views.setTextViewTextSize(R.id.textView4, TypedValue.COMPLEX_UNIT_SP, mSharedPrefs.getInt("fontSize", 14).toFloat())
        Log.d("[SIZE]", mSharedPrefs.getInt("fontSize", 14).toString())
    }

    private fun changeColor(mSharedPrefs: SharedPreferences, views: RemoteViews, context: Context){
            if (mSharedPrefs.contains("color_background")) {
                views.setInt(
                    R.id.imageView4,
                    "setColorFilter",
                    mSharedPrefs.getInt("color_background", 0)
                )

            }
            if (mSharedPrefs.contains("color_text")) {
                views.setTextColor(R.id.textView0, mSharedPrefs.getInt("color_text", 0));
                views.setTextColor(R.id.textView1, mSharedPrefs.getInt("color_text", 0));
                views.setTextColor(R.id.textView2, mSharedPrefs.getInt("color_text", 0));
                views.setTextColor(R.id.textView3, mSharedPrefs.getInt("color_text", 0));
                views.setTextColor(R.id.textView4, mSharedPrefs.getInt("color_text", 0));

                views.setInt(
                    R.id.separator0,
                    "setColorFilter",
                    mSharedPrefs.getInt("color_text", 0)
                )
                views.setInt(
                    R.id.separator1,
                    "setColorFilter",
                    mSharedPrefs.getInt("color_text", 0)
                )
                views.setInt(
                    R.id.separator2,
                    "setColorFilter",
                    mSharedPrefs.getInt("color_text", 0)
                )
                views.setInt(
                    R.id.separator3,
                    "setColorFilter",
                    mSharedPrefs.getInt("color_text", 0)
                )
            }

    }

    private fun visibleTextsView(views: RemoteViews){
        views.setViewVisibility(R.id.textView0, View.VISIBLE)
        views.setViewVisibility(R.id.textView1, View.VISIBLE)
        views.setViewVisibility(R.id.textView3, View.VISIBLE)
        views.setViewVisibility(R.id.textView4, View.VISIBLE)
    }

    class MainReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            // Start the service when music plays, stop the service when music ends.
            if (intent.hasExtra("playing")) {
                if (intent.getBooleanExtra("playing", false)) {
                    val songId = intent.getLongExtra("id", -1)
                    Log.d("[MUSIC]", "Playing $songId")
                } else {
                    Log.d("[MUSIC]", "Not Playing")
                }
            }
        }
    }

}