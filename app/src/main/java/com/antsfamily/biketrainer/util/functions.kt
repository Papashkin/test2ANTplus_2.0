package com.antsfamily.biketrainer.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * Time in format "00:00"
 */
fun Long.timeFormat(): String {
    val minutes = TimeUnit.SECONDS.toMinutes(this) - (TimeUnit.SECONDS.toHours(this) * 60)
    val seconds = TimeUnit.SECONDS.toSeconds(this) - (TimeUnit.SECONDS.toMinutes(this) * 60)

    return String.format("%2d:%02d", minutes, seconds)
}

/**
 * Time in format "0:00:00"
 */
fun Long.fullTimeFormat(): String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val minutes = TimeUnit.SECONDS.toMinutes(this) - (TimeUnit.SECONDS.toHours(this) * 60)
    val seconds = TimeUnit.SECONDS.toSeconds(this) - (TimeUnit.SECONDS.toMinutes(this) * 60)

    return String.format("%2d:%02d:%02d", hours, minutes, seconds)
}

/**
 * Standard params of bar chart setting
 */
fun BarChart.setCommonParams(data: BarData, timeLabels: List<String>) = this.also {
    it.setScaleEnabled(false)
    it.setTouchEnabled(true)
    it.description = null

    it.xAxis.labelCount = timeLabels.size
    it.xAxis.axisMinimum = -0.5f
    it.xAxis.axisMaximum = timeLabels.size.toFloat() - 0.5f
    it.xAxis.setCenterAxisLabels(false)
    it.xAxis.isEnabled = false

    it.axisRight.isEnabled = false

    it.data = data
    it.data.barWidth = 1f
    it.data.setValueTextSize(8f)
//    it.data.setValueFormatter { _, entry, _, _ ->
//        timeLabels[entry.x.toInt()]
//    }
}

/**
 * Workout params of bar chart setting
 */
fun BarChart.setWorkParams(data: BarData) = this.also {
    it.setScaleEnabled(false)
    it.setTouchEnabled(false)
    it.description = null

    it.legend.isEnabled = false

    it.xAxis.isEnabled = false
    it.axisRight.isEnabled = false
    it.axisLeft.isEnabled = false

    it.data = data
    it.data.barWidth = 1f
    it.data.setValueTextSize(8f)
}

fun String.convertToLatinScript(): String {
    val russianScript =
        "а б в г д е ё ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я".split(" ")
    val latinScript =
        "a b v g d e yo zh z i y k l m n o p r s t u f kh ts ch sh shch \" y ' e yu ya".split(" ")
    var convertedText = ""

    return try {
        for (i in this) {
            convertedText += latinScript[russianScript.indexOf(i.toString())]
        }
        convertedText
    } catch (ex: Exception) {
        this
    }
}

fun BarChart.saveProgramAsImage(name: String): Boolean {
    try {
        if (File(name).length() > 0) {
            File(name).delete()
        }
        val fos = FileOutputStream(name)

        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val drawable = this.background
        if (drawable != null) {
            drawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        draw(canvas)

        bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos)

        fos.flush()
        fos.close()

        val size = File(name).length()
        return if (size > 0) {
            bitmap.recycle()
            true
        } else {
            false
        }
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
        return false
    }
}
