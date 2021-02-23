package com.antsfamily.biketrainer.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsfamily.biketrainer.navigation.Route

abstract class BaseViewModel : ViewModel() {

    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val keyboard: MutableLiveData<Boolean> = MutableLiveData(false)
    val toast: MutableLiveData<Any?> = MutableLiveData(null)

    private val _showToastEvent = MutableLiveData<Event<String>>(null)
    val showToastEvent: LiveData<Event<String>>
        get() = _showToastEvent

    private val _navigationEvent = MutableLiveData<Event<Route>>()
    val navigationEvent: LiveData<Event<Route>>
        get() = _navigationEvent

    private val _navigationBackEvent = MutableLiveData<Event<Unit>>()
    val navigationBackEvent: LiveData<Event<Unit>>
        get() = _navigationBackEvent

    private val _showSnackBarEvent = MutableLiveData<Event<String>>()
    val showSnackBarEvent: LiveData<Event<String>>
        get() = _showSnackBarEvent

    fun navigateTo(route: Route) {
        _navigationEvent.postValue(Event(route))
    }

    fun navigateBack() {
        _navigationBackEvent.postValue(Event(Unit))
    }

    fun showSnackbar(message: String) {
        _showSnackBarEvent.postValue(Event(message))
    }

    fun showKeyboard() {
        keyboard.postValue(true)
    }

    fun hideKeyboard() {
        keyboard.postValue(false)
    }

    fun showToast(@StringRes id: Int) {
    }

    fun showToast(message: String) {
        _showToastEvent.postValue(Event(message))
    }
}
