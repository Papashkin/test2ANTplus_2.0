package com.example.test2antplus.presentation.view.start

import com.example.test2antplus.presentation.presenter.BaseView

interface StartView : BaseView {
    fun requestPermissions(permissions: Array<String>)
}