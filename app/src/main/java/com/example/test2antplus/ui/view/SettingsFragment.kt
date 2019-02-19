package com.example.test2antplus.ui.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.presenter.SettingPresenter
import com.pawegio.kandroid.textWatcher
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

interface SettingsInterface {
    fun showToast(text: String)
}

class SettingsFragment: Fragment(), SettingsInterface {

    @Inject lateinit var appContext: Context

    private lateinit var presenter: SettingPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = SettingPresenter(this)
        super.onViewCreated(view, savedInstanceState)

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

        editHeight.textWatcher {
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

        btnCancel.setOnClickListener {
            presenter.onCancelClick()
        }

        btnCreate.setOnClickListener {
            presenter.createProfile()
        }
    }

    override fun showToast(text: String) {
        toast(text)
    }
}