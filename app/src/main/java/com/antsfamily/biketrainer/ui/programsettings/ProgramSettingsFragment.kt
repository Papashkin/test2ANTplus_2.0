package com.antsfamily.biketrainer.ui.programsettings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.antsfamily.biketrainer.*
import com.antsfamily.biketrainer.MainApplication.Companion.ACTION_PROGRAM_SETTINGS
import com.antsfamily.biketrainer.MainApplication.Companion.ARGS_PROGRAM
import com.antsfamily.biketrainer.MainApplication.Companion.UPD_PROGRAMS_LIST
import com.antsfamily.biketrainer.data.repositories.programs.Program
import com.antsfamily.biketrainer.presentation.BaseFragment
import com.antsfamily.biketrainer.presentation.programSettings.ProgramSettingsPresenter
import com.antsfamily.biketrainer.presentation.programSettings.ProgramSettingsView
import com.antsfamily.biketrainer.util.setCommonParams
import com.antsfamily.biketrainer.util.showDialog
import com.antsfamily.biketrainer.util.timeFormat
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.pawegio.kandroid.textWatcher
import kotlinx.android.synthetic.main.dialog_new_program.*
import kotlinx.android.synthetic.main.fragment_program_setting_new.*


class ProgramSettingsFragment : BaseFragment(R.layout.fragment_program_setting_new),
    ProgramSettingsView {
    companion object {
        const val SEGMENT = 0
        const val INTERVAL = 1
        const val STEPS_UP = 2
        const val STEPS_DOWN = 3
        const val MODIFIED_PROGRAM_NAME = "modified program name"
        const val MODIFIED_PROGRAM_SETTING = "modified program setting"
        const val MODIFIED_PROGRAM_IMAGE_PATH = "modified program image path"

        fun newInstance(program: Program?): ProgramSettingsFragment = ProgramSettingsFragment()
            .apply {
            this.arguments = Bundle().also {
                it.putString(MODIFIED_PROGRAM_NAME, program?.getName())
                it.putString(MODIFIED_PROGRAM_SETTING, program?.getProgram())
                it.putString(MODIFIED_PROGRAM_IMAGE_PATH, program?.getImagePath())
            }
        }
    }

    private lateinit var presenter: ProgramSettingsPresenter
    private lateinit var modifiedProgram: Pair<String, String>

    private var dialog: Dialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.graph.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter =
            ProgramSettingsPresenter(
                this
            )

        this.arguments?.apply {
            val programName = this.getString(MODIFIED_PROGRAM_NAME)
            val programSetting = this.getString(MODIFIED_PROGRAM_SETTING)
            val programImagePath = this.getString(MODIFIED_PROGRAM_IMAGE_PATH)

            if (programName != null) {
                modifiedProgram = Pair(programName, programSetting!!)
                presenter.onEditExistedProgramOpen(modifiedProgram, programImagePath)
            } else {
                presenter.onNewProgramCreate()
            }
        }

        toolbarProgramSettings.setNavigationIcon(R.drawable.ic_arrow_back_32)


        editTargetPower.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setTargetPower(it.toString().toFloat())
                } else {
                    presenter.setTargetPower(0.0f)
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

        editDuration.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setDuration(it.toString().toFloat())
                } else {
                    presenter.setDuration(0.0f)
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

        editIntervalsCount.textWatcher {
            afterTextChanged {
                if (!it.isNullOrEmpty()) {
                    presenter.setIntervalCount(it.toString().toInt())
                } else {
                    presenter.setIntervalCount(0)
                }
            }
        }

        setListeners()

    }

    private fun setListeners() {
        toolbarProgramSettings.setNavigationOnClickListener {
            presenter.onBackPressed()
        }

        programSegment.setOnClickListener {
            presenter.addProgramClick(SEGMENT)
        }

        programIntervals.setOnClickListener {
            presenter.addProgramClick(INTERVAL)
        }

        programStepsUp.setOnClickListener {
            presenter.addProgramClick(STEPS_UP)
        }

        programStepsDown.setOnClickListener {
            presenter.addProgramClick(STEPS_DOWN)
        }

        buttonSaveProgram.setOnClickListener {
            chartProgram.data.setDrawValues(false)
            chartProgram.legend.isEnabled = false
            presenter.showSaveDialog()
        }

        btnAdd.setOnClickListener {
            presenter.onAddClick()
        }

        btnCancel.setOnClickListener {
            presenter.onCancelClick()
        }

        chartProgram.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {}

            override fun onValueSelected(e: Entry, h: Highlight?) {
                presenter.onModifyClick(e as BarEntry)
            }

        })
    }

    override fun updateChart(data: BarData, duration: ArrayList<Float>) {
        chartProgram.visibility = View.VISIBLE
        val timeLabels = duration.map { it.toLong().timeFormat() }
        chartProgram.setCommonParams(data, timeLabels)
        chartProgram.invalidate()
    }

    override fun hideLoading() {
        dialog?.dismiss()
    }

    override fun showLoading() {
        dialog = showDialog(requireActivity(), getString(R.string.dialog_program_saving))
    }

    override fun clearTextFields() {
        editDuration.setText("")
        editTargetPower.setText("")
        editIntervalsCount.setText("")
        editRestDuration.setText("")
        editRestPower.setText("")
    }

    override fun setProgramType(type: Int, powerAndTime: Pair<Float, Float>?) {
        when (type) {
            SEGMENT -> {
                layoutIntervalsCount.visibility = View.INVISIBLE
                layoutRestDurationTime.visibility = View.INVISIBLE
                layoutRestPower.visibility = View.INVISIBLE
                textTargetPower.text = getString(R.string.program_settings_power)
                textRestPower.text = getString(R.string.program_settings_rest_power)
                textDurationTime.text = getString(R.string.program_settings_time)
                if (powerAndTime != null) {
                    editTargetPower.setText(powerAndTime.first.toString())
                    editDuration.setText(powerAndTime.second.div(60).toString())
                }
            }
            INTERVAL -> {
                layoutIntervalsCount.visibility = View.VISIBLE
                layoutRestDurationTime.visibility = View.VISIBLE
                layoutRestPower.visibility = View.VISIBLE
                textTargetPower.text = getString(R.string.program_settings_power)
                textRestPower.text = getString(R.string.program_settings_rest_power)
                textDurationTime.text = getString(R.string.program_settings_time)
            }
            STEPS_UP, STEPS_DOWN -> {
                layoutIntervalsCount.visibility = View.INVISIBLE
                layoutRestDurationTime.visibility = View.INVISIBLE
                layoutRestPower.visibility = View.VISIBLE
                textTargetPower.text = getString(R.string.program_settings_max_power)
                textRestPower.text = getString(R.string.program_settings_min_power)
                textDurationTime.text = getString(R.string.program_setting_common_time)
            }
            else -> {
            }
        }
    }

    override fun closeScreen() {
        activity?.sendBroadcast(Intent(ACTION_PROGRAM_SETTINGS).apply {
            this.putExtra(UPD_PROGRAMS_LIST, ARGS_PROGRAM)
        })
    }

    override fun getChart() {
        presenter.getProgramImagePath(chartProgram)
    }

    override fun showProgramBottomDialog() {
        newProgramBottomDialog.visibility = View.VISIBLE
    }

    override fun hideProgramBottomDialog() {
        newProgramBottomDialog.visibility = View.GONE
    }

    override fun showBackDialog() {
        val alertDialog = AlertDialog.Builder(context!!)
        alertDialog.setMessage(getString(R.string.dialog_save_program_question))
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(resources.getText(R.string.dialog_yes)) { dialog, _ ->
            presenter.showSaveDialog()
            dialog.dismiss()
        }
        alertDialog.setNegativeButton(resources.getText(R.string.dialog_no)) { dialog, _ ->
            presenter.onExit()
            dialog.dismiss()
        }
        alertDialog.create().show()
    }

    override fun showProgramNameDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_program_name, null)

        val programNameDialog = AlertDialog.Builder(context!!)

        programNameDialog.setView(dialogView)

        val inputText = dialogView.findViewById<EditText>(R.id.etProgramName)

        programNameDialog
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_ok)) { dialog, _ ->
                if (inputText.text.isEmpty()) {
                    showToast(R.string.dialog_empty_program_name)
                } else {
                    presenter.setProgramName(inputText.text.toString())
                    dialog.dismiss()
                }
            }
            .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }
}