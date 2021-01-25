package com.antsfamily.biketrainer.domain

sealed class Result<out T, out R> {
    class Success<out T>(val successData: T) : Result<T, Nothing>()
    class Failure<out R : Error>(val errorData: R) : Result<Nothing, R>()

    fun handleResult(successBlock: (T) -> Unit = {}, failureBlock: (R) -> Unit = {}) {
        when (this) {
            is Success -> successBlock(successData)
            is Failure -> failureBlock(errorData)
        }
    }
}
