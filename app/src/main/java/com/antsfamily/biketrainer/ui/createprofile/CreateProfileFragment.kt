package com.antsfamily.biketrainer.ui.createprofile

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.databinding.FragmentCreateProfileBinding
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.createprofile.CreateProfileViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.ui.util.afterTextChange
import com.antsfamily.biketrainer.ui.util.resourceId
import com.antsfamily.biketrainer.util.mapDistinct
import com.antsfamily.biketrainer.util.orZero
import com.garmin.fit.Gender
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateProfileFragment : BaseFragment(R.layout.fragment_create_profile) {

    override val viewModel: CreateProfileViewModel by viewModels { withFactory(viewModelFactory) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentCreateProfileBinding.bind(view)) {
            observeState(this)
            observeEvents(this)
            bindInteractions(this)
        }
    }

    private fun observeState(binding: FragmentCreateProfileBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.isLoading }.observe(viewLifecycleOwner) {
                loadingView.isVisible = it
            }
            viewModel.state.mapDistinct { it.usernameError }.observe(viewLifecycleOwner) {
                usernameTil.error = it
            }
            viewModel.state.mapDistinct { it.ageError }.observe(viewLifecycleOwner) {
                ageTil.error = it
            }
            viewModel.state.mapDistinct { it.weightError }.observe(viewLifecycleOwner) {
                weightTil.error = it
            }
            viewModel.state.mapDistinct { it.heightError }.observe(viewLifecycleOwner) {
                heightTil.error = it
            }
            viewModel.state.mapDistinct { it.genderError }.observe(viewLifecycleOwner) {
                setupGenderError(it)
            }
        }
    }

    private fun observeEvents(binding: FragmentCreateProfileBinding) {
        viewModel.clearFieldsEvent.observe(viewLifecycleOwner, EventObserver {
            binding.clearFields()
        })
        viewModel.showSuccessSnackBarEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it, dismissListener = { viewModel.navigateForward() })
        })
        viewModel.showSuccessSnackBarMessageEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it, dismissListener = { viewModel.navigateForward() })
        })
    }

    private fun bindInteractions(binding: FragmentCreateProfileBinding) {
        with(binding) {
            createBtn.setOnClickListener { createProfile() }
            usernameEt.afterTextChange { viewModel.onUsernameChange() }
            ageEt.afterTextChange { viewModel.onAgeChange() }
            weightEt.afterTextChange { viewModel.onWeightChange() }
            heightEt.afterTextChange { viewModel.onHeightChange() }
            genderGroup.apply {
                femaleRb.setOnClickListener { viewModel.onFemaleGenderSelected() }
                maleRb.setOnClickListener { viewModel.onMaleGenderSelected() }
            }
        }
    }

    private fun FragmentCreateProfileBinding.createProfile() {
        viewModel.onCreateClick(
            usernameEt.text.toString(),
            ageEt.text.toString().toIntOrNull().orZero(),
            weightEt.text.toString().toBigDecimalOrNull(),
            heightEt.text.toString().toBigDecimalOrNull()
        )
    }

    private fun getAdapter(items: List<Gender>) = ArrayAdapter(
        requireContext(),
        R.layout.card_dropdown_item,
        items.map { getString(it.resourceId()) }
    )

    private fun FragmentCreateProfileBinding.clearFields() {
        usernameEt.text = null
        ageEt.text = null
        weightEt.text = null
        heightEt.text = null
        genderGroup.clearCheck()
    }

    private fun FragmentCreateProfileBinding.setupGenderError(error: String?) {
        with(genderErrorTv) {
            isVisible = !error.isNullOrBlank()
            text = error
        }
    }
}
