package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.MainApplication
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.data.programs.ProgramsRepository
import com.example.test2antplus.navigation.FragmentScreens
import com.example.test2antplus.ui.view.ProgramInterface
import com.example.test2antplus.workInAsinc
import io.reactivex.Single
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@SuppressLint("CheckResult")
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

    fun addProgram() {
        router.navigateTo(FragmentScreens.ProgramSettingsScreen())
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

    fun onDeleteClick(id: Int) {
        val deletedProgram = programs.first { it.getId() == id }
        programs.remove(deletedProgram)
        Single.fromCallable {
            programsRepository.removeProgram(deletedProgram)
        }.compose {
            it.workInAsinc()
        }.subscribe({
            view.updateProgramsList(id)
            if (programs.isEmpty()) {
                view.hideProgramsList()
            }
        },{
            it.printStackTrace()
        })
    }

    fun onEditClick(id: Int) {

    }

//    fun selectProfile(id: Int) {
//        selectedProgram = programs[id]
//        // TODO add selected program to broadcast
//    }
}