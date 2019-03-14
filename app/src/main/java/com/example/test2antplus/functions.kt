package com.example.test2antplus

import android.app.Activity
import android.app.Dialog
import android.view.View
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_program.view.*
import java.util.concurrent.TimeUnit

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
    val hours = TimeUnit.SECONDS.toHours(this)
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

fun <T> Observable<T>.workInAsinc(): Observable<T> =
    this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())