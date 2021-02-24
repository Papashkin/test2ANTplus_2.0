package com.antsfamily.biketrainer.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.antsfamily.biketrainer.navigation.Route
import com.antsfamily.biketrainer.navigation.mapToDirection
import com.antsfamily.biketrainer.presentation.BaseViewModel
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.ViewModelFactory
import com.antsfamily.biketrainer.ui.extensions.addDismissListener
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    abstract val viewModel: BaseViewModel

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEvents()
    }

    protected fun showSnackBar(
        @StringRes messageRes: Int,
        @StringRes actionLabelRes: Int? = null,
        actionClickListener: ((View) -> Unit)? = null,
        dismissListener: (() -> Unit)? = null,
        indefinite: Boolean = false,
    ) {
        showSnackBar(
            getString(messageRes),
            actionLabelRes,
            actionClickListener,
            dismissListener,
            indefinite
        )
    }

    protected fun showSnackBar(
        message: String,
        @StringRes actionLabelRes: Int? = null,
        actionClickListener: ((View) -> Unit)? = null,
        dismissListener: (() -> Unit)? = null,
        inDefinite: Boolean = false,
    ) {
        val duration = if (inDefinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG
        snackbar = Snackbar.make(requireView(), message, duration)
            .apply {
                actionLabelRes?.let { setAction(it, actionClickListener) }
                dismissListener?.let { addDismissListener(it) }
            }
            .also { it.show() }
    }

    fun hideSnackBar() {
        snackbar?.dismiss()
    }

    private fun observeEvents() {
        viewModel.navigationEvent.observe(viewLifecycleOwner, EventObserver {
            navigateTo(it)
        })
        viewModel.navigationBackEvent.observe(viewLifecycleOwner, EventObserver {
            navigateBack()
        })
        viewModel.showErrorSnackBarEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        viewModel.showErrorSnackBarMessageEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
    }

    private fun navigateTo(route: Route) {
        getNavController()?.navigate(route.mapToDirection())
    }

    private fun navigateBack() {
        getNavController()?.navigateUp()
    }

    private fun getNavController(): NavController? {
        val controller = findNavController()
        when (val destination = controller.currentDestination) {
            is FragmentNavigator.Destination -> {
                if (destination.className == this::class.java.name) {
                    return controller
                }
            }
            // in case of navigating from one BottomSheetDialogFragment to another
            is DialogFragmentNavigator.Destination -> {
                if (destination.className == this::class.java.name) {
                    return controller
                }
            }
        }
        return null
    }
}
