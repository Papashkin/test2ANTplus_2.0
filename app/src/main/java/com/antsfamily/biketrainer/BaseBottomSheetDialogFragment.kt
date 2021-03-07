package com.antsfamily.biketrainer

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.antsfamily.biketrainer.navigation.Route
import com.antsfamily.biketrainer.navigation.mapToDirection
import com.antsfamily.biketrainer.presentation.BaseViewModel
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    abstract val viewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigationEvents()
    }

    private fun observeNavigationEvents() {
        viewModel.navigationEvent.observe(viewLifecycleOwner, EventObserver { navigateTo(it) })
    }

    private fun navigateTo(route: Route) {
        NavHostFragment.findNavController(this).navigate(route.mapToDirection())
    }
}
