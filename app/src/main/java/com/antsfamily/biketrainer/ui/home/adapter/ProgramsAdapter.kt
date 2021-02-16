package com.antsfamily.biketrainer.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.data.models.program.ProgramData
import com.antsfamily.biketrainer.databinding.CardProgramInfoBinding
import com.antsfamily.biketrainer.util.fullTimeFormat
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import javax.inject.Inject

class ProgramsAdapter @Inject constructor() :
    RecyclerView.Adapter<ProgramsAdapter.ProgramViewHolder>() {

    var items: List<Program> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onItemClickListener: ((item: Program) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val binding =
            CardProgramInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProgramViewHolder(binding)
    }

    fun setOnItemClickListener(listener: (item: Program) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ProgramViewHolder(private val binding: CardProgramInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val emptyFormatter = object : ValueFormatter() {
            override fun getBarLabel(entry: BarEntry): String = ""
        }

        fun bind(item: Program) {
            val data = item.getData()
            with(binding) {
                programNameTv.text = item.getName()
                programDurationTv.text = getTotalTime(data)
                programMaxPowerTv.text = getMaxPower(data)
                programAvgPowerTv.text = getAveragePower(data)
                createChart(data)
                root.setOnClickListener { onItemClickListener?.invoke(item) }
            }
        }

        private fun getAveragePower(data: List<ProgramData>): String =
            data.map { it.power }.sum().div(data.size).toString()

        private fun getMaxPower(data: List<ProgramData>): String =
            data.maxOf { it.power }.toString()

        private fun getTotalTime(data: List<ProgramData>): String =
            data.map { it.duration }.sum().fullTimeFormat()

        private fun createChart(data: List<ProgramData>) {
            val entries = data.mapIndexed { index, _data ->
                BarEntry(index.toFloat(), _data.power.toFloat())
            }
            with(binding.programBc) {
                setScaleEnabled(false)
                setTouchEnabled(true)
                description.isEnabled = false
                legend.isEnabled = false
                xAxis.isEnabled = false
                axisLeft.isEnabled = false
                axisRight.isEnabled = false
                setDrawGridBackground(false)
                setDrawBorders(false)
                this.data = BarData(
                    BarDataSet(entries, "").apply {
                        barBorderWidth = BAR_BORDER_WIDTH
                        valueFormatter = emptyFormatter
                        color = R.color.color_central
                        stackLabels = emptyArray()
                        isHighlightEnabled = false
                    }
                ).apply { barWidth = BAR_WIDTH }
                animateXY(X_ANIMATION, Y_ANIMATION)
            }
        }
    }

    companion object {
        private const val BAR_BORDER_WIDTH = 0f
        private const val BAR_WIDTH = 1f
        private const val Y_ANIMATION = 900
        private const val X_ANIMATION = 700
    }
}
