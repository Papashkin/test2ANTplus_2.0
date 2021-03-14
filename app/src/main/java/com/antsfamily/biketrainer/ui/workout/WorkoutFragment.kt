package com.antsfamily.biketrainer.ui.workout

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.program.ProgramData
import com.antsfamily.biketrainer.databinding.FragmentWorkoutBinding
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.presentation.workout.WorkoutViewModel
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.ui.util.hideAllLabels
import com.antsfamily.biketrainer.ui.util.setHighlightedMode
import com.antsfamily.biketrainer.util.fullTimeFormat
import com.antsfamily.biketrainer.util.mapDistinct
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkoutFragment : BaseFragment(R.layout.fragment_workout) {

    private val args: WorkoutFragmentArgs by navArgs()

    override val viewModel: WorkoutViewModel by viewModels { withFactory(viewModelFactory) }

    private val chartHighlights = mutableListOf<Highlight>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        viewModel.onCreate(args.devices.toList(), args.program)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentWorkoutBinding.bind(view)) {
            observeState(this)
            observeEvents(this)
            bindInteractions(this)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun bindInteractions(binding: FragmentWorkoutBinding) {
        with(binding) {
            backBtn.setOnClickListener { viewModel.onBackClick() }
            startWorkoutBtn.setOnClickListener { viewModel.onStartClick() }
            pauseWorkoutBtn.setOnClickListener { viewModel.onPauseClick() }
            continueWorkoutBtn.setOnClickListener { viewModel.onContinueClick() }
            stopWorkoutBtn.setOnClickListener { viewModel.onStopClick() }
        }
    }

    private fun observeEvents(binding: FragmentWorkoutBinding) {
        viewModel.showSuccessSnackBarEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        viewModel.showSuccessSnackBarMessageEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        viewModel.resetChartHighlightsEvent.observe(viewLifecycleOwner, EventObserver {
            chartHighlights.clear()
            binding.programChart.highlightValues(arrayOf())
        })
    }

    private fun observeState(binding: FragmentWorkoutBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.isLoading }
                .observe(viewLifecycleOwner) { loadingView.isVisible = it }
            viewModel.state.mapDistinct { it.title }
                .observe(viewLifecycleOwner) { titleTv.text = it }
            viewModel.state.mapDistinct { it.allRounds }.observe(viewLifecycleOwner) {
                workoutAllStepsTv.text = it?.toString() ?: EMPTY_DATA
            }
            viewModel.state.mapDistinct { it.currentRound }.observe(viewLifecycleOwner) {
                workoutCurrentStepTv.text = it.toString()
                highlightAppropriateBar(it)
            }
            viewModel.state.mapDistinct { it.currentStep }.observe(viewLifecycleOwner) { data ->
                workoutTargetPowerTv.text =
                    data?.let { getString(R.string.workout_power, it.power.toString()) }
                        ?: EMPTY_DATA
            }
            viewModel.state.mapDistinct { it.nextStep }
                .observe(viewLifecycleOwner) { setNextStep(it) }
            viewModel.state.mapDistinct { it.startButtonVisible }
                .observe(viewLifecycleOwner) { startWorkoutBtn.isVisible = it }
            viewModel.state.mapDistinct { it.pauseButtonVisible }
                .observe(viewLifecycleOwner) { pauseWorkoutBtn.isVisible = it }
            viewModel.state.mapDistinct { it.stopButtonVisible }
                .observe(viewLifecycleOwner) { stopWorkoutBtn.isVisible = it }
            viewModel.state.mapDistinct { it.continueButtonVisible }
                .observe(viewLifecycleOwner) { continueWorkoutBtn.isVisible = it }
            viewModel.state.mapDistinct { it.progress }
                .observe(viewLifecycleOwner) { stepCountdownRb.progress = it }
            viewModel.state.mapDistinct { it.remainingTime }
                .observe(viewLifecycleOwner) { setRemainingTime(it) }
            viewModel.state.mapDistinct { it.heartRate }.observe(viewLifecycleOwner) {
                workoutHeartRateTv.text =
                    getString(R.string.workout_heart_rate, it?.toString() ?: EMPTY_DATA)
            }
            viewModel.state.mapDistinct { it.cadence }.observe(viewLifecycleOwner) {
                workoutCadenceTv.text =
                    getString(R.string.workout_cadence, it?.toString() ?: EMPTY_DATA)
            }
            viewModel.state.mapDistinct { it.distance }.observe(viewLifecycleOwner) {
                workoutDistanceTv.text =
                    getString(R.string.workout_distance, it?.toString() ?: EMPTY_DATA)
            }
            viewModel.state.mapDistinct { it.speed }.observe(viewLifecycleOwner) {
                workoutSpeedTv.text =
                    getString(R.string.workout_speed, it?.toString() ?: EMPTY_DATA)
            }
            viewModel.state.mapDistinct { it.power }.observe(viewLifecycleOwner) { power ->
                workoutUserPowerTv.text =
                    power?.let { getString(R.string.workout_power, it.toString()) } ?: EMPTY_DATA
            }
            viewModel.state.mapDistinct { it.program }
                .observe(viewLifecycleOwner) { setProgramBarChart(it) }
        }
    }

    private fun FragmentWorkoutBinding.highlightAppropriateBar(columnNumber: Int) {
        val index = columnNumber.dec()
        if (index < 0) return
        chartHighlights.add(Highlight(index.toFloat(), 0, 0))
        programChart.highlightValues(chartHighlights.toTypedArray())
    }

    private fun FragmentWorkoutBinding.setRemainingTime(remainingTime: Long) {
        workoutRemainingTimeTv.text = remainingTime.fullTimeFormat()
    }

    private fun FragmentWorkoutBinding.setNextStep(data: ProgramData?) {
        workoutNextStepValueTv.text = data?.let {
            getString(R.string.workout_next_round_value, it.power, it.duration.fullTimeFormat())
        } ?: EMPTY_DATA
    }

    private fun FragmentWorkoutBinding.setProgramBarChart(data: List<ProgramData>?) {
        val entities = data?.mapIndexed { index, _data ->
            BarEntry(index.toFloat(), _data.power.toFloat())
        }
        entities?.let {
            with(this.programChart) {
                setScaleEnabled(false)
                setTouchEnabled(true)
                hideAllLabels()
                setDrawGridBackground(false)
                setDrawBorders(false)
                this.data = BarData(
                    BarDataSet(it, EMPTY_LABEL).apply {
                        barBorderWidth = BAR_BORDER_WIDTH
                        valueFormatter = object : ValueFormatter() {
                            override fun getBarLabel(entry: BarEntry): String = EMPTY_LABEL
                        }
                        setHighlightedMode(false)
                        setColors(intArrayOf(R.color.color_cooler), requireContext())
                        highLightColor = resources.getColor(R.color.color_central, null)
                        stackLabels = emptyArray()
                    }
                ).apply {
                    barWidth = BAR_WIDTH
                }
                invalidate()
            }
        }
    }

    companion object {
        private const val EMPTY_DATA = "--"
        private const val EMPTY_LABEL = ""
        private const val BAR_BORDER_WIDTH = 0f
        private const val BAR_WIDTH = 0.95f
    }
}
