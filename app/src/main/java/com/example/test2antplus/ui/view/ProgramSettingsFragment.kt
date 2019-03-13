package com.example.test2antplus.ui.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.formatToTime
import com.example.test2antplus.presenter.ProgramSettingsPresenter
import com.example.test2antplus.showDialog
import com.github.mikephil.charting.data.LineData
import com.pawegio.kandroid.textWatcher
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.fragment_program_settings.*

interface ProgramSettingsInterface {
    fun updateChart(data: LineData)
    fun showAddPowerFab()
    fun hideAddPowerFab()
    fun showLoading()
    fun hideLoading()
    fun clearTextFields()

    fun setViewsEnabled()
    fun setViewsDisabled()
    fun setProgramType(type: Int)

    fun showToast(text: String)
}

 class ProgramSettingsFragment: Fragment(), ProgramSettingsInterface {
    companion object {
        const val NOTHING = 0
        const val SINGLE = 1
        const val INTERVAL = 2
    }

     private lateinit var presenter: ProgramSettingsPresenter

     private var dialog: Dialog? = null

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         MainApplication.graph.inject(this)
         return inflater.inflate(R.layout.fragment_program_settings, container, false)
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         presenter = ProgramSettingsPresenter(this)

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

     override fun updateChart(data: LineData) {
         var count = 0
         chartProgram.data = data
         chartProgram.xAxis.setValueFormatter { value, _ ->
             count += 1
             val time = value.toLong().formatToTime()
             if (0 == count % 2) {
                 time
             } else {
                 ""
             }
         }
         chartProgram.setDrawGridBackground(false)
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
                 editIntervalsCount.visibility = View.INVISIBLE
                 textIntervalsCount.visibility = View.INVISIBLE
                 editRestDuration.visibility = View.INVISIBLE
                 textRestDurationTime.visibility = View.INVISIBLE
                 editRestPower.visibility = View.INVISIBLE
                 textRestPower.visibility = View.INVISIBLE
             }
             INTERVAL -> {
                 editIntervalsCount.visibility = View.VISIBLE
                 textIntervalsCount.visibility = View.VISIBLE
                 editRestDuration.visibility = View.VISIBLE
                 textRestDurationTime.visibility = View.VISIBLE
                 editRestPower.visibility = View.VISIBLE
                 textRestPower.visibility = View.VISIBLE
             }
             NOTHING -> {
                 setViewsDisabled()
             }
         }
     }

     override fun showToast(text: String) {
         toast(text)
     }
 }