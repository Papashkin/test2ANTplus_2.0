package com.example.test2antplus.presenter

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.test2antplus.MainApplication
import com.example.test2antplus.data.profiles.Profile
import com.example.test2antplus.data.profiles.ProfilesRepository
import com.example.test2antplus.navigation.FragmentScreens
import com.example.test2antplus.ui.view.ProfileFragment
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class ProfilePresenter(private val view: ProfileFragment, owner: LifecycleOwner) {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var profilesRepository: ProfilesRepository

    private var profiles: ArrayList<Profile> = arrayListOf()

    private lateinit var selectedProfile: Profile

    init {
        MainApplication.graph.inject(this)
        view.showLoading()

        profilesRepository
            .getAllProfiles()
            .observe(owner, Observer { list ->
                profiles.clear()
                profiles.addAll(list)
                setData()
            })
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
        view.setProfilesList(profiles.map {
            it.getName()
        })
    }

    fun selectProfile(id: Int) {
        selectedProfile = profiles[id]
        router.navigateTo(FragmentScreens.ScanScreen())
    }

    fun onCreateProfileClick() {
        router.navigateTo(FragmentScreens.SettingScreen())
    }
}