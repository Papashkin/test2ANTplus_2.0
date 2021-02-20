package com.antsfamily.biketrainer.presentation.home

import com.antsfamily.biketrainer.data.models.profile.Profile
import com.antsfamily.biketrainer.data.models.profile.ProfileWithPrograms
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.GetProfileAndProgramsUseCase
import com.antsfamily.biketrainer.navigation.HomeToCreateProgram
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getProfileAndProgramsUseCase: GetProfileAndProgramsUseCase
) : StatefulViewModel<HomeViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = true,
        val profile: Profile? = null,
        val programs: List<Program> = emptyList(),
        val dateTime: String? = null,
        val isProgramsVisible: Boolean = false,
        val isEmptyProgramsVisible: Boolean = false
    )

    init {
        getProfileWithPrograms()
        getDateTime()
    }

    fun onSettingsClick() {
        // TODO: later
    }

    fun onProgramClick(item: Program) {
        // TODO: later
    }

    fun onCreateProgramClick() {
        navigateTo(HomeToCreateProgram)
    }

    private fun getDateTime() {
        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_FULL))
        changeState { it.copy(dateTime = date) }
    }

    private fun getProfileWithPrograms() {
        getProfileAndProgramsUseCase(Unit, ::handleProfileAndProgramsResult)
    }

    private fun handleProfileAndProgramsResult(result: Result<ProfileWithPrograms, Error>) {
        when (result) {
            is Result.Success -> handleProfileAndProgramsSuccessResult(result.successData)
            is Result.Failure -> handleProfileAndProgramsFailureResult(result.errorData)
        }
    }

    private fun handleProfileAndProgramsSuccessResult(data: ProfileWithPrograms) {
        changeState {
            it.copy(
                isLoading = false,
                profile = data.profile,
                programs = data.programs,
                isProgramsVisible = data.programs.isNotEmpty(),
                isEmptyProgramsVisible = data.programs.isEmpty()
            )
        }
    }

    private fun handleProfileAndProgramsFailureResult(error: Error) {
        // TODO: 15.02.2021
    }

    companion object {
        private const val DATE_FORMAT_FULL = "EEEE, MMMM dd, yyyy"
    }
}
