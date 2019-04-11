package com.example.test2antplus.presentation.view.profiles

import com.example.test2antplus.data.repositories.profiles.Profile
import com.example.test2antplus.presentation.presenter.BaseView

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