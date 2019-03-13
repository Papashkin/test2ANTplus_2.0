package com.example.test2antplus.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.test2antplus.Program
import com.example.test2antplus.R
import com.example.test2antplus.formatToTime
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

class ProgramAdapter : RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {
    private var programs: ArrayList<Program> = arrayListOf()
    private lateinit var selectedProgram: Program
    private lateinit var programsDiffUtil: ProgramCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramAdapter.ProgramViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_program_info, parent, false)
        return ProgramViewHolder(view)
    }

    fun addProgram(newProgram: Program) {
        val oldPrograms = this.getData()
        if (!programs.contains(newProgram)) {
            programs.add(newProgram)
        }
        programsDiffUtil = ProgramCallback(oldPrograms, programs)
        val productDiffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(programsDiffUtil, false)
        productDiffResult.dispatchUpdatesTo(this)
        this.notifyItemInserted(programs.size)
    }

    fun getSelectedPrograms(): Program = selectedProgram

    fun getAllData() = programs

    private fun getData(): ArrayList<Program> = programs

    override fun getItemCount(): Int = programs.size

    override fun onBindViewHolder(holder: ProgramAdapter.ProgramViewHolder, position: Int) {
        holder.bind(this.programs[position], position)
    }

    fun setProgramList(newPrograms: ArrayList<Program>) {
        programs.clear()
        programs.addAll(newPrograms)
        notifyDataSetChanged()
    }


    inner class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val programName = view.findViewById<TextView>(R.id.textProgramName)
        private val avgPower = view.findViewById<TextView>(R.id.textAveragePower)
        private val duration = view.findViewById<TextView>(R.id.textDuration)
        private val programChart = view.findViewById<LineChart>(R.id.chartProgram)
        private val checkBox = view.findViewById<CheckBox>(R.id.checkBoxIsSelected)

        fun bind(program: Program, position: Int) {

            val programSource = program.getProgram()
            programName.text = program.getName()
            avgPower.text = getAveragePower(programSource)
            duration.text = getDuration(programSource)

            programChart.data = getChartData(programSource)
            programChart.axisLeft.isEnabled = false
            programChart.xAxis.isEnabled = false
            programChart.invalidate()

            checkBox.isChecked = false
            checkBox.setOnClickListener {

            }
        }

        /**
         * (<time>*<power>|<time>*<power>|...)
         */
        private fun getAveragePower(program: String): CharSequence {
            var avgPower = 0L
            var count = 0
            program.split("|").forEach {
                val power = it.split("*").last()
                if (power.isNotEmpty()) {
                    avgPower += power.toBigDecimal().toLong()
                    count += 1
                }
            }

            return "Average power: ${avgPower / count}"
        }

        private fun getChartData(program: String): LineData {
            val entries: ArrayList<Entry> = arrayListOf()
            program.split("|").forEach {
                if (it.isNotEmpty()) {
                    val time = it.split("*").first().toFloat()
                    val power = it.split("*").last().toFloat()
                    entries.add(Entry(time, power))
                }
            }
            val chart = LineDataSet(entries, "")
            chart.setDrawFilled(true)
            return LineData(chart)
        }

        private fun getDuration(program: String): CharSequence {
            var count = 0L
            program.split("|").forEach {
                if (it.isNotEmpty()) {
                    count += 1
                }
            }

            val time = count.formatToTime()
            return "Duration: $time"
        }
    }

}