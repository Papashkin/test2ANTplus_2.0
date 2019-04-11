package com.example.test2antplus.presentation.view.programs

import com.example.test2antplus.data.repositories.programs.Program
import com.example.test2antplus.presentation.presenter.BaseView

interface ProgramsView : BaseView {
    fun selectProgram()
    fun showLoading()
    fun hideLoading()
    fun setProgramsList(programsList: ArrayList<Program>)
    fun showProgramsList()
    fun hideProgramsList()
    fun chooseProgramAndCloseScreen(programName: String)
    fun updateAdapter()
    fun showSnackBar(programName: String)
}