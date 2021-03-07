package com.antsfamily.biketrainer.ui.util

import android.view.MotionEvent
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.ui.util.BarCharsStaticFields.BAR_BORDER_WIDTH
import com.antsfamily.biketrainer.ui.util.BarCharsStaticFields.BAR_GRANULARITY
import com.antsfamily.biketrainer.ui.util.BarCharsStaticFields.FORMATTER_SCALE_X
import com.antsfamily.biketrainer.util.orZero
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import javax.inject.Inject

class BarChartGestureListener @Inject constructor() : OnChartGestureListener {

    private var barChart: BarChart? = null

    private var labels: List<String> = emptyList()

    private val isLabelsLessThanFive: Boolean
        get() = labels.size <= 5

    private val isChartScaledTwice: Boolean
        get() = barChart?.scaleX.orZero() > FORMATTER_SCALE_X

    override fun onChartGestureStart(
        me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {}

    override fun onChartGestureEnd(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {
    }

    override fun onChartLongPressed(me: MotionEvent?) {}

    override fun onChartDoubleTapped(me: MotionEvent?) {}

    override fun onChartSingleTapped(me: MotionEvent?) {}

    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) {
    }

    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
        barChart?.let {
            it.xAxis.apply {
                isEnabled = (isLabelsLessThanFive or isChartScaledTwice)
                setDefaultSettings(labels, it.getXAxisValueFormatter(labels))
            }
        }
    }

    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}

    fun setBarChart(barChart: BarChart) {
        this.barChart = barChart
    }

    fun setLabels(labels: List<String>) {
        this.labels = labels
    }
}


fun BarChart.getYAxisValueFormatter(entrySize: Int) = object : ValueFormatter() {
    private val isLabelsLessThanFive: Boolean
        get() = entrySize <= 5

    private val isChartScaledTwice: Boolean
        get() = this@getYAxisValueFormatter.scaleX > FORMATTER_SCALE_X

    override fun getBarLabel(entry: BarEntry): String {
        return if (isLabelsLessThanFive or isChartScaledTwice) entry.y.toString() else ""
    }
}

fun BarChart.getXAxisValueFormatter(labels: List<String>) = object : ValueFormatter() {
    private val isLabelsLessThanFive: Boolean
        get() = labels.size <= 5

    private val isChartScaledTwice: Boolean
        get() = this@getXAxisValueFormatter.scaleX > FORMATTER_SCALE_X

    override fun getFormattedValue(value: Float): String =
        if (isLabelsLessThanFive or isChartScaledTwice) {
            labels.getOrNull(value.toInt()).orEmpty()
        } else {
            ""
        }
}

fun BarChart.setDefaultBaseSettings(entryCount: Int) = this.apply {
    isScaleYEnabled = false
    isScaleXEnabled = entryCount > 5
    setTouchEnabled(true)
    description.isEnabled = false
    legend.isEnabled = false
    axisLeft.isEnabled = true
    axisRight.isEnabled = false
    setDrawGridBackground(false)
    setDrawBorders(false)
}

fun XAxis.setDefaultSettings(labels: List<String>, formatter: ValueFormatter) = this.apply {
    position = XAxis.XAxisPosition.BOTTOM
    setDrawGridLines(false)
    granularity = BAR_GRANULARITY
    labelCount = labels.size
    valueFormatter = formatter
}

fun BarDataSet.setDefaultSettings(formatter: ValueFormatter) = this.apply {
    barBorderWidth = BAR_BORDER_WIDTH
    valueTextSize = 11f
    valueFormatter = formatter
    color = R.color.color_central
    isHighlightEnabled = false
}

fun BarChart.setHighlightedMode(isFull: Boolean) {
    this.apply {
        isHighlightPerTapEnabled = false
        isHighlightFullBarEnabled = false
        isHighlightPerDragEnabled = false
        data?.dataSets?.forEach {
            it.isHighlightEnabled = !isFull
        }
    }
}

fun BarChart.hideAllLabels() {
    this.apply {
        description.isEnabled = false
        legend.isEnabled = false
        xAxis.isEnabled = false
        axisLeft.isEnabled = false
        axisRight.isEnabled = false
    }
}

object BarCharsStaticFields {
    const val FORMATTER_SCALE_X = 2.4f
    const val BAR_GRANULARITY = 1f
    const val BAR_BORDER_WIDTH = 0f
    const val BAR_WIDTH_95 = 0.95f
    const val BAR_WIDTH_100 = 1f
}
