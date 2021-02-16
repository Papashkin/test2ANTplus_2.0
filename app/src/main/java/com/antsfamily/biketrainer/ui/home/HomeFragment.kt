package com.antsfamily.biketrainer.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.Profile
import com.antsfamily.biketrainer.databinding.FragmentHomeBinding
import com.antsfamily.biketrainer.presentation.home.HomeViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.ui.home.adapter.CreateProgramAdapter
import com.antsfamily.biketrainer.ui.programs.ProgramsAdapter
import com.antsfamily.biketrainer.ui.util.iconId
import com.antsfamily.biketrainer.ui.util.isShimmering
import com.antsfamily.biketrainer.util.mapDistinct
import com.garmin.fit.Gender
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val args: HomeFragmentArgs by navArgs()

    override val viewModel: HomeViewModel by viewModels { withFactory(viewModelFactory) }

    @Inject
    lateinit var programsAdapter: ProgramsAdapter

    @Inject
    lateinit var createProgramAdapter: CreateProgramAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated(args.profileName)
        with(FragmentHomeBinding.bind(view)) {
            observeState(this)
            bindInteractions(this)
        }
    }

    private fun observeState(binding: FragmentHomeBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.isLoading }.observe(viewLifecycleOwner) {
                programsLoading.programsShimmerFl.isShimmering = it
                programsLoading.programsShimmerFl.isVisible = it
            }
            viewModel.state.mapDistinct { it.isProgramsVisible }
                .observe(viewLifecycleOwner) { homeProgramsRv.isVisible = it }
            viewModel.state.mapDistinct { it.isEmptyProgramsVisible }
                .observe(viewLifecycleOwner) { emptyProgramsCl.isVisible = it }
            viewModel.state.mapDistinct { it.programs }
                .observe(viewLifecycleOwner) { programsAdapter.items = it }
            viewModel.state.mapDistinct { it.profile }
                .observe(viewLifecycleOwner) { setupProfile(it) }
            viewModel.state.mapDistinct { it.dateTime }
                .observe(viewLifecycleOwner) { homeDateTimeTv.text = it }
        }
    }

    private fun FragmentHomeBinding.setupProfile(profile: Profile?) {
        profile?.let {
            homeProfileNameTv.text = it.getName()
            homeProfileIconIv.setImageResource(Gender.valueOf(it.getGender()).iconId())
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
