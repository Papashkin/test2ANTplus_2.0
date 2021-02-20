package com.antsfamily.biketrainer.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.profile.Profile
import com.antsfamily.biketrainer.databinding.FragmentHomeBinding
import com.antsfamily.biketrainer.presentation.home.HomeViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.ui.home.adapter.CreateProgramAdapter
import com.antsfamily.biketrainer.ui.home.adapter.ProgramsAdapter
import com.antsfamily.biketrainer.ui.util.iconId
import com.antsfamily.biketrainer.util.mapDistinct
import com.garmin.fit.Gender
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override val viewModel: HomeViewModel by viewModels { withFactory(viewModelFactory) }

    @Inject
    lateinit var programsAdapter: ProgramsAdapter

    @Inject
    lateinit var createProgramAdapter: CreateProgramAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentHomeBinding.bind(view)) {
            observeState(this)
            observeFlow(this)
            bindInteractions(this)
        }
    }

    private fun observeState(binding: FragmentHomeBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.dateTime }
                .observe(viewLifecycleOwner) { homeDateTimeTv.text = it }
        }
    }

    private fun observeFlow(binding: FragmentHomeBinding) {
        with(binding) {
            viewModel.profileWithProgramsFlow.observe(viewLifecycleOwner) {
                setupProfile(it.profile)
                programsAdapter.items = it.programs
                homeProgramsRv.isVisible = it.programs.isNotEmpty()
                emptyProgramsCl.isVisible = it.programs.isEmpty()
            }
        }
    }

    private fun FragmentHomeBinding.setupProfile(profile: Profile?) {
        profile?.let {
            homeProfileNameTv.text = it.name
            homeProfileIconIv.setImageResource(Gender.valueOf(it.gender).iconId())
        }
    }

    private fun bindInteractions(binding: FragmentHomeBinding) {
        with(binding) {
            createProgramBtn.setOnClickListener { viewModel.onCreateProgramClick() }
            settingsIb.setOnClickListener { viewModel.onSettingsClick() }
            createProgramAdapter.apply {
                setOnCreateProgramClickListener { viewModel.onCreateProgramClick() }
            }
            programsAdapter.apply {
                setOnItemClickListener { viewModel.onProgramClick(it) }
            }
            homeProgramsRv.adapter = ConcatAdapter(programsAdapter, createProgramAdapter)
        }
    }
}
