package com.example.test2antplus.ui.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.test2antplus.MainApplication
import com.example.test2antplus.MainApplication.Companion.ACTION_PROGRAM_SETTINGS
import com.example.test2antplus.R
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.presenter.ProgramPresenter
import com.example.test2antplus.ui.adapter.ProgramAdapter
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import kotlinx.android.synthetic.main.fragment_programs.*


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

//    private var dialog: Dialog? = null

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
        return inflater.inflate(R.layout.fragment_programs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showLoading()
        presenter = ProgramPresenter(this)

        toolbarPrograms.setNavigationIcon(R.drawable.ic_arrow_back_32)
        toolbarPrograms.setNavigationOnClickListener {
            presenter.onBackPressed()
        }

        programAdapter = ProgramAdapter(
            onDeleteClick = {
                AlertDialog.Builder(context!!)
                    .setTitle("Attention!")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        presenter.onDeleteClick(it)
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            },
            onEditClick = {
                presenter.onEditeClick(it)
            }
        )
        listPrograms.adapter = programAdapter

        buttonAddProgram.setOnClickListener {
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
        pbPrograms.show()
    }

    override fun hideLoading() {
        pbPrograms.hide()
    }

    override fun hideEmptyProgramsList() {
        emptyListPrograms.visibility = View.GONE
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