package com.antsfamily.biketrainer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsfamily.biketrainer.navigation.Route

abstract class BaseViewModel : ViewModel() {

    private val _navigationEvent = MutableLiveData<Event<Route>>()
    val navigationEvent: LiveData<Event<Route>>
        get() = _navigationEvent

    private val _navigationBackEvent = MutableLiveData<Event<Unit>>()
    val navigationBackEvent: LiveData<Event<Unit>>
        get() = _navigationBackEvent

    private val _showSuccessSnackBarMessageEvent = MutableLiveData<Event<String>>()
    val showSuccessSnackBarMessageEvent: LiveData<Event<String>>
        get() = _showSuccessSnackBarMessageEvent

    private val _showSuccessSnackBarEvent = MutableLiveData<Event<Int>>()
    val showSuccessSnackBarEvent: LiveData<Event<Int>>
        get() = _showSuccessSnackBarEvent

    private val _showErrorSnackBarMessageEvent = MutableLiveData<Event<String>>()
    val showErrorSnackBarMessageEvent: LiveData<Event<String>>
        get() = _showErrorSnackBarMessageEvent

    private val _showErrorSnackBarEvent = MutableLiveData<Event<Int>>()
    val showErrorSnackBarEvent: LiveData<Event<Int>>
        get() = _showErrorSnackBarEvent

    fun navigateTo(route: Route) {
        _navigationEvent.postValue(Event(route))
    }

    fun navigateBack() {
        _navigationBackEvent.postValue(Event(Unit))
    }

    fun showErrorSnackbar(message: String) {
        _showErrorSnackBarMessageEvent.postValue(Event(message))
    }

    fun showErrorSnackbar(messageId: Int) {
        _showErrorSnackBarEvent.postValue(Event(messageId))
    }

    fun showSuccessSnackbar(message: String) {
        _showSuccessSnackBarMessageEvent.postValue(Event(message))
    }

    fun showSuccessSnackbar(messageId: Int) {
        _showSuccessSnackBarEvent.postValue(Event(messageId))
    }
}
