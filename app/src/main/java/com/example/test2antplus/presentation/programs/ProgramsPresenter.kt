package com.example.test2antplus.presentation.programs

import android.annotation.SuppressLint
import com.example.test2antplus.MainApplication
import com.example.test2antplus.data.repositories.programs.Program
import com.example.test2antplus.data.repositories.programs.ProgramsRepository
import com.example.test2antplus.navigation.FragmentScreens
import com.example.test2antplus.presentation.BaseView
import com.example.test2antplus.util.workInAsinc
import io.reactivex.Single
import ru.terrakok.cicerone.Router
import javax.inject.Inject


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

@SuppressLint("CheckResult")
class ProgramsPresenter(private val view: ProgramsView) {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var programsRepository: ProgramsRepository

    private var programs: ArrayList<Program> = arrayListOf()
    private var programToDelete: Program? = null
    private var deletePosition = -1

    init {
        MainApplication.graph.inject(this)
        view.showLoading()
        updateProgramsList()
    }

    fun addProgram() {
        router.navigateTo(FragmentScreens.ProgramSettingsScreen(null))
    }

    private fun updateProgramsList() {
        programs.clear()
        programsRepository.getAllPrograms()
            .compose {
                it.workInAsinc()
            }.subscribe({ list ->
                programs.addAll(list)
                setData()
            }, { error ->
                error.printStackTrace()
                setData()
            })
    }

    private fun setData() {
        if (programs.isEmpty()) {
            view.hideProgramsList()
        } else {
            view.showProgramsList()
        }
        view.setProgramsList(programs)
    }

    fun onBackPressed() {
        router.exit()
    }

    fun onDeleteClick(position: Int) {
        deletePosition = position
        programToDelete = programs[position]
        programs.remove(programToDelete!!)
        Single.fromCallable {
            programsRepository.removeProgram(programToDelete!!)
        }.compose {
            it.workInAsinc()
        }.subscribe({
            view.showSnackBar(programToDelete!!.getName())
            if (programs.isEmpty()) {
                view.hideProgramsList()
            }
        },{
            it.printStackTrace()
        })
    }

    fun undoDelete() {
        undoDeleteProgram()
        programs.add(deletePosition, programToDelete!!)
    }

    private fun undoDeleteProgram() {
        Single.fromCallable {
            programsRepository.insertProgram(programToDelete!!)
        }.compose {
            it.workInAsinc()
        }.subscribe({
            view.updateAdapter()
        },{
            it.printStackTrace()
        })
    }

    fun onEditClick(id: Int) {
        router.navigateTo(FragmentScreens.ProgramSettingsScreen(programs.first { it.getId() == id }))
    }

    fun setWorkOut(id: Int, profileName: String) {
        val selectedProgram = programs.first { it.getId() == id }
        router.navigateTo(FragmentScreens.ScanScreen(profileName, selectedProgram))
    }
}