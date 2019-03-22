package com.example.test2antplus.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.data.profiles.Profile
import com.example.test2antplus.presenter.ProfilePresenter
import com.example.test2antplus.ui.adapter.ProfileAdapter
import com.pawegio.kandroid.textWatcher
import kotlinx.android.synthetic.main.dialog_new_profile.*
import kotlinx.android.synthetic.main.fragment_profiles.*

interface ProfileInterface {
    fun setProfilesList(newProfiles: ArrayList<Pair<String, Int>>)
    fun showProfilesList()
    fun hideProfilesList()
    fun showLoading()
    fun hideLoading()
    fun deleteSelectedProfile(id: Int)
    fun showProfileBottomDialog(profile: Profile)
    fun hideProfileBottomDialog()
}

class ProfileFragment : BaseFragment(), ProfileInterface {

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
                        .setMessage(getString(R.string.dialog_message_are_you_sure))
                        .setPositiveButton(getString(R.string.dialog_yes)) { dialog, _ ->
                            presenter.onDeleteClick(id)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                },
                onEditClick = { id ->
                    presenter.onEditProfileClick(id)
                }, onItemClick = { id ->
                    hideProfileBottomDialog()
                    presenter.selectProfile(id)
                })
            listProfiles.adapter = profilesAdapter
        }

        buttonAddProfile.setOnClickListener {
            presenter.addNewProfileClick()
        }

        editName.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setName(it.toString())
                } else {
                    presenter.setName("")
                }
            }
        }

        editAge.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setAge(it.toString().toInt())
                } else {
                    presenter.setAge(0)
                }
            }
        }

        editWeight.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setWeight(it.toString().toFloat())
                } else {
                    presenter.setWeight(0.0F)
                }
            }
        }

        editHeight.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setHeight(it.toString().toFloat())
                } else {
                    presenter.setHeight(0.0F)
                }
            }
        }

        radioMale.setOnClickListener {
            presenter.setGender("M")
        }

        radioFemale.setOnClickListener {
            presenter.setGender("F")
        }

        btnCreate.setOnClickListener {
            presenter.onCreateBtnClick()
        }

        btnCancel.setOnClickListener {
            hideKeyboard()
            newProfileBottomDialog.visibility = View.GONE
        }

        bottomDialogBackground.setOnClickListener {
            if (presenter.checkProfileFilling()) {
                AlertDialog.Builder(context!!)
                    .setMessage(getString(R.string.dialog_message_are_you_sure))
                    .setPositiveButton(getString(R.string.dialog_yes)) { dialog, _ ->
                        closeProfileDialog()
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            } else {
                closeProfileDialog()
            }
        }
    }

    private fun closeProfileDialog() {
        hideKeyboard()
        hideProfileBottomDialog()
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

    override fun hideProfileBottomDialog() {
        newProfileBottomDialog.visibility = View.GONE
        editName.text.clear()
        editAge.text.clear()
        editWeight.text.clear()
        editHeight.text.clear()
        groupGender.clearCheck()
        hideKeyboard()
    }

    override fun showProfileBottomDialog(profile: Profile) {
        newProfileBottomDialog.visibility = View.VISIBLE

        if (profile.getName() == "") {
            toolbarNewProfile.setTitle(R.string.toolbar_new_profile)
        } else {
            toolbarNewProfile.setTitle(R.string.toolbar_update_profile)
        }

        if (profile.getName() == "") {
            editName.setText(profile.getName())
            editAge.setText("")
            editWeight.setText("")
            editHeight.setText("")
        } else {
            editName.setText(profile.getName())
            editAge.setText(profile.getAge().toString())
            editWeight.setText(profile.getWeight().toString())
            editHeight.setText(profile.getHeight().toString())
            when (profile.getGender()) {
                "M" -> radioMale.toggle()
                "F" -> radioFemale.toggle()
            }
        }
    }
}