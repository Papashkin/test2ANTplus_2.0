package com.example.test2antplus.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

abstract class BasePresenter<TView: BaseView> : CoroutineScope {

    private val job = SupervisorJob()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    override val coroutineContext: CoroutineContext = job + Dispatchers.Main + errorHandler

    open fun onError(e: Throwable) {
        e.printStackTrace()
    }
}