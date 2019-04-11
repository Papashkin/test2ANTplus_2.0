package com.example.test2antplus.presentation.presenter

interface BaseView {
    fun showToast(text: String)
    fun showToast(id: Int)
    fun showKeyboard()
    fun hideKeyboard()
}