package com.example.test2antplus.ui.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.data.profiles.Profile
import com.example.test2antplus.data.profiles.ProfilesRepository
import com.example.test2antplus.workInAsinc
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pawegio.kandroid.textWatcher
import com.pawegio.kandroid.toast
import io.reactivex.Observable
import javax.inject.Inject

class NewProfileDialog : BottomSheetDialogFragment() {

    private var newProfile: Profile = Profile(0, "", 0, "", 0.0f, 0.0f)
    private lateinit var oldProfile: Profile
    private var isNewProfile = true

    @Inject
    lateinit var profilesRepository: ProfilesRepository

    override fun onAttach(context: Context?) {
        MainApplication.graph.inject(this)
        val bundle = arguments
        bundle?.apply {
            isNewProfile = false
            newProfile = Profile(
                id = this.getInt("profileId"),
                name = this.getString("profileName") ?: "",
                age = this.getInt("profileAge"),
                gender = this.getString("profileGender") ?: "",
                weight = this.getFloat("profileWeight"),
                height = this.getFloat("profileHeight")
            )
        }
        super.onAttach(context)
    }

    override fun setupDialog(dialog: Dialog?, style: Int) {
        val contentView = View.inflate(context, R.layout.dialog_new_profile_bottom, null)
        dialog?.setContentView(contentView)

        val createButton = contentView.findViewById<FloatingActionButton>(R.id.btnCreate)
        val cancelButton = contentView.findViewById<FloatingActionButton>(R.id.btnCancel)
        val nameEditor = contentView.findViewById<EditText>(R.id.editName)
        val ageEditor = contentView.findViewById<EditText>(R.id.editAge)
        val maleRadioEditor = contentView.findViewById<RadioButton>(R.id.radioMale)
        val femaleRadioEditor = contentView.findViewById<RadioButton>(R.id.radioFemale)
        val weightEditor = contentView.findViewById<EditText>(R.id.editWeight)
        val heightEditor = contentView.findViewById<EditText>(R.id.editHeight)

        if (newProfile.getName() != "") {
            nameEditor.editableText.append(newProfile.getName())
            ageEditor.editableText.append(newProfile.getAge().toString())
            weightEditor.editableText.append(newProfile.getWeight().toString())
            heightEditor.editableText.append(newProfile.getHeight().toString())
            when (newProfile.getGender()) {
                "M" -> maleRadioEditor.toggle()
                "F" -> femaleRadioEditor.toggle()
            }
        }

        nameEditor.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    newProfile.setName(it.toString())
                } else {
                    newProfile.setName("")
                }
            }
        }

        ageEditor.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    newProfile.setAge(it.toString().toInt())
                } else {
                    newProfile.setAge(0)
                }
            }
        }

        weightEditor.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    newProfile.setWeight(it.toString().toFloat())
                } else {
                    newProfile.setWeight(0.0F)
                }
            }
        }

        heightEditor.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    newProfile.setHeight(it.toString().toFloat())
                } else {
                    newProfile.setHeight(0.0F)
                }
            }
        }

        maleRadioEditor.setOnClickListener {
            newProfile.setGender("M")
        }

        femaleRadioEditor.setOnClickListener {
            newProfile.setGender("F")
        }

        createButton.setOnClickListener {
            if (isNewProfile) {
                createProfile()
            } else {
                updateProfile()
            }
        }

        cancelButton.setOnClickListener {
            this.dismiss()
        }

    }

    @SuppressLint("CheckResult")
    private fun createProfile() {
        if (newProfile.getName().isNotEmpty() && newProfile.getAge() != 0 && newProfile.getWeight() != 0.0F) {
            Observable.fromCallable {
                profilesRepository.insertProfile(newProfile)
            }.compose {
                it.workInAsinc()
            }.subscribe({
                this.dismiss()
            }, {
                toast("New profile creating failed. Please try it again")
                it.printStackTrace()
            })
        } else {
            toast("Invalid data")
        }
    }


    @SuppressLint("CheckResult")
    private fun updateProfile() {
        if (newProfile.getName().isNotEmpty() && newProfile.getAge() != 0 && newProfile.getWeight() != 0.0F) {
            Observable.fromCallable {
                profilesRepository.updateProfile(newProfile)
            }.compose {
                it.workInAsinc()
            }.subscribe({
                this.dismiss()
            }, {
                toast("New profile creating failed. Please try it again")
                it.printStackTrace()
            })
        } else {
            toast("Invalid data")
        }
    }
}