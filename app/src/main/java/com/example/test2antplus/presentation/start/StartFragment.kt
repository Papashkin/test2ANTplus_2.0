package com.example.test2antplus.presentation.start

import android.os.Bundle
import android.view.View
import com.example.test2antplus.MainApplication
import com.example.test2antplus.MainApplication.Companion.PERMISSION_FOR_APP
import com.example.test2antplus.R
import com.example.test2antplus.presentation.BaseFragment
import kotlinx.android.synthetic.main.fragment_start.*


class StartFragment : BaseFragment(R.layout.fragment_start), StartView {

    private lateinit var presenter: StartPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.graph.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = StartPresenter(this)

        setListeners()
    }

    private fun setListeners() {
        btnProfiles.setOnClickListener {
            presenter.onProfileClick()
        }

        btnPrograms.setOnClickListener {
            presenter.onProgramClick()
        }
    }

    override fun requestPermissions(permissions: Array<String>) {
        requestPermissions(permissions, PERMISSION_FOR_APP)
    }

}