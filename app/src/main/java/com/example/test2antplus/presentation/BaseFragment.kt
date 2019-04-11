package com.example.test2antplus.presentation

import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.test2antplus.presentation.presenter.BaseView
import com.pawegio.kandroid.inputMethodManager
import com.pawegio.kandroid.toast


open class BaseFragment: Fragment(), BaseView {
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