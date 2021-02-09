package com.antsfamily.biketrainer.ui.createprogram.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.databinding.ViewWorkoutChartBinding
import com.antsfamily.biketrainer.ui.createprogram.model.WorkoutItem
import com.antsfamily.biketrainer.util.timeFormat
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class WorkoutChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var item: WorkoutItem? = null
        set(value) {
            field = value
            setupBarChartData(value)
        }

    var isEmptyDataVisible: Boolean = false
        set(value) {
            field = value
            binding.emptyDataFl.isVisible = value
        }

    var isBarChartVisible: Boolean = false
        set(value) {
            field = value
            binding.workoutBc.isVisible = value
        }

    var error: String? = null
        set(value) {
            field = value
            setupError(value)
        }

    private fun setupError(error: String?) {
        binding.errorTv.text = error
        binding.errorTv.isVisible = error != null
    }

    private val binding: ViewWorkoutChartBinding =
        ViewWorkoutChartBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        setupBarChart()
    }

    private fun setupBarChart() {
        with(binding.workoutBc) {
            setScaleEnabled(false)
            setTouchEnabled(true)
            description = null
            xAxis.setCenterAxisLabels(false)
            xAxis.isEnabled = false
            axisRight.isEnabled = false
        }
    }

    private fun setupBarChartData(item: WorkoutItem?) {
        with(binding) {
            emptyDataFl.isVisible = item == null
            workoutBc.isVisible = item != null
            item?.let {
                val dataSet = it.dataSet.apply {
                    barBorderWidth = BAR_CHAT_VALUE_BORDER_WIDTH
                    stackLabels = it.labels.map { it.timeFormat() }.toTypedArray()
                    color = R.color.color_central
                    isHighlightEnabled = false
                }
                workoutBc.apply {
                    data = BarData(dataSet)
                    data.barWidth = BAR_CHAT_VALUE_WIDTH
                    data.setValueTextSize(BAR_CHAT_VALUE_TEXT_SIZE)
                    xAxis.valueFormatter = object: ValueFormatter() {
                        override fun getBarLabel(entry: BarEntry): String =
                            it.labels[entry.x.toInt()].timeFormat()
                    }
                }
                workoutBc.invalidate()
            }
        }
    }

    companion object {
        private const val BAR_CHAT_VALUE_TEXT_SIZE = 10f
        private const val BAR_CHAT_VALUE_WIDTH = 1f
        private const val BAR_CHAT_VALUE_BORDER_WIDTH = 0f
    }
}
