package com.antsfamily.biketrainer.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class BaseUseCase<in Params, out Type> where Type : Any {

    abstract suspend fun run(params: Params): Result<Type, Error>

    operator fun invoke(params: Params, onResult: (Result<Type, Error>) -> Unit = {}) {
        val job = GlobalScope.async(Dispatchers.IO) { run(params) }
        GlobalScope.launch(Dispatchers.Main) { onResult(job.await()) }
    }
}
