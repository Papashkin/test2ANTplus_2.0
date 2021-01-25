package com.antsfamily.biketrainer.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <X, Y> LiveData<X>.mapDistinct(mapFunction: (input: X) -> Y): LiveData<Y> =
    Transformations.map(this, mapFunction).distinctUntilChanged()

fun <X> LiveData<X>.distinctUntilChanged(): LiveData<X> = Transformations.distinctUntilChanged(this)
