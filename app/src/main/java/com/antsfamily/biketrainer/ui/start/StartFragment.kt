package com.antsfamily.biketrainer.ui.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.databinding.FragmentStartBinding
import com.antsfamily.biketrainer.presentation.start.StartViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartFragment : BaseFragment(R.layout.fragment_start) {

    override val viewModel: StartViewModel by viewModels { withFactory(viewModelFactory) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentStartBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentStartBinding.bind(view)) {
            bindInteractions(this)
        }
    }

    private fun bindInteractions(binding: FragmentStartBinding) {
        with(binding) {
            profilesBtn.setOnClickListener { viewModel.onProfileClick() }
            programsBtn.setOnClickListener { viewModel.onProgramClick() }
        }
    }
}
