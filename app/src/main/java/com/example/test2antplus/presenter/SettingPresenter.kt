package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.MainApplication
import com.example.test2antplus.data.profiles.Profile
import com.example.test2antplus.data.profiles.ProfilesRepository
import com.example.test2antplus.ui.view.SettingsInterface
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class SettingPresenter(private val view: SettingsInterface) {

    private var profile: Profile

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var profilesRepository: ProfilesRepository

    init {
        MainApplication.graph.inject(this)
        profile = Profile(0, "", 0, "", 0F, 0F)
    }

    fun setName(text: String) {
        profile.setName(text)
    }

    fun setAge(age: Int) {
        profile.setAge(age)
    }

    fun setGender(gender: String) {
        profile.setGender(gender)
    }

    fun setWeight(weight: Float) {
        profile.setWeight(weight)
    }

    fun setHeight(height: Float) {
        profile.setHeight(height)
    }

    @SuppressLint("CheckResult")
    fun createProfile() {
        if (profile.getName().isNotEmpty() && profile.getAge() != 0 && profile.getWeight() != 0.0F) {
            Observable.fromCallable {
                profilesRepository.insertProfile(profile)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    onCancelClick()
                }
        } else {
            view.showToast("Invalid data")
        }
    }

    fun onCancelClick() {
        router.exit()
    }
}