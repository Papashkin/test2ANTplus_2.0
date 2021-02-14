package com.antsfamily.biketrainer.presentation.createprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.data.models.Profile
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.CreateProfileUseCase
import com.antsfamily.biketrainer.navigation.CreateProfileToStart
import com.antsfamily.biketrainer.presentation.Event
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.antsfamily.biketrainer.util.orZero
import com.garmin.fit.Gender
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.random.Random

class CreateProfileViewModel @Inject constructor(
    private val createProfileUseCase: CreateProfileUseCase
) : StatefulViewModel<CreateProfileViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = false,
        val genders: List<Gender> = Gender.values().toList(),
        val usernameError: String? = null,
        val ageError: String? = null,
        val weightError: String? = null,
        val heightError: String? = null,
        val genderError: String? = null
    )

    private val _clearFieldsEvent = MutableLiveData<Event<Unit>>()
    val clearFieldsEvent: LiveData<Event<Unit>>
        get() = _clearFieldsEvent

    private var genderIndex: Int? = null

    fun onUsernameChange() {
        changeState { it.copy(usernameError = null) }
    }

    fun onAgeChange() {
        changeState { it.copy(ageError = null) }
    }

    fun onWeightChange() {
        changeState { it.copy(weightError = null) }
    }

    fun onHeightChange() {
        changeState { it.copy(heightError = null) }
    }

    fun onGenderChange() {
        changeState { it.copy(genderError = null) }
    }

    fun onGenderSelected(index: Int) {
        genderIndex = index
    }

    fun onGenderCleared() {
        genderIndex = null
    }

    fun onCreateClick(
        username: String?,
        age: Int,
        weight: BigDecimal?,
        height: BigDecimal?
    ) {
        if (isValid(username, age, weight, height)) {
            showLoading()
            createProfile(
                username!!,
                age,
                weight.orZero(),
                height.orZero(),
                getGenderById(genderIndex!!)
            )
        }
    }

    private fun isValid(
        username: String?, age: Int, weight: BigDecimal?, height: BigDecimal?
    ): Boolean {
        val isUsernameValid = !username.isNullOrBlank()
        val isAgeValid = age in 1..109
        val isWeightValid = weight.orZero() > BigDecimal.ZERO
        val isHeightValid = height.orZero() > BigDecimal.ZERO
        val isGenderValid = genderIndex != null
        changeState {
            it.copy(
                usernameError = if (isUsernameValid) null else "This is required",
                ageError = if (isAgeValid) null else "This is required",
                weightError = if (isWeightValid) null else "This is required",
                heightError = if (isHeightValid) null else "This is required",
                genderError = if (isGenderValid) null else "This is required",
            )
        }
        return isUsernameValid && isAgeValid && isWeightValid && isHeightValid && isGenderValid
    }

    private fun createProfile(
        username: String,
        age: Int,
        weight: BigDecimal,
        height: BigDecimal,
        gender: String?
    ) {
        createProfileUseCase(
            Profile(
                getRandomId(),
                username,
                age,
                gender.orEmpty(),
                weight.toFloat(),
                height.toFloat(),
                true
            )
        ) { handleResult(it) }
    }

    private fun handleResult(result: Result<Unit, Error>) {
        hideLoading()
        when (result) {
            is Result.Success -> {
                _clearFieldsEvent.postValue(Event(Unit))
                navigateTo(CreateProfileToStart)
            }
            is Result.Failure -> {
                showSnackbar(result.errorData.message ?: "Something went wrong :(")
            }
        }
    }

    private fun showLoading() {
        changeState { it.copy(isLoading = true) }
    }

    private fun hideLoading() {
        changeState { it.copy(isLoading = false) }
    }

    private fun getRandomId(): Int = Random.nextInt(0, 1000000)

    private fun getGenderById(index: Int?) = index?.let { Gender.values()[it].toString() }
}
