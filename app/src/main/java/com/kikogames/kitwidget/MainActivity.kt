package com.kikogames.kitwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.IOException
import android.widget.Toast
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.view.children
import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog
import me.jfenn.colorpickerdialog.views.picker.ImagePickerView
import java.util.*
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var directory: String
    var debugMode = 0
    lateinit var appWidgetManager: AppWidgetManager
    lateinit var remoteViews: RemoteViews
    lateinit var thisWidget: ComponentName
    lateinit var file: File
    val widgetDataUpdater = WidgetDataUpdater()
    var imw: ImageView? = null

    override fun onStart() {
        super.onStart()

        val widgetSizeProvider = WidgetSizeProvider(this)
        val density: Float = applicationContext.getResources().getDisplayMetrics().density

        if(appWidgetManager.getAppWidgetIds(thisWidget).isNotEmpty()) {
            findViewById<View>(R.id.widgetBack).layoutParams.width =
                widgetSizeProvider.getWidgetsSize(appWidgetManager.getAppWidgetIds(thisWidget)[0]).first // Получаем размер виджета
            findViewById<View>(R.id.widgetBack).layoutParams.height =
                widgetSizeProvider.getWidgetsSize(appWidgetManager.getAppWidgetIds(thisWidget)[0]).second // Получаем размер виджета
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.hide()
        val mSharedPrefs = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editR = mSharedPrefs.edit()

        appWidgetManager = AppWidgetManager.getInstance(this)
        thisWidget = ComponentName(this, MainWidget::class.java)
        remoteViews = RemoteViews(packageName, R.layout.main_widget)

        getDirectory() // Получаем main директорию

        if (File("$directory/temp.html").exists()) {
            file = File("$directory/temp.html")
        } else {
            File("$directory/temp.html").createNewFile()
            file = File("$directory/temp.html")
            Log.d("[FS]", "Can't delete file, file isn't exists")
        }
        if(mSharedPrefs.contains("color_background")){
            findViewById<View>(R.id.widgetBack).backgroundTintList = ColorStateList.valueOf(mSharedPrefs.getInt("color_background", 0))
        }
        if(mSharedPrefs.contains("color_text")){
            colorizeWidgetText(mSharedPrefs.getInt("color_text", 0))
        }

        updateColumnsInReview()

        val fontSizeTextView = findViewById<TextView>(R.id.fontSizeTV)
        val seekBarFontSize = findViewById<SeekBar>(R.id.shrift)
        val seekBarOffset = findViewById<SeekBar>(R.id.offset)
        val offsetSizeTextView = findViewById<TextView>(R.id.offsetSizeTV)

        val updateButton = findViewById<Button>(R.id.update)
        val deleteButton = findViewById<Button>(R.id.deleteAll)
        val credits = findViewById<TextView>(R.id.createdBy)
        val widgetSizeProvider = WidgetSizeProvider(this)

        val firstBtn = findViewById<Button>(R.id.firstcolor)
        val secondBtn = findViewById<Button>(R.id.secondcolor)

        // Обновление превью виджета
        widgetDataUpdater.updateMainPreview(findViewById(R.id.widgetBack), file)

        val musicCheckBox = findViewById<CheckBox>(R.id.musicCheckbox)
        val scheduleChangerCheckBox = findViewById<CheckBox>(R.id.fullShedule)
        val schedulleButton = findViewById<Button>(R.id.schedullButonMainA)

        scheduleChangerCheckBox.isChecked = mSharedPrefs.getBoolean("fullSchedule", false)
        if(scheduleChangerCheckBox.isChecked){
            schedulleButton.visibility = View.VISIBLE
        }
        scheduleChangerCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            editR.putBoolean("fullSchedule", isChecked)
            editR.apply()
            if(isChecked) {
                schedulleButton.visibility = View.VISIBLE
            }
            else
            {
                schedulleButton.visibility = View.INVISIBLE
            }
            updateOnce()
        }

        musicCheckBox.isChecked = mSharedPrefs.getBoolean("music", false)
        if(musicCheckBox.isChecked){
            startService(Intent(this, MusicArt::class.java))
        }
        musicCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            editR.putBoolean("music", isChecked)
            editR.apply()
            if(isChecked){
                startService(Intent(this, MusicArt::class.java))
            }
            else {
                stopService(Intent(this, MusicArt::class.java))
            }
        }

        if (!mSharedPrefs.contains("fontSize")) {
            editR.putInt("fontSize", 14)
            editR.apply()
            Log.d(
                "[SP]",
                "Put fontSize 14 | Contain fontSize - " + mSharedPrefs.contains("fontSize")
            )
        } else {
            seekBarFontSize.progress = mSharedPrefs.getInt("fontSize", 14)
            fontSizeTextView.text =
                "Размер шрифта виджета - " + seekBarFontSize.progress.toString() + "dp"

            updateAllTextSizes(seekBarFontSize.progress)

            updateOnce()
        }

        if (!mSharedPrefs.contains("offset")) {
            editR.putInt("offset", 16)
            editR.apply()
            Log.d(
                "[SP]",
                "Put offset 16 | Contain offset - " + mSharedPrefs.contains("offset")
            )
        } else {
            seekBarOffset.progress = mSharedPrefs.getInt("offset", 14)
            offsetSizeTextView.text =
                "Ширина промежутков - " + seekBarOffset.progress.toString() + "dp"

            updateAllOffsets(seekBarOffset.progress)

            updateOnce()
        }

        seekBarOffset.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                offsetSizeTextView.text = "Ширина промежутков - " + progress.toString() + "dp"
                editR.putInt("offset", progress)
                editR.apply()
                Log.d(
                    "[SP]",
                    "Put offset $progress | Contain offset - " + mSharedPrefs.contains("offset")
                )

                updateAllOffsets(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                updateOnce()
            }
        })

        seekBarFontSize.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fontSizeTextView.text = "Размер шрифта виджета - " + progress.toString() + "px"
                editR.putInt("fontSize", progress)
                editR.apply()
                Log.d(
                    "[SP]",
                    "Put fontSize $progress | Contain fontSize - " + mSharedPrefs.contains("fontSize")
                )

                updateAllTextSizes(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                updateOnce()
            }
        })

        credits.setOnClickListener {
            debugMode++
            debugCheck()
        }
        deleteButton.setOnClickListener {
            if (File("$directory/temp.html").exists()) {
                Log.d("[FS]", "$directory/temp.html deleted")
                File("$directory/temp.html").delete()
            } else {
                Log.d("[FS]", "Can't delete file, file isn't exists")
            }
        }
        updateButton.setOnClickListener {
            parseHTML(this)
            widgetDataUpdater.update(appWidgetManager,
                thisWidget, remoteViews, file, applicationContext)
        }

        //Цвета
        firstBtn.setOnClickListener{
            ColorPickerDialog()
                .withPicker(ImagePickerView::class.java)
                .withTheme(R.style.ColorPickerDialog_Dark)
                .withCornerRadius(20f)
                .withPresets(Color.RED, Color.GREEN, Color.BLUE)
                .withColor(mSharedPrefs.getInt("color_background", Color.GRAY)) // the default / initial color
                .withListener { dialog, color ->
                    findViewById<View>(R.id.widgetBack).backgroundTintList = ColorStateList.valueOf(color)
                    editR.putInt("color_background",color)
                    editR.apply()
                    updateOnce()
                }
                .show(supportFragmentManager, "colorPicker")
        }
        secondBtn.setOnClickListener{
            ColorPickerDialog()
                .withPicker(ImagePickerView::class.java)
                .withTheme(R.style.ColorPickerDialog_Dark)
                .withCornerRadius(20f)
                .withPresets(Color.RED, Color.GREEN, Color.BLUE)
                .withColor(mSharedPrefs.getInt("color_text", Color.GRAY)) // the default / initial color
                .withListener { dialog, color ->
                    colorizeWidgetText(color)
                    editR.putInt("color_text",color)
                    editR.apply()
                    updateOnce()
                }
                .show(supportFragmentManager, "colorPicker")
        }

        startUpdating()
    }

    private fun updateAllTextSizes(progress: Int){
        for (childView in findViewById<ConstraintLayout>(R.id.widgetBack).children) {
            if(childView is TextView){
                childView.textSize = progress.toFloat()
            }
        }
    }

    private fun updateAllOffsets(progress: Int){
        val mainView = findViewById<ConstraintLayout>(R.id.widgetBack)

        for (childC: Int in 0..mainView.childCount-1) {
            when(childC){
                in 0..3 -> {
                    val childView = mainView.getChildAt(childC)
                    val lp = childView.layoutParams as ConstraintLayout.LayoutParams
                    val density: Float = applicationContext.getResources().getDisplayMetrics().density
                    lp.setMargins(0, 0,0,(progress * density).toInt())
                    childView.layoutParams = lp
                }
                in 5..8 -> {
                    val childView = mainView.getChildAt(childC)
                    val lp = childView.layoutParams as ConstraintLayout.LayoutParams
                    val density: Float = applicationContext.getResources().getDisplayMetrics().density
                    lp.setMargins(0, (progress * density).toInt(),0,0)
                    childView.layoutParams = lp
                }
            }
        }
    }

    private fun startUpdating() {
        val alarmMan = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(applicationContext, UpdateWidget::class.java)

        val pendingIntent: PendingIntent

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
        else{
            pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_MUTABLE)
        }

        val nextMinute = ((System.currentTimeMillis()/60000)+1)*60000

        Log.d("[SYSTEM]", "NEXT MIN: " + nextMinute.toString() + " AND " + "SYSTEM: " + System.currentTimeMillis())

        alarmMan.setRepeating(AlarmManager.RTC_WAKEUP ,nextMinute, 60000, pendingIntent)

    }

    private fun updateOnce() {
        widgetDataUpdater.update(appWidgetManager, thisWidget, remoteViews, file, applicationContext)
    }

    fun colorizeWidgetText(color: Int){
        for (childView in findViewById<ConstraintLayout>(R.id.widgetBack).children) {
            if(childView is TextView){
                childView.setTextColor(color)
            }
            else{
                childView.backgroundTintList = ColorStateList.valueOf(color)
            }
        }
    }

    private fun debugCheck() {
        if (debugMode in 5..9) {
            Toast.makeText(
                this,
                "Осталось до режима разработчика: " + (10 - debugMode),
                Toast.LENGTH_SHORT
            ).show()
        } else if (debugMode >= 10) {
            Toast.makeText(this, "Вы разработчик!", Toast.LENGTH_SHORT).show()
            findViewById<Button>(R.id.deleteAll).visibility = View.VISIBLE
        }
    }

    private fun getDirectory() {
        val pManager: PackageManager = packageManager
        val pName: String = packageName
        try {
            val p = pManager.getPackageInfo(pName, 0)
            directory = p.applicationInfo.dataDir
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w("KITWidget", "Error Package name not found ", e)
        }
    }

    private fun updateColumnsInReview(){
        findViewById<TextView>(R.id.col1).text = "Расписание на сегодня"

        findViewById<TextView>(R.id.col2).text = Jsoup.parse(file.readText())
            .select("#dni-109" + widgetDataUpdater.getDate().toString() + "> b:nth-child(2)")
            .text()

        findViewById<TextView>(R.id.col3).text = Jsoup.parse(file.readText())
            .select("#dni-109" + widgetDataUpdater.getDate().toString() + "> b:nth-child(8)")
            .text()

        findViewById<TextView>(R.id.col4).text = Jsoup.parse(file.readText())
            .select("#dni-109" + widgetDataUpdater.getDate().toString() + "> b:nth-child(14)")
            .text()

        findViewById<TextView>(R.id.col5).text = Jsoup.parse(file.readText())
            .select("#dni-109" + widgetDataUpdater.getDate().toString() + "> b:nth-child(20)")
            .text()
    }

    private fun parseHTML(context: Context) {
        val inputSite = File("$directory/temp.html")
        if (inputSite.exists()) {
            inputSite.delete()
            Log.d("[FS]", "$directory/temp.html deleted")
        }
        if (!inputSite.exists()) {
            inputSite.createNewFile()
            Log.d("[FS]", "$directory/temp.html created")
        }
        val ar = AsyncRequest()

        ar.takeMainThread(file, remoteViews, appWidgetManager, thisWidget, this, findViewById(R.id.root), context)
        ar.execute()
        //TODO("ОБЯЗАТЕЛЬНО ПОФИКСИТЬ ЗДЕСЬ ОШИБКА, если нет виджета то приложение вылетит!")

    }

    internal class AsyncRequest : AsyncTask<Void?, Void?, Void?>() { // Создаем поток Networking
        private lateinit var doc: Document
        private lateinit var file: File
        private lateinit var views: RemoteViews
        lateinit var appWidgetManager: AppWidgetManager
        lateinit var thisWidget: ComponentName
        var mainA: MainActivity = MainActivity()
        lateinit var rootView: View
        lateinit  var context: Context
        fun takeMainThread(
            inputSite: File,
            rv: RemoteViews,
            awm: AppWidgetManager,
            tw: ComponentName,
            ma: MainActivity,
            rootV: ConstraintLayout,
            ct: Context
        ) {
            file = inputSite
            views = rv
            appWidgetManager = awm
            thisWidget = tw
            mainA = ma
            rootView = rootV
            context = ct
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                //Считываем
                doc =
                    Jsoup.connect("http://www.spbkit.edu.ru/index.php?option=com_timetable&Itemid=82")
                        .get()
            } catch (e: IOException) {
                //Если не получилось считать
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            file.writeText(doc.html())

            val widgetDataUpdater = WidgetDataUpdater()
            widgetDataUpdater.update(appWidgetManager, thisWidget, views, file, context)
            widgetDataUpdater.updateMainPreview(rootView.findViewById(R.id.widgetBack), file)
            mainA.startUpdating() // Начинаем обновление
        }
    }
}