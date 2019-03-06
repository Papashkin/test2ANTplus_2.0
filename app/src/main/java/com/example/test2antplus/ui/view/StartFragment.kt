package com.example.test2antplus.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.presenter.StartPresenter
import kotlinx.android.synthetic.main.fragment_start.*

interface StartInterface {}

class StartFragment : Fragment(), StartInterface {
    private lateinit var presenter: StartPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = StartPresenter()

        btnProfiles.setOnClickListener {
            presenter.onProfileClick()
        }

        btnPrograms.setOnClickListener {
            presenter.onProgramClick()
        }
    }

}