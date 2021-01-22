package com.antsfamily.biketrainer.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.Program
import com.antsfamily.biketrainer.data.models.ProgramType
import com.antsfamily.biketrainer.presentation.programSettings.ProgramSettingsViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.ui.util.afterTextChange
import com.antsfamily.biketrainer.util.setCommonParams
import com.antsfamily.biketrainer.util.showDialog
import com.antsfamily.biketrainer.util.timeFormat
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_program_settings.*

@AndroidEntryPoint
class ProgramSettingsFragment : BaseFragment(R.layout.fragment_program_settings) {
    companion object {
        const val MODIFIED_PROGRAM_NAME = "modified program name"
        const val MODIFIED_PROGRAM_SETTING = "modified program setting"
        const val MODIFIED_PROGRAM_IMAGE_PATH = "modified program image path"

        fun newInstance(program: Program?): ProgramSettingsFragment =
            ProgramSettingsFragment().apply {
                this.arguments = bundleOf(
                    MODIFIED_PROGRAM_NAME to program?.getName(),
                    MODIFIED_PROGRAM_IMAGE_PATH to program?.getImagePath(),
                    MODIFIED_PROGRAM_SETTING to program?.getProgram()
                )
            }
    }

    override val viewModel: ProgramSettingsViewModel by viewModels { withFactory(viewModelFactory) }

    override fun onPause() {
        super.onPause()
        viewModel.clear()
        clearTextFields()
    }

    private lateinit var modifiedProgram: Pair<String, String>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.arguments?.apply {
            val programName = this.getString(MODIFIED_PROGRAM_NAME)
            val programSetting = this.getString(MODIFIED_PROGRAM_SETTING)
            val programImagePath = this.getString(MODIFIED_PROGRAM_IMAGE_PATH)

            if (!programName.isNullOrBlank()) {
                modifiedProgram = Pair(programName, programSetting!!)
                editProgramName.setText(programName)
                viewModel.onEditExistedProgramOpen(modifiedProgram, programImagePath)
            } else {
                viewModel.onNewProgramCreate()
            }
        }

        toolbarProgramSettings.setNavigationIcon(R.drawable.ic_arrow_back_32)

