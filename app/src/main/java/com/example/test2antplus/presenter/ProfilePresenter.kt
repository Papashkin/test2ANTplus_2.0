package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.*
import com.example.test2antplus.data.profiles.Profile
import com.example.test2antplus.data.profiles.ProfilesRepository
import com.example.test2antplus.navigation.FragmentScreens
import com.example.test2antplus.ui.view.ProfileFragment
import io.reactivex.Observable
import io.reactivex.Single
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@SuppressLint("CheckResult")
class ProfilePresenter(private val view: ProfileFragment) {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var profilesRepository: ProfilesRepository

    private var profiles: ArrayList<Profile> = arrayListOf()
    private var newProfile: Profile = Profile(0, "", 0, "", 0.0f, 0.0f)
    private var existedProfile: Profile? = null
    private var profileToDelete: Profile? = null
    private var deletePosition = -1
    private var isNewProfile = true

    private lateinit var selectedProfile: Profile

    init {
        MainApplication.graph.inject(this)
        view.hideProfileBottomDialog()
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
        router.navigateTo(FragmentScreens.ProgramScreen(true, selectedProfile.getName()))
    }

    fun onBackPressed() {
        router.exit()
    }

    @SuppressLint("CheckResult")
    fun onDeleteClick(pos: Int) {
        deletePosition = pos
        profileToDelete = profiles[pos]
        Single.fromCallable {
            profilesRepository.removeProfile(profileToDelete!!)
        }.compose {
            it.workInAsinc()
        }.subscribe({
            profiles.remove(profiles[pos])
            if (profiles.isEmpty()) {
                view.hideProfilesList()
            }
            view.showSnackBar(profileToDelete!!.getName())
        }, {
            it.printStackTrace()
        })
    }

    fun undoDelete() {
        undoDeleteProfile()
        profiles.add(deletePosition, profileToDelete!!)

    }

    fun onEditProfileClick(id: Int) {
        isNewProfile = false
        existedProfile = profiles.first { it.getId() == id }
        view.showProfileBottomDialog(existedProfile!!)
    }

    fun addNewProfileClick() {
        isNewProfile = true
        view.showProfileBottomDialog(newProfile)
    }

    private fun undoDeleteProfile() {
        Observable.fromCallable {
            profilesRepository.insertProfile(profileToDelete!!)
        }.compose {
            it.workInAsinc()
        }.subscribe({
            view.updateAdapter()
        }, {
            view.showToast(R.string.new_profile_failed_to_create)
            it.printStackTrace()
        })
    }

    private fun checkTheProfiles() {
        Observable.fromCallable {
            profilesRepository.getProfileByName(newProfile.getName())
        }.compose {
            it.workInAsinc()
        }.subscribe({
            view.showToast(R.string.new_profile_existed)
        }, {
            createProfile()
        })
    }

    private fun createProfile() {
        if (newProfile.isFilled()) {
            Observable.fromCallable {
                profilesRepository.insertProfile(newProfile)
            }.compose {
                it.workInAsinc()
            }.subscribe({
                view.hideProfileBottomDialog()
                view.hideKeyboard()
            }, {
                view.showToast(R.string.new_profile_failed_to_create)
                it.printStackTrace()
            })
        } else {
            view.showToast(R.string.invalid_data)
        }
    }

    private fun updateProfile() {
        if (existedProfile!!.isFilled()) {
            Observable.fromCallable {
                profilesRepository.updateProfile(existedProfile!!)
            }.compose {
                it.workInAsinc()
            }.subscribe({
                view.hideProfileBottomDialog()
            }, {
                view.showToast(R.string.new_profile_failed_to_update)
                it.printStackTrace()
            })
        } else {
            view.showToast(R.string.invalid_data)
        }
    }

    fun setName(name: String) {
        if (isNewProfile) {
            newProfile.setName(name)
        } else {
            existedProfile?.setName(name)
        }
    }

    fun setAge(age: Int) {
        if (isNewProfile) {
            newProfile.setAge(age)
        } else {
            existedProfile?.setAge(age)
        }
    }

    fun setGender(gender: String) {
        if (isNewProfile) {
            newProfile.setGender(gender)
        } else {
            existedProfile?.setGender(gender)
        }
    }

    fun setWeight(weight: Float) {
        if (isNewProfile) {
            newProfile.setWeight(weight)
        } else {
            existedProfile?.setWeight(weight)
        }
    }

    fun setHeight(height: Float) {
        if (isNewProfile) {
            newProfile.setHeight(height)
        } else {
            existedProfile?.setHeight(height)
        }
    }

    fun onCreateBtnClick() {
        if (isNewProfile) {
            checkTheProfiles()
        } else {
            updateProfile()
        }
    }

    fun checkProfileFilling(): Boolean = newProfile.isSomethingFilled()

}