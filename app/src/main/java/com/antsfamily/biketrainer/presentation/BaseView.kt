package com.antsfamily.biketrainer.presentation

interface BaseView {
    fun showToast(text: String)
    fun showToast(id: Int)
    fun showKeyboard()
    fun hideKeyboard()
}