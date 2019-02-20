package com.example.test2antplus.ui.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.presenter.ProfilePresenter
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.fragment_profiles.*
import javax.inject.Inject

interface ProfileInterface {
    fun setProfilesList(newProfiles: List<String>)
    fun onProfileClick()
    fun showToast(text: String)
    fun onCreateProfile()
    fun showProfilesList()
    fun hideProfilesList()
    fun showEmptyProfilesList()
    fun hideEmptyProfilesList()
    fun showLoading()
    fun hideLoading()
}

class ProfileFragment: Fragment(), ProfileInterface {

    @Inject lateinit var appContext: Context

    private lateinit var presenter: ProfilePresenter
    private lateinit var profilesAdapter: ArrayAdapter<String>

    private var profiles: ArrayList<String> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_profiles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = ProfilePresenter(this)

        profilesAdapter = ArrayAdapter(appContext, R.layout.layout_profile_info, profiles)
        listProfiles.adapter = profilesAdapter

        fabCreate.setOnClickListener {
            presenter.onCreateProfileClick()
        }

        listProfiles.setOnItemClickListener { _, _, position, _ ->
            presenter.selectProfile(position)
        }
    }

    override fun onProfileClick() {

    }

    override fun showToast(text: String) {
        toast(text)
    }

    override fun onCreateProfile() {

    }

    override fun setProfilesList(newProfiles: List<String>) {
        profiles.clear()
        profiles.addAll(newProfiles)
        profilesAdapter.notifyDataSetChanged()

        listProfiles.visibility = View.VISIBLE
        pbProfiles.visibility = View.GONE
    }

    override fun showProfilesList() {
        listProfiles.visibility = View.VISIBLE
    }

    override fun hideProfilesList() {
        listProfiles.visibility = View.GONE
    }

    override fun showLoading() {
        pbProfiles.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        pbProfiles.visibility = View.GONE
    }

    override fun showEmptyProfilesList() {
        emptyListProfiles.visibility = View.VISIBLE
    }

    override fun hideEmptyProfilesList() {
        emptyListProfiles.visibility = View.GONE
    }
}