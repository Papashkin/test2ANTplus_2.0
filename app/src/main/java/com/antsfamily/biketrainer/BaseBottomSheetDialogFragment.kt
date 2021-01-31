package com.antsfamily.biketrainer

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.navigation.fragment.NavHostFragment
import com.antsfamily.biketrainer.navigation.Route
import com.antsfamily.biketrainer.navigation.mapToDirection
import com.antsfamily.biketrainer.presentation.BaseViewModel
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    abstract val viewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeErrorEvents()
        observeNavigationEvents()
    }

    private fun observeErrorEvents() {
//        viewModel.showErrorMessageEvent.observe(viewLifecycleOwner, EventObserver {
//            showSnackBar(it)
//        })
//        viewModel.showErrorTextResourceEvent.observe(viewLifecycleOwner, EventObserver {
//            showSnackBar(it.resourceId())
//        })
    }

    private fun observeNavigationEvents() {
        viewModel.navigationEvent.observe(viewLifecycleOwner, EventObserver { navigateTo(it) })
    }

    private fun navigateTo(route: Route) {
        NavHostFragment.findNavController(this).navigate(route.mapToDirection())
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
        Snackbar.make(requireView(), message, duration).apply {
            actionLabelRes?.let { setAction(it, actionClickListener) }
//            dismissListener?.let { addDismissListener(it) }
        }.show()
    }
}
