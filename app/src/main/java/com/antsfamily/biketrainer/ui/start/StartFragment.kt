package com.antsfamily.biketrainer.ui.start

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.presentation.start.StartViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : BaseFragment(R.layout.fragment_start) {

    override val viewModel: StartViewModel by viewModels { withFactory(viewModelFactory)  }

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.graph.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnProfiles.setOnClickListener { viewModel.onProfileClick() }
        btnPrograms.setOnClickListener { viewModel.onProgramClick() }
    }
}
