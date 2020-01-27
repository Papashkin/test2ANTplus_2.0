package com.example.test2antplus.presentation.programs

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.test2antplus.MainApplication
import com.example.test2antplus.MainApplication.Companion.ACTION_WORK_SENDING
import com.example.test2antplus.MainApplication.Companion.ARGS_PROGRAM
import com.example.test2antplus.R
import com.example.test2antplus.data.repositories.programs.Program
import com.example.test2antplus.presentation.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import kotlinx.android.synthetic.main.fragment_programs.*


class ProgramsFragment : BaseFragment(R.layout.fragment_programs), ProgramsView {
    companion object {
        const val WORK = "it's time to work"
        const val PROFILE_NAME = "profile name"

        fun newInstance(isTime2work: Boolean, profile: String): ProgramsFragment = ProgramsFragment().apply {
            arguments = Bundle().apply {
                isTime2work to WORK
                profile to PROFILE_NAME
//                putBoolean(WORK, isTime2work)
//                putString(PROFILE_NAME, profile)
            }
        }
    }

    private lateinit var presenter: ProgramsPresenter
    private lateinit var programsAdapter: ProgramsAdapter
    private lateinit var programCallback: ItemTouchHelper.Callback

    private var isTime2work: Boolean = false
    private var profileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.graph.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = ProgramsPresenter(this)

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
        setAdapter()
        setListeners()
    }

    private fun setAdapter() {
        programsAdapter = ProgramsAdapter(
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

        programCallback = ProgramsSwipeCallback(programsAdapter)
        ItemTouchHelper(programCallback).attachToRecyclerView(listPrograms)
        listPrograms.adapter = programsAdapter
    }

    private fun setListeners() {
        toolbarPrograms.setNavigationOnClickListener {
            presenter.onBackPressed()
        }
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
        programsAdapter.setProgramList(programsList)
        hideLoading()
    }

    override fun chooseProgramAndCloseScreen(programName: String) {
        activity?.sendBroadcast(Intent(ACTION_WORK_SENDING).apply {
            this.putExtra(ARGS_PROGRAM, programName)
        })
    }

    override fun updateAdapter() {
        programsAdapter.undoDelete()
    }

    override fun showSnackBar(programName: String) {
        Snackbar
            .make(programsListLayout, "Program \"$programName\" was deleted", Snackbar.LENGTH_LONG)
            .setActionTextColor(Color.YELLOW)
            .setAction("UNDO") {
                presenter.undoDelete()
            }
            .show()
    }
}