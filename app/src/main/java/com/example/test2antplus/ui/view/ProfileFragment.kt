package com.example.test2antplus.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.presenter.ProfilePresenter
import com.example.test2antplus.ui.adapter.ProfileAdapter
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.fragment_profiles.*

interface ProfileInterface {
    fun setProfilesList(newProfiles: ArrayList<Pair<String, Int>>)
    fun showToast(text: String)
    fun showProfilesList()
    fun hideProfilesList()
    fun showLoading()
    fun hideLoading()

    fun deleteSelectedProfile(id: Int)
}

class ProfileFragment : Fragment(), ProfileInterface {

    private lateinit var presenter: ProfilePresenter
    private lateinit var profilesAdapter: ProfileAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_profiles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = ProfilePresenter(this)

        toolbarProfiles.setNavigationIcon(R.drawable.ic_arrow_back_32)
        toolbarProfiles.setNavigationOnClickListener {
            presenter.onBackPressed()
        }

        activity?.let {
            profilesAdapter = ProfileAdapter(
                onDeleteClick = { id ->
                    AlertDialog.Builder(context!!)
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            presenter.onDeleteClick(id)
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                },
                onEditClick = { id ->
                    presenter.editSelectedProfile(id)
                }, onItemClick = { id ->
                    presenter.selectProfile(id)
                })
            listProfiles.adapter = profilesAdapter
        }

        buttonAddProfile.setOnClickListener {
            val profileDialog = NewProfileDialog()
            profileDialog.show(fragmentManager, profileDialog.tag)
        }
    }

    override fun showToast(text: String) {
        toast(text)
    }

    override fun setProfilesList(newProfiles: ArrayList<Pair<String, Int>>) {
        profilesAdapter.setProfileList(newProfiles)
        pbProfiles.visibility = View.GONE
        if (newProfiles.isEmpty()) {
            hideProfilesList()
        } else {
            showProfilesList()
        }
    }

    override fun showProfilesList() {
        emptyListProfiles.visibility = View.GONE
        listProfiles.visibility = View.VISIBLE
    }

    override fun hideProfilesList() {
        listProfiles.visibility = View.GONE
        emptyListProfiles.visibility = View.VISIBLE
    }

    override fun showLoading() {
        pbProfiles.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        pbProfiles.visibility = View.GONE
    }

    override fun deleteSelectedProfile(id: Int) {
        profilesAdapter.removeItem(id)
    }
}