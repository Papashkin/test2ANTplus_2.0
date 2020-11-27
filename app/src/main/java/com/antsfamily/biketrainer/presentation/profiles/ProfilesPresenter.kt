package com.antsfamily.biketrainer.presentation.profiles

import android.annotation.SuppressLint
import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.repositories.profiles.Profile
import com.antsfamily.biketrainer.data.repositories.profiles.ProfilesRepository
import com.antsfamily.biketrainer.navigation.FragmentScreens
import com.antsfamily.biketrainer.presentation.BasePresenter
import com.antsfamily.biketrainer.presentation.BaseView
import com.antsfamily.biketrainer.util.isFilled
import com.antsfamily.biketrainer.util.isSomethingFilled
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import java.lang.Exception
import javax.inject.Inject

interface ProfilesView : BaseView {
    fun setProfilesList(newProfiles: ArrayList<Pair<String, Int>>)
    fun showProfilesList()
    fun hideProfilesList()
    fun showLoading()
    fun hideLoading()
    fun showSnackBar(profileName: String)
    fun showProfileSettingDialog(profile: Profile)
    fun hideProfileSettingDialog()
    fun updateAdapter()
}

@SuppressLint("CheckResult")
class ProfilesPresenter(private val view: ProfilesView): BasePresenter<ProfilesView>() {

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
        view.hideProfileSettingDialog()
        view.showLoading()
        checkProfileList()
    }

    private fun checkProfileList() = launch {
        try {
            profiles.clear()
            profiles.addAll(profilesRepository.getAllProfiles())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            setData()
        }
    }

    private fun setData() {
        view.hideLoading()
        if (profiles.isEmpty()) {
            view.hideProfilesList()
        } else {
            view.showProfilesList()
        }
        view.setProfilesList(profiles.map { Pair(it.getName(), it.getId()) } as ArrayList)
    }

    fun selectProfile(id: Int) {
        selectedProfile = profiles.first { it.getId() == id }
        router.navigateTo(FragmentScreens.ProgramScreen(true, selectedProfile.getName()))
    }

    fun onBackPressed() {
        router.exit()
    }

    fun onDeleteClick(pos: Int) = launch {
        try {
            deletePosition = pos
            profileToDelete = profiles[pos]
            profilesRepository.removeProfile(profileToDelete!!)
            profiles.remove(profiles[pos])
            if (profiles.isEmpty()) {
                view.hideProfilesList()
            }
            view.showSnackBar(profileToDelete!!.getName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun undoDelete() {
        undoDeleteProfile()
        profiles.add(deletePosition, profileToDelete!!)
    }

    fun onEditProfileClick(id: Int) {
        isNewProfile = false
        existedProfile = profiles.first { it.getId() == id }
        view.showProfileSettingDialog(existedProfile!!)
    }

    fun addNewProfileClick() {
        isNewProfile = true
        view.showProfileSettingDialog(newProfile)
    }

    private fun undoDeleteProfile() = launch {
        try {
            profilesRepository.insertProfile(profileToDelete!!)
            view.updateAdapter()
        } catch (e: Exception) {
            view.showToast(R.string.new_profile_failed_to_create)
            e.printStackTrace()
        }
    }

    private fun checkTheProfiles() = launch {
        try {
            val profileWithSameName = profilesRepository.getProfileByName(newProfile.getName())
            if (profileWithSameName != null) {
                view.showToast(R.string.new_profile_existed)
            } else {
                createProfile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createProfile() = launch {
        try {
            if (newProfile.isFilled()) {
                profilesRepository.insertProfile(newProfile)
                view.hideProfileSettingDialog()
                view.hideKeyboard()
                profiles.add(newProfile)
                setData()
            } else {
                view.showToast(R.string.invalid_data)
            }
        } catch (e: Exception) {
            view.showToast(R.string.new_profile_failed_to_create)
            e.printStackTrace()
        }
    }

    private fun updateProfile() = launch {
        try {
            if (existedProfile!!.isFilled()) {
                profilesRepository.updateProfile(existedProfile!!)
                view.hideProfileSettingDialog()
            } else {
                view.showToast(R.string.invalid_data)
            }
        } catch (e: Exception) {
            view.showToast(R.string.new_profile_failed_to_update)
            e.printStackTrace()
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

    fun onCancelClick() {
        view.hideKeyboard()
        view.hideProfileSettingDialog()
    }

    fun checkProfileFilling(): Boolean = newProfile.isSomethingFilled()

}