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

fun String.getTwoDigits() = if (this.length < 2) "0$this" else this

fun Long.formatToTime(): String {
    val hours = (this / 3600).toInt().toString()
    var minutes = ((this % 3600) / 60).toInt().toString()
    if (minutes.length < 2) minutes = "0$minutes"
    minutes = minutes.getTwoDigits()
    var seconds= ((this) / 3600).toInt().toString()
    seconds = seconds.getTwoDigits()

    return "$hours:$minutes:$seconds"
}

fun <T> Single<T>.workInAsinc(): Single<T> =
    this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

fun <T> Observable<T>.workInAsinc(): Observable<T> =
    this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())