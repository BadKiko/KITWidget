package com.kikogames.kitwidget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import org.jsoup.Jsoup
import java.io.File
import java.util.*

class WidgetDataUpdater {
    var directory: String = ""
}