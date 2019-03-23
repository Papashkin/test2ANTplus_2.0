package com.example.test2antplus

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.test2antplus.data.profiles.Profile
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_program.view.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

/**
 * Отображает окно с текстом
 */
fun showDialog(activity: Activity, text: String): Dialog? {
    if (!activity.isFinishing) {
        val contentView = View.inflate(activity, R.layout.dialog_program, null)
        val dialog = AlertDialog.Builder(activity).create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.setContentView(contentView)

        contentView.vTextMessage.text = text
        return dialog
    }
    return null
}

/**
 * Время в формате "00:00"
 */
fun Long.timeFormat(): String {
    val minutes = TimeUnit.SECONDS.toMinutes(this) - (TimeUnit.SECONDS.toHours(this) * 60)
    val seconds = TimeUnit.SECONDS.toSeconds(this) - (TimeUnit.SECONDS.toMinutes(this) * 60)

    return String.format("%2d:%02d", minutes, seconds)
}

/**
 * Время в формате "0:00:00"
 */
fun Long.fullTimeFormat(): String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val minutes = TimeUnit.SECONDS.toMinutes(this) - (TimeUnit.SECONDS.toHours(this) * 60)
    val seconds = TimeUnit.SECONDS.toSeconds(this) - (TimeUnit.SECONDS.toMinutes(this) * 60)

    return String.format("%2d:%02d:%02d", hours, minutes, seconds)
}

fun <T> Single<T>.workInAsinc(): Single<T> =
    this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

fun <T> Flowable<T>.workInAsinc(): Flowable<T> =
    this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

fun <T> Observable<T>.workInAsinc(): Observable<T> =
    this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

/**
 * Установка стандартных параметров гистограммы
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
    it.data.setValueFormatter { _, entry, _, _ ->
            timeLabels[entry.x.toInt()]
    }
}

fun String.convertToLatinScript(): String {
    val russianScript = "а б в г д е ё ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я".split(" ")
    val latinScript = "a b v g d e yo zh z i y k l m n o p r s t u f kh ts ch sh shch \" y ' e yu ya".split(" ")
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

        bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos)

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

fun Profile.isFilled(): Boolean = this.getName().isNotEmpty() && this.getAge() != 0 && this.getWeight() != 0.0F && this.getHeight() != 0.0F && this.getGender() != ""

fun Profile.isSomethingFilled(): Boolean = this.getName().isNotEmpty() || this.getAge() != 0 || this.getWeight() != 0.0F || this.getHeight() != 0.0F || this.getGender() != ""