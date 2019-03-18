package com.example.test2antplus.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.presenter.ProfilePresenter
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.fragment_profiles.*

interface ProfileInterface {
    fun setProfilesList(newProfiles: List<String>)
    fun showToast(text: String)
    fun showProfilesList()
    fun hideProfilesList()
    fun showEmptyProfilesList()
    fun hideEmptyProfilesList()
    fun showLoading()
    fun hideLoading()
}

class ProfileFragment : Fragment(), ProfileInterface {

    private lateinit var presenter: ProfilePresenter
    private lateinit var profilesAdapter: ArrayAdapter<String>
    private lateinit var owner: LifecycleOwner

    private var profiles: ArrayList<String> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_profiles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        owner = LifecycleOwner { lifecycle }
        presenter = ProfilePresenter(this, owner)

        toolbarProfiles.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbarProfiles.setNavigationOnClickListener {
            presenter.onBackPressed()
        }

        activity?.let {
            profilesAdapter = ArrayAdapter(it.applicationContext, R.layout.card_profile_info, profiles)
            listProfiles.adapter = profilesAdapter
        }

        fabCreate.setOnClickListener {
            presenter.onCreateProfileClick()
        }

        listProfiles.setOnItemClickListener { _, _, position, _ ->
            presenter.selectProfile(position)
        }
    }

    override fun showToast(text: String) {
        toast(text)
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