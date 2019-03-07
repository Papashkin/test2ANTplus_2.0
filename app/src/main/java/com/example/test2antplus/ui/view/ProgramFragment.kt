package com.example.test2antplus.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.presenter.ProgramPresenter
import com.example.test2antplus.ui.adapter.ProgramAdapter
import kotlinx.android.synthetic.main.fragment_program.*

interface ProgramInterface {
    fun selectProgram()
    fun addNewProgram()
    fun deleteProgram()

}

class ProgramFragment: Fragment(), ProgramInterface {

    private lateinit var presenter: ProgramPresenter
    private lateinit var programAdapter: ProgramAdapter
    private lateinit var owner: LifecycleOwner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_program, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        owner = LifecycleOwner { lifecycle }
        presenter = ProgramPresenter(this, owner)

        programAdapter = ProgramAdapter()

        fabAddProgram.setOnClickListener {
            presenter.addProgram()
        }

    }

    override fun selectProgram() {
    }

    override fun addNewProgram() {
    }

    override fun deleteProgram() {
    }
}