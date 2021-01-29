package com.antsfamily.biketrainer.ui.programs

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.databinding.FragmentProgramsBinding
import com.antsfamily.biketrainer.presentation.programs.ProgramsViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.util.mapDistinct
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProgramsFragment : BaseFragment(R.layout.fragment_programs) {

    override val viewModel: ProgramsViewModel by viewModels { withFactory(viewModelFactory) }

    @Inject
    lateinit var programsAdapter: ProgramsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentProgramsBinding.bind(view)) {
            observeState(this)
            observeEvents()
            bindInteractions(this)
        }
    }

    private fun observeState(binding: FragmentProgramsBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.isLoading }.observe(viewLifecycleOwner) {
                loadingView.isVisible = it
            }
            viewModel.state.mapDistinct { it.isProgramsVisible }.observe(viewLifecycleOwner) {
                programsRv.isVisible = it
            }
            viewModel.state.mapDistinct { it.isEmptyProgramsVisible }.observe(viewLifecycleOwner) {
                emptyProgramsListTv.isVisible = it
            }
            viewModel.state.mapDistinct { it.programs }.observe(viewLifecycleOwner) {
                programsAdapter.items = it
            }
        }
    }

    private fun observeEvents() {
        // TODO add events (click on profile; create profile; ...)
    }

    private fun bindInteractions(binding: FragmentProgramsBinding) {
        with(binding) {
            backBtn.setOnClickListener { viewModel.onBackClick() }
            addProgramBtn.setOnClickListener { viewModel.onAddProgramClick() }
            programsRv.adapter = programsAdapter.apply {
                setOnItemClickListener { viewModel.onProgramClick(it) }
                setOnLongItemClickListener { viewModel.onLongProgramClick(it) }
            }
        }
    }
}
