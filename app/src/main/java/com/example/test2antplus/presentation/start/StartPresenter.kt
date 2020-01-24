package com.example.test2antplus.presentation.start

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.test2antplus.MainApplication
import com.example.test2antplus.navigation.FragmentScreens
import com.example.test2antplus.presentation.BaseView
import ru.terrakok.cicerone.Router
import javax.inject.Inject


interface StartView : BaseView {
    fun requestPermissions(permissions: Array<String>)
}

class StartPresenter(private val view: StartView) {
    @Inject
    lateinit var router: Router
    @Inject
    lateinit var context: Context

    private val permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    init {
        MainApplication.graph.inject(this)
        requestPermission()
    }

    fun onProfileClick() {
        if (isPermissionGranted()) {
            router.navigateTo(FragmentScreens.ProfileScreen())
        } else {
            requestPermission()
        }
    }

    fun onProgramClick() {
        if (isPermissionGranted()) {
            router.navigateTo(
                FragmentScreens.ProgramScreen(
                    isTime2work = false,
                    profile = ""
                )
            )
        } else {
            requestPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.forEach { permission ->
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        } else {
            return true
        }
    }

    private fun requestPermission() {
        view.requestPermissions(permissions)
    }
}