        initTextWatchers()
        initListeners()
        initObservers()
        initBottomSheetCallback()
    }

    private fun initTextWatchers() {
        editTargetPower.afterTextChange { viewModel.setTargetPower(it.toFloat()) }
        editRestPower.afterTextChange { viewModel.setRestPower(it.toFloat()) }
        editDuration.afterTextChange { viewModel.setDuration(it.toFloat()) }
        editRestDuration.afterTextChange { viewModel.setRestDuration(it.toFloat()) }
        editIntervalsCount.afterTextChange { viewModel.setIntervalCount(it.toInt()) }
        editProgramName.afterTextChange { viewModel.setProgramName(it) }
    }

    private fun initListeners() {
        toolbarProgramSettings.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }

        btnSegment.setOnClickListener {
            viewModel.addProgramClick(ProgramType.SEGMENT)
        }

        btnIntervals.setOnClickListener {
            viewModel.addProgramClick(ProgramType.INTERVAL)
        }

        btnUpstairs.setOnClickListener {
            viewModel.addProgramClick(ProgramType.STEPS_UP)
        }

        btnDownstairs.setOnClickListener {
            viewModel.addProgramClick(ProgramType.STEPS_DOWN)
        }

        buttonSaveProgram.setOnClickListener {
            chartProgram?.data?.setDrawValues(false)
            chartProgram?.legend?.isEnabled = false
            viewModel.onSaveClick()
        }

        btnAdd.setOnClickListener {
            viewModel.onAddClick()
        }

        btnCancel.setOnClickListener {
            viewModel.onCancelClick()
        }

        chartProgram.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {}

            override fun onValueSelected(e: Entry, h: Highlight?) {
                viewModel.onModifyClick(e as BarEntry)
            }
        })
    }

    private fun initObservers() {
        viewModel.keyboard.observe(viewLifecycleOwner, Observer {
            if (it) showKeyboard() else hideKeyboard()
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it is Int) showToast(it) else showToast(it as String)
            }
        })
        viewModel.programDialog.observe(viewLifecycleOwner, Observer {
            if (it) showProgramBottomDialog() else hideProgramBottomDialog()
        })
        viewModel.barChart.observe(viewLifecycleOwner, Observer {
            if (it != null) updateChart(it.first, it.second)
        })
        viewModel.programTypeAndData.observe(viewLifecycleOwner, Observer {
            setProgramType(it.first, it.second, it.third)
        })
        viewModel.chartGetter.observe(viewLifecycleOwner, Observer {
            if (it) getChart()
        })
        viewModel.backDialog.observe(viewLifecycleOwner, Observer {
            if (it) showBackDialog()
        })
    }

    private fun initBottomSheetCallback() {
        BottomSheetBehavior.from(programSettingLayout)
            .setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(view: View, offset: Float) {
                    viewBottomSheetBackground.alpha = offset
                }

                override fun onStateChanged(view: View, state: Int) {
                    hideKeyboard()
                    if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                        viewBottomSheetBackground.isClickable = false
                        clearTextFields()
                    }
                    if (state == BottomSheetBehavior.STATE_EXPANDED) {
                        editProgramName.clearFocus()
                        viewBottomSheetBackground.isClickable = true
                    }
                }
            })
    }

    private fun updateChart(data: BarData, duration: ArrayList<Float>) {
        chartProgram.visibility = View.VISIBLE
        val timeLabels = duration.map { it.toLong().timeFormat() }
        chartProgram.setCommonParams(data, timeLabels)
        chartProgram.invalidate()
    }

    private var dialog: Dialog? = null
    private fun hideLoading() {
        dialog?.dismiss()
    }

    private fun showLoading() {
        dialog = showDialog(requireActivity(), getString(R.string.dialog_program_saving))
    }

    private fun clearTextFields() {
        editDuration.text = null
        editTargetPower.text = null
        editIntervalsCount.text = null
        editRestDuration.text = null
        editRestPower.text = null

        editDuration.clearFocus()
        editTargetPower.clearFocus()
        editIntervalsCount.clearFocus()
        editRestDuration.clearFocus()
        editRestPower.clearFocus()
    }

    private fun setProgramType(type: ProgramType, power: Float?, duration: Float?) {
        when (type) {
            ProgramType.SEGMENT -> {
                layoutIntervalsCount.isVisible = false
                layoutRestDurationTime.isVisible = false
                layoutRestPower.isVisible = false
                layoutTargetPower.hint = getString(R.string.program_settings_power)
                layoutRestPower.hint = getString(R.string.program_settings_rest_power)
                layoutDurationTime.hint = getString(R.string.program_settings_time)
                if (power != null) {
                    editTargetPower.setText(power.toString())
                }
                if (duration != null) {
                    editDuration.setText(duration.div(60).toString())
                }
            }
            ProgramType.INTERVAL -> {
                layoutIntervalsCount.isVisible = true
                layoutRestDurationTime.isVisible = true
                layoutRestPower.isVisible = true
                layoutTargetPower.hint = getString(R.string.program_settings_power)
                layoutRestPower.hint = getString(R.string.program_settings_rest_power)
                layoutDurationTime.hint = getString(R.string.program_settings_time)
            }
            ProgramType.STEPS_UP, ProgramType.STEPS_DOWN -> {
                layoutIntervalsCount.isVisible = false
                layoutRestDurationTime.isVisible = false
                layoutRestPower.isVisible = true
                layoutTargetPower.hint = getString(R.string.program_settings_max_power)
                layoutRestPower.hint = getString(R.string.program_settings_min_power)
                layoutDurationTime.hint = getString(R.string.program_setting_common_time)
            }
        }
    }

    private fun getChart() {
        viewModel.getProgramImagePath(chartProgram)
    }

    private fun showProgramBottomDialog() {
        BottomSheetBehavior.from(programSettingLayout).state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideProgramBottomDialog() {
        BottomSheetBehavior.from(programSettingLayout).state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun showBackDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage(getString(R.string.dialog_save_program_question))
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(resources.getText(R.string.dialog_yes)) { dialog, _ ->
            viewModel.onSaveClick()
            dialog.dismiss()
        }
        alertDialog.setNegativeButton(resources.getText(R.string.dialog_no)) { dialog, _ ->
            viewModel.onExit()
            dialog.dismiss()
        }
        alertDialog.create().show()
    }
}
