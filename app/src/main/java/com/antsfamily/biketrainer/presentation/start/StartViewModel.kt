package com.antsfamily.biketrainer.presentation.start

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.util.navigation.FragmentScreens
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class StartViewModel @Inject constructor(
    private val router: Router,
    private val context: Context
) : StatefulViewModel<StartViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = false
    )

    fun onStartClick() {
        if (isPermissionGranted()) {
            router.navigateTo(FragmentScreens.ProfileScreen(true))
        } else {
            requestPermission()
        }
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
            router.navigateTo(FragmentScreens.ProgramScreen(profile = ""))
        } else {
            requestPermission()
        }
    }

    private val permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isPermissionGranted(): Boolean {
        permissions.forEach { permission ->
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    var permissionsForGrant: MutableLiveData<Array<String>> = MutableLiveData(arrayOf())
    private fun requestPermission() {
        permissionsForGrant.postValue(permissions)
    }

    fun clear() {
        permissionsForGrant.postValue(arrayOf())
        clearLiveDataValues()
    }
}
