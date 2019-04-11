package com.example.test2antplus.presentation.view.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test2antplus.MainApplication
import com.example.test2antplus.MainApplication.Companion.PERMISSION_FOR_APP
import com.example.test2antplus.R
import com.example.test2antplus.presentation.BaseFragment
import com.example.test2antplus.presentation.presenter.start.StartPresenter
import kotlinx.android.synthetic.main.fragment_start.*


class StartFragment : BaseFragment(), StartView {

    private lateinit var presenter: StartPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.graph.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_start, container, false)

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