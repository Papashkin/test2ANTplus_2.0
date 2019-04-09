package com.example.test2antplus.ui.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.test2antplus.MainApplication
import com.example.test2antplus.MainApplication.Companion.ACTION_WORK_SENDING
import com.example.test2antplus.MainApplication.Companion.ARGS_PROGRAM
import com.example.test2antplus.R
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.presenter.ProgramPresenter
import com.example.test2antplus.ui.adapter.program.ProgramAdapter
import com.example.test2antplus.ui.adapter.program.ProgramSwipeCallback
import com.google.android.material.snackbar.Snackbar
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import kotlinx.android.synthetic.main.fragment_programs.*


interface ProgramInterface {
    fun selectProgram()
    fun showLoading()
    fun hideLoading()

    fun setProgramsList(programsList: ArrayList<Program>)

    fun showProgramsList()
    fun hideProgramsList()

    fun chooseProgramAndCloseScreen(programName: String)

    fun updateAdapter()

    fun showSnackbar(programName: String)
}

class ProgramFragment : BaseFragment(), ProgramInterface {
    companion object {
        const val WORK = "it's time to work"
        const val PROFILE_NAME = "profile name"
    }

    private lateinit var presenter: ProgramPresenter
    private lateinit var programAdapter: ProgramAdapter
    private lateinit var programCallback: ItemTouchHelper.Callback

    private var isTime2work: Boolean = false
    private var profileName: String = ""

    fun newInstance(isTime2work: Boolean, profile: String): ProgramFragment = ProgramFragment().apply {
        arguments = Bundle().apply {
            putBoolean(WORK, isTime2work)
            putString(PROFILE_NAME, profile)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_programs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showLoading()
        presenter = ProgramPresenter(this)

        this.arguments?.apply {
            isTime2work = this.getBoolean(WORK)
            profileName = this.getString(PROFILE_NAME, "")
        }

        if (profileName.isNotEmpty()) {
            toolbarPrograms.setTitle(R.string.toolbar_select_program)
        } else {
            toolbarPrograms.setTitle(R.string.toolbar_programs)
        }

        toolbarPrograms.setNavigationIcon(R.drawable.ic_arrow_back_32)
        toolbarPrograms.setNavigationOnClickListener {
            presenter.onBackPressed()
        }

        programAdapter = ProgramAdapter(
            onDeleteClick = { position ->
                presenter.onDeleteClick(position)
            },
            onEditClick = {
                presenter.onEditClick(it)
            },
            onItemClick = {
                if (isTime2work) {
                    presenter.setWorkOut(it, profileName)
                }
            })

        programCallback = ProgramSwipeCallback(programAdapter)
        ItemTouchHelper(programCallback).attachToRecyclerView(listPrograms)
        listPrograms.adapter = programAdapter

        buttonAddProgram.setOnClickListener {
            presenter.addProgram()
        }

    }

    override fun selectProgram() {
    }

    override fun showLoading() {
        pbPrograms.show()
    }

    override fun hideLoading() {
        pbPrograms.hide()
    }

    override fun hideProgramsList() {
        emptyListPrograms.visibility = View.VISIBLE
        listPrograms.visibility = View.GONE
    }

    override fun showProgramsList() {
        emptyListPrograms.visibility = View.GONE
        listPrograms.visibility = View.VISIBLE
    }

    override fun setProgramsList(programsList: ArrayList<Program>) {
        programAdapter.setProgramList(programsList)
        hideLoading()
    }

    override fun chooseProgramAndCloseScreen(programName: String) {
        activity?.sendBroadcast(Intent(ACTION_WORK_SENDING).apply {
            this.putExtra(ARGS_PROGRAM, programName)
        })
    }

    override fun updateAdapter() {
        programAdapter.undoDelete()
    }

    override fun showSnackbar(programName: String) {
        Snackbar
            .make(programsListLayout, "Program \"$programName\" was deleted", Snackbar.LENGTH_LONG)
            .setActionTextColor(Color.YELLOW)
            .setAction("UNDO") {
                presenter.undoDelete()
            }
            .show()
    }
}