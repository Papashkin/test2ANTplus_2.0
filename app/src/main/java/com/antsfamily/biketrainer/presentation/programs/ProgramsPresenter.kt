package com.antsfamily.biketrainer.presentation.programs

import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.data.repositories.programs.Program
import com.antsfamily.biketrainer.data.repositories.programs.ProgramsRepository
import com.antsfamily.biketrainer.navigation.FragmentScreens
import com.antsfamily.biketrainer.presentation.BasePresenter
import com.antsfamily.biketrainer.presentation.BaseView
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import java.lang.Exception
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

class ProgramsPresenter(private val view: ProgramsView) : BasePresenter<ProgramsView>() {

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

    private fun updateProgramsList() = launch {
        try {
            programs.clear()
            programs.addAll(programsRepository.getAllPrograms())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            setData()
        }
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

    fun onDeleteClick(position: Int) = launch {
        try {
            deletePosition = position
            programToDelete = programs[position]
            programs.remove(programToDelete!!)
            programsRepository.removeProgram(programToDelete!!)
            view.showSnackBar(programToDelete!!.getName())
            if (programs.isEmpty()) {
                view.hideProgramsList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun undoDelete() {
        undoDeleteProgram()
        programs.add(deletePosition, programToDelete!!)
    }

    private fun undoDeleteProgram() = launch {
        try {
            programsRepository.insertProgram(programToDelete!!)
            view.updateAdapter()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onEditClick(id: Int) {
        router.navigateTo(FragmentScreens.ProgramSettingsScreen(programs.first { it.getId() == id }))
    }

    fun setWorkOut(id: Int, profileName: String) {
        val selectedProgram = programs.first { it.getId() == id }
        router.navigateTo(FragmentScreens.ScanScreen(profileName, selectedProgram))
    }
}