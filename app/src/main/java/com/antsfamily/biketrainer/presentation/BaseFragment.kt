package com.antsfamily.biketrainer.presentation

import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.pawegio.kandroid.inputMethodManager
import com.pawegio.kandroid.toast


open class BaseFragment(@LayoutRes layoutId: Int): Fragment(layoutId), BaseView {

    override fun showToast(text: String) {
        toast(text)
    }

    override fun showToast(id: Int) {
        toast(id)
    }

    override fun showKeyboard() {
        val inputMethodManager = activity?.inputMethodManager
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    override fun hideKeyboard() {
        val inputMethodManager = activity?.inputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}