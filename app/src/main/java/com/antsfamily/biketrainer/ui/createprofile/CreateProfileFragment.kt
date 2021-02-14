package com.antsfamily.biketrainer.ui.createprofile

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
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
            viewModel.state.mapDistinct { it.genders }.observe(viewLifecycleOwner) {
                genderActv.setAdapter(getAdapter(it))
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
                genderTil.error = it
            }
        }
    }

    private fun observeEvents(binding: FragmentCreateProfileBinding) {
        viewModel.clearFieldsEvent.observe(viewLifecycleOwner, EventObserver {
            binding.clearFields()
        })
        viewModel.showSnackBarEvent.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
                .show()
        })
    }

    private fun bindInteractions(binding: FragmentCreateProfileBinding) {
        with(binding) {
            createBtn.setOnClickListener { createProfile() }
            usernameEt.afterTextChange { viewModel.onUsernameChange() }
            ageEt.afterTextChange { viewModel.onAgeChange() }
            weightEt.afterTextChange { viewModel.onWeightChange() }
            heightEt.afterTextChange { viewModel.onHeightChange() }
            genderActv.apply {
                afterTextChange { viewModel.onGenderChange() }
                onItemClickListener = AdapterView.OnItemClickListener { _, _, index, _ ->
                    viewModel.onGenderSelected(index)
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        p0: AdapterView<*>?, view: View?, index: Int, p3: Long
                    ) {
                        viewModel.onGenderSelected(index)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        viewModel.onGenderCleared()
                    }
                }
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
        genderActv.clearListSelection()
        genderActv.setText("")
    }
}
