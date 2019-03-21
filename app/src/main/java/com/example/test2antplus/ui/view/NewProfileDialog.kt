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

    private val newProfile: Profile = Profile(0, "", 0, "", 0.0f, 0.0f)

    @Inject
    lateinit var profilesRepository: ProfilesRepository

    override fun onAttach(context: Context?) {
        MainApplication.graph.inject(this)
        super.onAttach(context)
    }

    override fun setupDialog(dialog: Dialog?, style: Int) {
//        MainApplication.graph.inject(this)
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
            createProfile()
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
            }.subscribe ({
                this.dismiss()
            },{
                toast("New profile creating failed. Please try it again")
                it.printStackTrace()
            })
        } else {
            toast("Invalid data")
        }
    }
}