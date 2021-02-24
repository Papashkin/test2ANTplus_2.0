package com.antsfamily.biketrainer.ui.extensions

import com.google.android.material.snackbar.Snackbar

fun Snackbar.addDismissListener(dismissListener: (() -> Unit)) {
    addCallback(object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            dismissListener()
        }
    })
}
