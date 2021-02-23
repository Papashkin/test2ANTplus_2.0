package com.antsfamily.biketrainer.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class BaseUseCase<in Params, out Type> where Type : Any {

    private val mainDispatcher = Dispatchers.Main
    private val backgroundDispatcher = Dispatchers.IO

    abstract suspend fun run(params: Params): Type

    operator fun invoke(params: Params, onResult: (Type) -> Unit = {}) {
        val job = CoroutineScope(backgroundDispatcher).async { run(params) }
        CoroutineScope(mainDispatcher).launch { onResult(job.await()) }
    }
}
