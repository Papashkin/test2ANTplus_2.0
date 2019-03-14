package com.example.test2antplus.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test2antplus.R
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.fullTimeFormat
import com.example.test2antplus.setCommonParams
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.util.*

class ProgramAdapter : RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {
    private var programs: ArrayList<Program> = arrayListOf()
//    private lateinit var programsDiffUtil: ProgramCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramAdapter.ProgramViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_program_info, parent, false)
        return ProgramViewHolder(view)
    }

//    fun addProgram(newProgram: Program) {
//        val oldPrograms = this.getAllData()
//        if (!programs.contains(newProgram)) {
//            programs.add(newProgram)
//        }
//        programsDiffUtil = ProgramCallback(oldPrograms, programs)
//        val productDiffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(programsDiffUtil, false)
//        productDiffResult.dispatchUpdatesTo(this)
//        this.notifyItemInserted(programs.size)
//    }

//    fun getSelectedPrograms(): Program = selectedProgram

//    fun getAllData() = programs

    override fun getItemCount(): Int = programs.size

    override fun onBindViewHolder(holder: ProgramAdapter.ProgramViewHolder, position: Int) {
        holder.bind(this.programs[position])
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
        private val programChart = view.findViewById<BarChart>(R.id.chartProgram)
        private val maxPower = view.findViewById<TextView>(R.id.textMaxPower)

        fun bind(program: Program) {

            val programSource = program.getProgram()
            programName.text = program.getName()
            avgPower.text = getAveragePower(programSource)
            maxPower.text = getMaxPower(programSource)
            duration.text = getDuration(programSource)

            programChart.setCommonParams(getChartData(programSource))
            programChart.setTouchEnabled(false)
            programChart.invalidate()

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

            return "Average power: ${avgPower / count} W"
        }

        private fun getMaxPower(program: String): CharSequence {
            var maxPower = 0L
            program.split("|").forEach {
                val power = it.split("*").last()
                if (power.isNotEmpty()) {
                    if (power.toBigDecimal().toLong() > maxPower) {
                        maxPower = power.toBigDecimal().toLong()
                    }
                }
            }
            return "Max power: $maxPower W"
        }

        private fun getChartData(program: String): BarData {
            var count = 0
            val entries: ArrayList<BarEntry> = arrayListOf()
            program.split("|").forEach {
                if (it.isNotEmpty()) {
                    val power = it.split("*").last().toFloat()
                    entries.add(BarEntry(count.toFloat(), power))
                    count += 1
                }
            }
            val chart = BarDataSet(entries, "")
            chart.barBorderWidth = 0f
            chart.setValueFormatter { _, _, _, _ ->
                ""
            }
            return BarData(chart)
        }

        private fun getDuration(program: String): CharSequence {
            var count = 0.0f
            program.split("|").forEach {
                if (it.isNotEmpty()) {
                    count += it.split("*").first().toFloat()
                }
            }
            return "Duration: ${count.toLong().fullTimeFormat()}"
        }
    }

}