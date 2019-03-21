package com.example.test2antplus.ui.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.example.test2antplus.MainApplication
import com.example.test2antplus.MainApplication.Companion.ACTION_PROGRAM_SETTINGS
import com.example.test2antplus.MainApplication.Companion.ARGS_PROGRAM
import com.example.test2antplus.MainApplication.Companion.UPD_PROGRAMS_LIST
import com.example.test2antplus.R
import com.example.test2antplus.presenter.ProgramSettingsPresenter
import com.example.test2antplus.setCommonParams
import com.example.test2antplus.showDialog
import com.github.mikephil.charting.data.BarData
import com.pawegio.kandroid.inputMethodManager
import com.pawegio.kandroid.textWatcher
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.card_program_info.*
import kotlinx.android.synthetic.main.fragment_program_settings.*

interface ProgramSettingsInterface {
    fun updateChart(data: BarData, duration: ArrayList<Float>)
    fun showAddPowerFab()
    fun hideAddPowerFab()
    fun showLoading()
    fun hideLoading()
    fun clearTextFields()

    fun setViewsEnabled()
    fun setViewsDisabled()
    fun setProgramType(type: Int)

    fun showToast(text: String)
    fun showToast(id: Int)

    fun showKeyboard()
    fun hideKeyboard()

    fun closeScreen()

    fun getChart()
}

class ProgramSettingsFragment : Fragment(), ProgramSettingsInterface {
    companion object {
        const val NOTHING = 0
        const val SINGLE = 1
        const val INTERVAL = 2
        const val STAIRS = 3
    }

    private lateinit var presenter: ProgramSettingsPresenter

    private var dialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_program_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = ProgramSettingsPresenter(this)

        toolbarProgramSettings.setNavigationIcon(R.drawable.ic_arrow_back_32)
        toolbarProgramSettings.setNavigationOnClickListener {
            presenter.onBackPressed()
        }

        fabAddPower.hide()

        editProgramName.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setProgramName(it.toString())
                } else {
                    presenter.setProgramName("")
                }
            }
        }

        editTargetPower.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setTargetPower(it.toString().toFloat())
                } else {
                    presenter.setTargetPower(0.0f)
                }
            }
        }

        editDuration.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setDuration(it.toString().toFloat())
                } else {
                    presenter.setDuration(0.0f)
                }
            }
        }

        editIntervalsCount.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setIntervalCount(it.toString().toInt())
                } else {
                    presenter.setIntervalCount(0)
                }
            }
        }

        editRestDuration.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setRestDuration(it.toString().toFloat())
                } else {
                    presenter.setRestDuration(0.0f)
                }
            }
        }

        editRestPower.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setRestPower(it.toString().toFloat())
                } else {
                    presenter.setRestPower(0.0f)
                }
            }
        }

        spinType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                presenter.setProgramType(NOTHING)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.setProgramType(id.toInt())
            }
        }

        fabAddPower.setOnClickListener {
            presenter.onAddClick()
        }

        fabCreateProgram.setOnClickListener {
            presenter.saveProgram()
        }
    }

    override fun updateChart(data: BarData, duration: ArrayList<Float>) {
        chartProgram.setCommonParams(data)
        chartProgram.setTouchEnabled(true)
        chartProgram.invalidate()
    }

    override fun showAddPowerFab() {
        fabAddPower.show()
    }

    override fun hideAddPowerFab() {
        fabAddPower.hide()
    }

    override fun hideLoading() {
        dialog?.dismiss()
    }

    override fun showLoading() {
        dialog = showDialog(requireActivity(), "Сохранение программы")
    }

    override fun clearTextFields() {
        editDuration.setText("")
        editTargetPower.setText("")
        editIntervalsCount.setText("")
        editRestDuration.setText("")
        editRestPower.setText("")
    }

    override fun setViewsEnabled() {
        editDuration.isEnabled = true
        editTargetPower.isEnabled = true
        editIntervalsCount.isEnabled = true
        editRestDuration.isEnabled = true
        editRestPower.isEnabled = true
    }

    override fun setViewsDisabled() {
        editDuration.isEnabled = false
        editTargetPower.isEnabled = false
        editIntervalsCount.isEnabled = false
        editRestDuration.isEnabled = false
        editRestPower.isEnabled = false
    }

    override fun setProgramType(type: Int) {
        when (type) {
            SINGLE -> {
                layoutIntervalsCount.visibility = View.INVISIBLE
                layoutRestDurationTime.visibility = View.INVISIBLE
                layoutRestPower.visibility = View.INVISIBLE
                textTargetPower.text = getString(R.string.program_settings_power)
                textRestPower.text = getString(R.string.program_settings_rest_power)
                textDurationTime.text = getString(R.string.program_settings_duration)
            }
            INTERVAL -> {
                layoutIntervalsCount.visibility = View.VISIBLE
                layoutRestDurationTime.visibility = View.VISIBLE
                layoutRestPower.visibility = View.VISIBLE
                textTargetPower.text = getString(R.string.program_settings_power)
                textRestPower.text = getString(R.string.program_settings_rest_power)
                textDurationTime.text = getString(R.string.program_settings_duration)
            }
            STAIRS -> {
                layoutIntervalsCount.visibility = View.INVISIBLE
                layoutRestDurationTime.visibility = View.INVISIBLE
                layoutRestPower.visibility = View.VISIBLE
                textTargetPower.text = getString(R.string.program_settings_max_power)
                textRestPower.text = getString(R.string.program_settings_min_power)
            }
            NOTHING -> {
                setViewsDisabled()
            }
        }
    }

    override fun showToast(text: String) {
        toast(text)
    }

    override fun showToast(id: Int) {
        toast(id)
    }

    override fun showKeyboard() {
        val inputMethodManager = activity?.inputMethodManager
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    override fun hideKeyboard() {
        val inputMethodManager = activity?.inputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun closeScreen() {
        activity?.sendBroadcast(Intent(ACTION_PROGRAM_SETTINGS).apply {
            this.putExtra(UPD_PROGRAMS_LIST, ARGS_PROGRAM)
        })
    }

    override fun getChart() {
        presenter.getProgramImagePath(chartProgram)
    }
}