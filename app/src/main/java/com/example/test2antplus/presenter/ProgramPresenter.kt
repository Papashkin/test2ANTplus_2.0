package com.example.test2antplus.presenter

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.test2antplus.MainApplication
import com.example.test2antplus.Program
import com.example.test2antplus.data.programs.ProgramsRepository
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.navigation.Screens
import com.example.test2antplus.ui.view.ProgramInterface
import javax.inject.Inject

class ProgramPresenter(private val view: ProgramInterface, owner: LifecycleOwner) {

    @Inject
    lateinit var router: AppRouter
    @Inject
    lateinit var programsRepository: ProgramsRepository

    private var programs: ArrayList<Program> = arrayListOf()

    init {
        MainApplication.graph.inject(this)
        view.showLoading()

        programsRepository
            .getAllPrograms()
            .observe(owner, Observer { list ->
                programs.clear()
                programs.addAll(list)
                setData()
            })
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
        router.navigateTo(Screens.PROGRAM_SETTINGS_FRAGMENT)
    }

//    fun selectProfile(id: Int) {
//        selectedProgram = programs[id]
//        // TODO add selected program to broadcast
//    }
}