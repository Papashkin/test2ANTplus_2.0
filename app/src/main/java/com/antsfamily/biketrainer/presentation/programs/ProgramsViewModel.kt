package com.antsfamily.biketrainer.presentation.programs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import com.antsfamily.biketrainer.data.models.Program
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProgramsViewModel @Inject constructor(
    private val programsRepository: ProgramsRepository
) : StatefulViewModel<ProgramsViewModel.State>(State()) {

    data class State(
        var programToDelete: Program? = null,
        var deletePosition: Int = -1
    )

    val programs: LiveData<List<Program>> = liveData {
        emitSource(programsRepository.getAllPrograms())
    }
    val deletingSnackBar: MutableLiveData<String?> = MutableLiveData(null)

    fun addProgram() {
//        router.navigateTo(FragmentScreens.ProgramSettingsScreen(null))
        clearLiveDataValues()
    }

    fun onBackPressed() {
//        router.exit()
    }

    fun clearValues() {
        deletingSnackBar.postValue(null)
        clearLiveDataValues()
    }

    fun onDeleteClick(position: Int) = launch {
        try {
//            deletePosition = position
//            programToDelete = programs.value?.get(position)
//            programsRepository.removeProgram(programToDelete!!)
//            deletingSnackBar.postValue(programToDelete?.getName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun undoDelete() = launch {
//        try {
//            programsRepository.insertProgram(programToDelete!!)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    fun onEditClick(id: Int) {
//        router.navigateTo(FragmentScreens.ProgramSettingsScreen(programs.value?.first { it.getId() == id }))
    }

    fun setWorkOut(id: Int, profileName: String) {
        programs.value?.firstOrNull { it.getId() == id }?.let { selectedProgram ->
//            router.navigateTo(FragmentScreens.ScanScreen(profileName, selectedProgram))
        }
    }
}
