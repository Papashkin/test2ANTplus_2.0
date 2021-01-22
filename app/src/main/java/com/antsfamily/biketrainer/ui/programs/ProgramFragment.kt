package com.antsfamily.biketrainer.ui.programs

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.antsfamily.biketrainer.MainApplication.Companion.ACTION_WORK_SENDING
import com.antsfamily.biketrainer.MainApplication.Companion.ARGS_PROGRAM
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.Program
import com.antsfamily.biketrainer.presentation.programs.ProgramsViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_programs.*
import javax.inject.Inject

@AndroidEntryPoint
class ProgramsFragment : BaseFragment(R.layout.fragment_programs) {
    companion object {
        const val WORK = "it's time to work"
        const val PROFILE_NAME = "profile name"

        fun newInstance(isTime2work: Boolean, profile: String): ProgramsFragment =
            ProgramsFragment().apply {
                arguments = Bundle().apply {
                    isTime2work to WORK
                    profile to PROFILE_NAME
                }
            }
    }

    override val viewModel: ProgramsViewModel by viewModels { withFactory(viewModelFactory) }

    @Inject
    lateinit var programsAdapter: ProgramsAdapter

    private lateinit var programCallback: ItemTouchHelper.Callback

    private var isTime2work: Boolean = false
    private var profileName: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.apply {
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
        programsAdapter = ProgramsAdapter().apply {
            setOnDeleteClickListener { viewModel.onDeleteClick(it) }
            setOnEditClickListener { viewModel.onEditClick(it) }
            setOnItemClickListener { viewModel.setWorkOut(it, profileName) }
        }
        programCallback = ProgramsSwipeCallback(programsAdapter)
        ItemTouchHelper(programCallback).attachToRecyclerView(listPrograms)
        listPrograms.adapter = programsAdapter
    }

    private fun setListeners() {
        toolbarPrograms.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }
        buttonAddProgram.setOnClickListener {
            viewModel.addProgram()
        }
    }

    fun selectProgram() {
    }

    fun showLoading() {
        pbPrograms.isVisible = true
    }

    fun hideLoading() {
        pbPrograms.isVisible = false
    }

    fun hideProgramsList() {
        emptyListPrograms.visibility = View.VISIBLE
        listPrograms.visibility = View.GONE
    }

    fun showProgramsList() {
        emptyListPrograms.visibility = View.GONE
        listPrograms.visibility = View.VISIBLE
    }

    fun setProgramsList(programsList: ArrayList<Program>) {
        programsAdapter.setProgramList(programsList)
        hideLoading()
    }

    fun chooseProgramAndCloseScreen(programName: String) {
        activity?.sendBroadcast(Intent(ACTION_WORK_SENDING).apply {
            this.putExtra(ARGS_PROGRAM, programName)
        })
    }

    fun updateAdapter() {
//        programsAdapter.undoDelete()
    }

    fun showSnackBar(programName: String) {
        Snackbar
            .make(programsListLayout, "Program \"$programName\" was deleted", Snackbar.LENGTH_LONG)
            .setActionTextColor(Color.YELLOW)
            .setAction("UNDO") {
                viewModel.undoDelete()
            }
            .show()
    }
}
