package com.antsfamily.biketrainer.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.antsfamily.biketrainer.presentation.BaseViewModel
import com.antsfamily.biketrainer.presentation.ViewModelFactory
import javax.inject.Inject


abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    abstract val viewModel: BaseViewModel

    protected fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    protected fun showToast(id: Int) {
        Toast.makeText(requireContext(), getString(id), Toast.LENGTH_SHORT).show()
    }

    protected fun showKeyboard() {
        val manager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    protected fun hideKeyboard() {
        val manager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
