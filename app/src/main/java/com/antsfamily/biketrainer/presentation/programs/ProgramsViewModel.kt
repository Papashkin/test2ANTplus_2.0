package com.antsfamily.biketrainer.presentation.programs

import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.GetProgramsUseCase
import com.antsfamily.biketrainer.navigation.ProgramToCreateProgram
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import javax.inject.Inject

class ProgramsViewModel @Inject constructor(
    private val getProgramsUseCase: GetProgramsUseCase
) : StatefulViewModel<ProgramsViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = true,
        val programs: List<Program> = emptyList(),
        val isProgramsVisible: Boolean = false,
        val isEmptyProgramsVisible: Boolean = false
    )

    init {
        getPrograms()
    }

    fun onAddProgramClick() {
        navigateTo(ProgramToCreateProgram)
    }

    fun onProgramClick(item: Program) {

    }

    fun onLongProgramClick(item: Program) {

    }

    fun onBackClick() {
        navigateBack()
    }

    private fun getPrograms() {
        getProgramsUseCase(Unit, ::handleProgramResult)
    }

    private fun handleProgramResult(result: Result<List<Program>, Error>) {
        hideLoading()
        when (result) {
            is Result.Success -> handleSuccessResult(result.successData)
            is Result.Failure -> showSnackbar(result.errorData.message ?: "Something went wrong :(")
        }
    }

    private fun handleSuccessResult(data: List<Program>) {
        changeState {
            it.copy(
                programs = data,
                isProgramsVisible = data.isNotEmpty(),
                isEmptyProgramsVisible = data.isEmpty()
            )
        }
    }

    private fun showLoading() {
        changeState { it.copy(isLoading = true) }
    }

    private fun hideLoading() {
        changeState { it.copy(isLoading = false) }
    }
}
