package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import android.os.Bundle
import com.example.test2antplus.MainApplication
import com.example.test2antplus.data.profiles.Profile
import com.example.test2antplus.data.profiles.ProfilesRepository
import com.example.test2antplus.navigation.FragmentScreens
import com.example.test2antplus.ui.view.ProfileFragment
import com.example.test2antplus.workInAsinc
import io.reactivex.Single
import ru.terrakok.cicerone.Router
import javax.inject.Inject


class ProfilePresenter(private val view: ProfileFragment) {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var profilesRepository: ProfilesRepository

    private var profiles: ArrayList<Profile> = arrayListOf()

    private lateinit var selectedProfile: Profile

    init {
        MainApplication.graph.inject(this)
        view.showLoading()
        checkProfileList()
    }

    @SuppressLint("CheckResult")
    private fun checkProfileList() {
        profilesRepository.getAllProfiles()
            .compose {
                it.workInAsinc()
            }.subscribe({ list ->
                profiles.clear()
                profiles.addAll(list)
                setData()
            }, {})
    }

    private fun setData() {
        view.hideLoading()
        if (profiles.isEmpty()) {
            view.hideProfilesList()
        } else {
            view.showProfilesList()
        }
        view.setProfilesList(profiles.map {
            Pair(it.getName(), it.getId())
        } as ArrayList)
    }

    fun selectProfile(id: Int) {
        selectedProfile = profiles.first { it.getId() == id }
        router.navigateTo(FragmentScreens.ScanScreen())
    }

    fun onBackPressed() {
        router.exit()
    }

    @SuppressLint("CheckResult")
    fun onDeleteClick(id: Int) {
        val profileToDelete = profiles.first {
            it.getId() == id
        }
        Single.fromCallable {
            profilesRepository.removeProfile(profileToDelete)
        }.compose {
            it.workInAsinc()
        }.subscribe({
            profiles.remove(profiles.first {
                it.getId() == id
            })
            view.deleteSelectedProfile(id)
            if (profiles.isEmpty()) {
                view.hideProfilesList()
            }
            }, {
            it.printStackTrace()
        })
    }

    fun editSelectedProfile(id: Int) {
        view.editProfile(profiles.first { it.getId() ==id })
    }
}