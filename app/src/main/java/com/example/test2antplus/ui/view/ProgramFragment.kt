package com.example.test2antplus.ui.view

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test2antplus.MainApplication
import com.example.test2antplus.MainApplication.Companion.ACTION_PROGRAM_SETTINGS
import com.example.test2antplus.R
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.presenter.ProgramPresenter
import com.example.test2antplus.showDialog
import com.example.test2antplus.ui.adapter.ProgramAdapter
import kotlinx.android.synthetic.main.fragment_program.*


interface ProgramInterface {
    fun selectProgram()
    fun addNewProgram()
    fun deleteProgram()
    fun showLoading()
    fun hideLoading()

    fun setProgramsList(programsList: ArrayList<Program>)

    fun showProgramsList()
    fun hideProgramsList()
    fun showEmptyProgramsList()
    fun hideEmptyProgramsList()
}

class ProgramFragment : Fragment(), ProgramInterface {

    private lateinit var presenter: ProgramPresenter
    private lateinit var programAdapter: ProgramAdapter

    private var dialog: Dialog? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val extra = intent?.getStringExtra(ACTION_PROGRAM_SETTINGS)
            when (extra) {
                MainApplication.UPD_PROGRAMS_LIST -> {
                    // do something
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_program, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showLoading()
        presenter = ProgramPresenter(this)

        programAdapter = ProgramAdapter()
        listPrograms.adapter = programAdapter

        fabAddProgram.setOnClickListener {
            presenter.addProgram()
        }

        activity?.registerReceiver(receiver, IntentFilter(ACTION_PROGRAM_SETTINGS))
    }

    override fun selectProgram() {
    }

    override fun addNewProgram() {
    }

    override fun deleteProgram() {
    }

    override fun showLoading() {
        hideEmptyProgramsList()
        hideProgramsList()
        dialog = showDialog(requireActivity(), "Идет загрузка, подождите ...")
    }

    override fun hideLoading() {
        dialog?.dismiss()
    }

    override fun hideEmptyProgramsList() {
        emptyListPrograms.visibility = View.INVISIBLE
    }

    override fun showEmptyProgramsList() {
        emptyListPrograms.visibility = View.VISIBLE
    }

    override fun hideProgramsList() {
        listPrograms.visibility = View.INVISIBLE
    }

    override fun showProgramsList() {
        listPrograms.visibility = View.VISIBLE
    }

    override fun setProgramsList(programsList: ArrayList<Program>) {
        programAdapter.setProgramList(programsList)
        hideLoading()
    }
}