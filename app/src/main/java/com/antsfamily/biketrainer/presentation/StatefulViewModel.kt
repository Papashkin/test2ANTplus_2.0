package com.antsfamily.biketrainer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

open class StatefulViewModel<State>(private val initialState: State) : BaseViewModel() {

    private val _state = MutableLiveData(initialState)
    val state: LiveData<State>
        get() = _state

    protected fun changeState(changeState: (currentState: State) -> State) {
        _state.value = changeState(_state.value ?: initialState)
    }

    protected fun postChangeState(changeState: (currentState: State) -> State) {
        _state.postValue(changeState(_state.value ?: initialState))
    }
}
