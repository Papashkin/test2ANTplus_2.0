package com.antsfamily.biketrainer.ui.profiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.databinding.FragmentProfilesBinding
import com.antsfamily.biketrainer.presentation.profiles.ProfilesViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.util.mapDistinct
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilesFragment : BaseFragment(R.layout.fragment_profiles) {

    override val viewModel: ProfilesViewModel by viewModels { withFactory(viewModelFactory) }

    @Inject
    lateinit var profilesAdapter: ProfilesAdapter

//    private lateinit var profileCallback: ItemTouchHelper.Callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfilesBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentProfilesBinding.bind(view)) {
            observeState(this)
            observeEvents()
            bindInteractions(this)
        }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        toolbarProfiles.setNavigationIcon(R.drawable.ic_arrow_back_32)
//        toolbarProfiles.setNavigationOnClickListener { } // viewModel.onBackPressed() }
//        requireActivity().let {
//            profilesAdapter = ProfilesAdapter(
//                onDeleteClick = { pos -> viewModel.onDeleteClick(pos) },
//                onEditClick = { id -> viewModel.onEditProfileClick(id)
//                }, onItemClick = { id ->
//                    hideProfileSettingDialog()
//                    viewModel.selectProfile(id, false)
//                }
//            )
//            profileCallback = ProfilesSwipeCallback(profilesAdapter)
//            ItemTouchHelper(profileCallback).attachToRecyclerView(listProfiles)
//            listProfiles.adapter = profilesAdapter
//        }
//        editName.afterTextChange { viewModel.setName(it.toString()) }
//        editAge.afterTextChange { viewModel.setAge(it.toString().toInt()) }
//        editWeight.afterTextChange { viewModel.setWeight(it.toString().toFloat()) }
//        editHeight.afterTextChange { viewModel.setHeight(it.toString().toFloat()) }
//        setListeners()
//    }

    private fun observeState(binding: FragmentProfilesBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.isLoading }
                .observe(viewLifecycleOwner) { loadingView.isVisible = it }
            viewModel.state.mapDistinct { it.isProfilesVisible }
                .observe(viewLifecycleOwner) { profilesRv.isVisible = it }
            viewModel.state.mapDistinct { it.isEmptyProfileVisible }
                .observe(viewLifecycleOwner) { emptyListProfiles.isVisible = it }
            viewModel.state.mapDistinct { it.profiles }
                .observe(viewLifecycleOwner) { profilesAdapter.setProfileList(it) }
        }
    }

    private fun bindInteractions(binding: FragmentProfilesBinding) {
        with(binding) {
            backBtn.setOnClickListener { viewModel.onBackButtonClick() }
            addProfileBtn.setOnClickListener { viewModel.addNewProfileClick() }
            profilesRv.adapter = profilesAdapter.apply {
//                setOnDeleteClickListener { viewModel.onDeleteClick(it) }
//                setOnEditClickListener { viewModel.onEditProfileClick(it) }
//                setOnItemClickListener { viewModel.selectProfile(it, false) }
            }
        }
//        radioMale.setOnClickListener { viewModel.setGender("M") }
//        radioFemale.setOnClickListener { viewModel.setGender("F") }
//        btnCreate.setOnClickListener { } //viewModel.onCreateBtnClick() }
//        btnCancel.setOnClickListener { viewModel.onCancelClick() }
//        bottomDialogBackground.setOnClickListener { showExitDialog() }
    }

    private fun observeEvents() {
        // TODO add events (click on profile; create profile; ...)
    }

//    private fun showExitDialog() {
//        if (viewModel.checkProfileFilling()) {
//            AlertDialog.Builder(requireContext())
//                .setMessage(getString(R.string.dialog_message_are_you_sure))
//                .setPositiveButton(getString(R.string.dialog_yes)) { dialog, _ ->
//                    closeProfileDialog()
//                    dialog.dismiss()
//                }
//                .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .create()
//                .show()
//        } else {
//            closeProfileDialog()
//        }
//    }

//    private fun closeProfileDialog() {
//        hideKeyboard()
//        hideProfileSettingDialog()
//    }

//    fun setProfilesList(newProfiles: ArrayList<Pair<String, Int>>) {
//        profilesAdapter.setProfileList(listOf(newProfiles))
//        pbProfiles.visibility = View.GONE
//        if (newProfiles.isEmpty()) {
//            hideProfilesList()
//        } else {
//            showProfilesList()
//        }
//    }

//    fun showProfilesList() {
//        emptyListProfiles.visibility = View.GONE
//        listProfiles.visibility = View.VISIBLE
//    }

//    fun hideProfilesList() {
//        listProfiles.visibility = View.GONE
//        emptyListProfiles.visibility = View.VISIBLE
//    }

//    fun showLoading() {
//        pbProfiles.visibility = View.VISIBLE
//    }

//    fun hideLoading() {
//        pbProfiles.visibility = View.GONE
//    }

//    fun showSnackBar(profileName: String) {
//        Snackbar
//            .make(profileListLayout, "Profile \"$profileName\" was deleted", Snackbar.LENGTH_LONG)
//            .setActionTextColor(Color.YELLOW)
//            .setAction("UNDO") {
////                viewModel.undoDelete()
//            }
//            .show()
//    }

//    fun updateAdapter() {
//        profilesAdapter.notifyDataSetChanged()
//    }

//    fun hideProfileSettingDialog() {
//        newProfileBottomDialog.visibility = View.GONE
//        editName.text.clear()
//        editAge.text.clear()
//        editWeight.text.clear()
//        editHeight.text.clear()
//        groupGender.clearCheck()
//        hideKeyboard()
//    }

//    fun showProfileSettingDialog(profile: Profile) {
//        newProfileBottomDialog.visibility = View.VISIBLE

//        toolbarNewProfile.title = if (profile.getName() == "") {
//            getString(R.string.toolbar_new_profile)
//        } else {
//            getString(R.string.toolbar_update_profile)
//        }

//        if (profile.getName() == "") {
//            editName.setText(profile.getName())
//            editAge.setText("")
//            editWeight.setText("")
//            editHeight.setText("")
//        } else {
//            editName.setText(profile.getName())
//            editAge.setText(profile.getAge().toString())
//            editWeight.setText(profile.getWeight().toString())
//            editHeight.setText(profile.getHeight().toString())
//            when (profile.getGender()) {
//                "M" -> radioMale.toggle()
//                "F" -> radioFemale.toggle()
//            }
//        }
//    }
}
