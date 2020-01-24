package com.example.test2antplus.presentation.profiles

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.data.repositories.profiles.Profile
import com.example.test2antplus.presentation.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.pawegio.kandroid.textWatcher
import kotlinx.android.synthetic.main.dialog_new_profile.*
import kotlinx.android.synthetic.main.fragment_profiles.*


class ProfilesFragment : BaseFragment(R.layout.fragment_profiles),
    ProfilesView {

    private lateinit var presenter: ProfilesPresenter
    private lateinit var profilesAdapter: ProfilesAdapter
    private lateinit var profileCallback: ItemTouchHelper.Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.graph.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = ProfilesPresenter(this)

        toolbarProfiles.setNavigationIcon(R.drawable.ic_arrow_back_32)
        toolbarProfiles.setNavigationOnClickListener {
            presenter.onBackPressed()
        }

        activity?.let {
            profilesAdapter = ProfilesAdapter(
                onDeleteClick = { pos ->
                    presenter.onDeleteClick(pos)
                },
                onEditClick = { id ->
                    presenter.onEditProfileClick(id)
                }, onItemClick = { id ->
                    hideProfileSettingDialog()
                    presenter.selectProfile(id)
                })

            profileCallback = ProfilesSwipeCallback(profilesAdapter)
            ItemTouchHelper(profileCallback).attachToRecyclerView(listProfiles)
            listProfiles.adapter = profilesAdapter
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

        setListeners()
    }

    private fun setListeners() {
        buttonAddProfile.setOnClickListener {
            presenter.addNewProfileClick()
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
            presenter.onCancelClick()
        }

        bottomDialogBackground.setOnClickListener {
            showExitDialog()
        }
    }

    private fun showExitDialog() {
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

    private fun closeProfileDialog() {
        hideKeyboard()
        hideProfileSettingDialog()
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

    override fun showSnackBar(profileName: String) {
        Snackbar
            .make(profileListLayout, "Profile \"$profileName\" was deleted", Snackbar.LENGTH_LONG)
            .setActionTextColor(Color.YELLOW)
            .setAction("UNDO") {
                presenter.undoDelete()
            }
            .show()
    }

    override fun updateAdapter() {
        profilesAdapter.notifyDataSetChanged()
    }

    override fun hideProfileSettingDialog() {
        newProfileBottomDialog.visibility = View.GONE
        editName.text.clear()
        editAge.text.clear()
        editWeight.text.clear()
        editHeight.text.clear()
        groupGender.clearCheck()
        hideKeyboard()
    }

    override fun showProfileSettingDialog(profile: Profile) {
        newProfileBottomDialog.visibility = View.VISIBLE

        toolbarNewProfile.title = if (profile.getName() == "") {
            getString(R.string.toolbar_new_profile)
        } else {
            getString(R.string.toolbar_update_profile)
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