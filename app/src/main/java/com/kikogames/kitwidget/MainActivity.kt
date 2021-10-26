package com.kikogames.kitwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.Text
import java.io.File
import java.io.IOException
import java.util.*
import android.widget.Toast

import android.widget.SeekBar

import android.widget.SeekBar.OnSeekBarChangeListener




class MainActivity : AppCompatActivity() {
    var directory: String? = null
    var debugMode = 0
    var appWidgetManager: AppWidgetManager? = null
    var remoteViews: RemoteViews? = null
    var thisWidget: ComponentName? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.hide()
        val mSharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editR = mSharedPrefs.edit()

        appWidgetManager = AppWidgetManager.getInstance(this)
        thisWidget = ComponentName(this, MainWidget::class.java)
        remoteViews = RemoteViews(packageName, R.layout.main_widget)

        getDirectory() // Получаем main директорию

        val fontSizeTextView = findViewById<TextView>(R.id.fontSizeTV)
        val seekBarFontSize = findViewById<SeekBar>(R.id.shrift)
        val updateButton = findViewById<Button>(R.id.update)
        val deleteButton = findViewById<Button>(R.id.deleteAll)
        val credits = findViewById<TextView>(R.id.createdBy)

        if(!mSharedPrefs.contains("fontSize")){
            editR.putInt("fontSize",14)
            editR.apply()
            Log.d("[SP]", "Put fontSize 14 | Contain fontSize - "+mSharedPrefs.contains("fontSize"))
        }
        else{
            seekBarFontSize.progress = mSharedPrefs.getInt("fontSize", 14)
            fontSizeTextView.text = "Размер шрифта виджета - "+seekBarFontSize.progress.toString()+"px"
        }

        seekBarFontSize.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fontSizeTextView.text = "Размер шрифта виджета - "+progress.toString()+"px"
                editR.putInt("fontSize", progress)
                editR.apply()
                Log.d("[SP]", "Put fontSize $progress | Contain fontSize - "+mSharedPrefs.contains("fontSize"))

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        credits.setOnClickListener{
            debugMode++
            debugCheck()
        }
        deleteButton.setOnClickListener{
            if(File("$directory/temp.html").exists()) {
                Log.d("[FS]", "$directory/temp.html deleted")
                File("$directory/temp.html").delete()
            }
            else{
                Log.d("[FS]", "Can't delete file, file isn't exists")
            }
        }
        updateButton.setOnClickListener {
            parseHTML(this);
        }
    }

    private fun startUpdating(){
        startService(Intent(this, UpdateWiget::class.java))
    }

    private fun debugCheck(){
        if(debugMode in 5..9){
            Toast.makeText(this, "Осталось до режима разработчика: "+(10-debugMode), Toast.LENGTH_SHORT).show()
        }
        else if (debugMode >= 10){
            Toast.makeText(this, "Вы разработчик!", Toast.LENGTH_SHORT).show()
            findViewById<Button>(R.id.deleteAll).visibility = View.VISIBLE
        }
    }

    private fun getDirectory(){
        val pManager: PackageManager = packageManager
        val pName: String = packageName
        try {
            val p = pManager.getPackageInfo(pName, 0)
            directory = p.applicationInfo.dataDir
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w("KITWidget", "Error Package name not found ", e)
        }
    }

    private fun parseHTML(context: Context) {
        val inputSite = File("$directory/temp.html")
        if(inputSite.exists()) {
            inputSite.delete()
            Log.d("[FS]", "$directory/temp.html deleted")
        }
        if(!inputSite.exists()) {
            inputSite.createNewFile()
            Log.d("[FS]", "$directory/temp.html created")
        }
        val ar = AsyncRequest()
        remoteViews?.let { appWidgetManager?.let { it1 ->
            thisWidget?.let { it2 ->
                ar.takeMainThread(inputSite, it,
                    it1, it2, this
                )
            }
        } }
        ar.execute()

        Log.d("[WIDGET INFO]", "WIDGET ID = "+appWidgetManager?.getAppWidgetIds(thisWidget)!![0].toString())
        //TODO("ОБЯЗАТЕЛЬНО ПОФИКСИТЬ ЗДЕСЬ ОШИБКА, если нет виджета то приложение вылетит!")

    }

    internal class AsyncRequest : AsyncTask<Void?, Void?, Void?>() { // Создаем поток Networking
        private var doc: Document? = null
        private var file: File? = null
        private var views: RemoteViews? = null
        var appWidgetManager: AppWidgetManager? = null
        var thisWidget: ComponentName? = null
        var mainA: MainActivity = MainActivity()
        fun takeMainThread(inputSite: File, rv: RemoteViews, awm: AppWidgetManager, tw: ComponentName, ma: MainActivity){
            file = inputSite
            views=rv
            appWidgetManager = awm
            thisWidget = tw
            mainA=ma
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                //Считываем
                doc = Jsoup.connect("http://www.spbkit.edu.ru/index.php?option=com_timetable&Itemid=82").get()
            } catch (e: IOException) {
                //Если не получилось считать
                e.printStackTrace()
            }
            return null
        }
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            doc?.html()?.let { file?.writeText(it) }
            mainA.startUpdating()
        }
    }
}