package com.example.test2antplus.presenter

import com.example.test2antplus.MainApplication
import com.example.test2antplus.Profile
import com.example.test2antplus.data.ProfilesDatabase
import com.example.test2antplus.data.ProfilesRepository
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.navigation.Screens
import com.example.test2antplus.ui.view.ProfileFragment
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfilePresenter(private val view: ProfileFragment) {
    @Inject
    lateinit var router: AppRouter
    @Inject
    lateinit var database: ProfilesDatabase
    @Inject
    lateinit var profilesRepository: ProfilesRepository

    private var profiles: ArrayList<Profile> = arrayListOf()

    private lateinit var selectedProfile: Profile

    init {
        MainApplication.graph.inject(this)
        view.showLoading()
        profilesRepository
            .getAllProfiles()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { list ->
                profiles.addAll(list)
                setData()
            }
    }

    private fun setData() {
        view.hideLoading()
        if (profiles.isEmpty()) {
            view.hideProfilesList()
            view.showEmptyProfilesList()
        } else {
            view.hideEmptyProfilesList()
            view.showProfilesList()
        }
        view.setProfilesList(profiles.map { it -> it.getName() })
    }

    fun selectProfile(id: Int) {
        selectedProfile = profiles[id]
        router.navigateTo(Screens.SCAN_FRAGMENT) //"scan screen")
    }

    fun onCreateProfileClick() {
        router.navigateTo(Screens.SETTING_FRAGMENT) //"settings screen")
    }

}