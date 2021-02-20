package com.antsfamily.biketrainer.presentation.home

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.domain.usecase.SubscribeToProfileWithProgramsUseCase
import com.antsfamily.biketrainer.navigation.HomeToCreateProgram
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    subscribeToProfileWithProgramsUseCase: SubscribeToProfileWithProgramsUseCase,
) : StatefulViewModel<HomeViewModel.State>(State()) {

    data class State(val dateTime: String? = null)

    val profileWithProgramsFlow = subscribeToProfileWithProgramsUseCase(Unit)
        .asLiveData(viewModelScope.coroutineContext)

    init {
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

    companion object {
        private const val DATE_FORMAT_FULL = "EEEE, MMMM dd, yyyy"
    }
}
