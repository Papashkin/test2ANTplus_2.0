package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.MainApplication
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.data.programs.ProgramsRepository
import com.example.test2antplus.navigation.FragmentScreens
import com.example.test2antplus.ui.view.ProgramInterface
import com.example.test2antplus.workInAsinc
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class ProgramPresenter(private val view: ProgramInterface) {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var programsRepository: ProgramsRepository

    private var programs: ArrayList<Program> = arrayListOf()

    init {
        MainApplication.graph.inject(this)
        updateProgramsList()
    }

    private fun setData() {
        if (programs.isEmpty()) {
            view.hideProgramsList()
            view.showEmptyProgramsList()
        } else {
            view.hideEmptyProgramsList()
            view.showProgramsList()
        }
        view.setProgramsList(programs)
    }

    fun addProgram() {
        router.navigateTo(FragmentScreens.ProgramSettingsScreen())
    }

    @SuppressLint("CheckResult")
    fun updateProgramsList() {
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

//    fun selectProfile(id: Int) {
//        selectedProgram = programs[id]
//        // TODO add selected program to broadcast
//    }
}