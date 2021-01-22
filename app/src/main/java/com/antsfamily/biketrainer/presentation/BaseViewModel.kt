package com.antsfamily.biketrainer.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val keyboard: MutableLiveData<Boolean> = MutableLiveData(false)
    val toast: MutableLiveData<Any?> = MutableLiveData(null)

    fun showLoading() {
        loading.postValue(true)
    }

    fun hideLoading() {
        loading.postValue(false)
    }

    fun showKeyboard() {
        keyboard.postValue(true)
    }

    fun hideKeyboard() {
        keyboard.postValue(false)
    }

    fun showToast(@StringRes id: Int) {
        toast.postValue(id)
    }

    fun showToast(message: String) {
        toast.postValue(message)
    }


    fun clearLiveDataValues() {
        loading.postValue(false)
        keyboard.postValue(false)
        toast.postValue(null)
    }

}
