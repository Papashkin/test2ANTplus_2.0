package com.example.test2antplus.ui.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.test2antplus.MainApplication
import com.example.test2antplus.Program
import com.example.test2antplus.R
import com.example.test2antplus.presenter.ProgramPresenter
import com.example.test2antplus.showDialog
import com.example.test2antplus.ui.adapter.ProgramAdapter
import com.pawegio.kandroid.runDelayed
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

    companion object {
        const val DIALOG_DELAY = 200L // delay for loading dialog
    }

    private lateinit var presenter: ProgramPresenter
    private lateinit var programAdapter: ProgramAdapter
    private lateinit var owner: LifecycleOwner

    private var dialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_program, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        owner = LifecycleOwner { lifecycle }
        presenter = ProgramPresenter(this, owner)

        programAdapter = ProgramAdapter()
        listPrograms.adapter = programAdapter

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

    override fun showLoading() {
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
        runDelayed(DIALOG_DELAY) {
            hideLoading()
        }
    }
}