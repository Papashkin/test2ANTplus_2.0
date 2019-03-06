package com.example.test2antplus.ui.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.presenter.ProgramPresenter
import com.example.test2antplus.showDialog
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.pawegio.kandroid.textWatcher
import kotlinx.android.synthetic.main.fragment_program.*

interface ProgramInterface {
    fun updateBarChart(data: LineData)
//    fun updateBarChart(data: BarData)
    fun showAddPowerFab()
    fun hideAddPowerFab()
    fun showLoading()
    fun hideLoading()
    fun clearTextFields()
}

 class ProgramFragment: Fragment(), ProgramInterface {

     private lateinit var presenter: ProgramPresenter

     private var dialog: Dialog? = null

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         MainApplication.graph.inject(this)
         return inflater.inflate(R.layout.fragment_program, container, false)
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         presenter = ProgramPresenter(this)

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

         fabAddPower.setOnClickListener {
             presenter.onAddClick()
         }

         fabCreateProgram.setOnClickListener {
             presenter.saveProgram()
         }
     }

     override fun updateBarChart(data: LineData) {
         chartProgram.data = data
         chartProgram.invalidate()
     }

//     override fun updateBarChart(data: BarData) {
//         chartProgram.data = data
//         chartProgram.invalidate()
//     }

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
 }