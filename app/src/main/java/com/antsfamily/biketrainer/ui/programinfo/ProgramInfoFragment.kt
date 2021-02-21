package com.antsfamily.biketrainer.ui.programinfo

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.program.ProgramData
import com.antsfamily.biketrainer.databinding.FragmentProgramInfoBinding
import com.antsfamily.biketrainer.presentation.programinfo.ProgramInfoViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.ui.util.*
import com.antsfamily.biketrainer.ui.util.BarCharsStaticFields.BAR_WIDTH_95
import com.antsfamily.biketrainer.util.mapDistinct
import com.antsfamily.biketrainer.util.timeFormat
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProgramInfoFragment : BaseFragment(R.layout.fragment_program_info) {

    private val args: ProgramInfoFragmentArgs by navArgs()

    @Inject
    lateinit var barChartGestureListener: BarChartGestureListener

    override val viewModel: ProgramInfoViewModel by viewModels { withFactory(viewModelFactory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(args.programName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentProgramInfoBinding.bind(view)) {
            observeState(this)
            bindInteractions(this)
        }
    }

    private fun observeState(binding: FragmentProgramInfoBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.isLoading }
                .observe(viewLifecycleOwner) { loadingView.isVisible = it }
            viewModel.state.mapDistinct { it.program }
                .observe(viewLifecycleOwner) { setChart(it) }
            viewModel.state.mapDistinct { it.programName }
                .observe(viewLifecycleOwner) { programInfoNameTv.text = it }
            viewModel.state.mapDistinct { it.duration }
                .observe(viewLifecycleOwner) { programDurationTv.text = it }
            viewModel.state.mapDistinct { it.maxPower }
                .observe(viewLifecycleOwner) { programMaxPowerTv.text = it }
            viewModel.state.mapDistinct { it.avgPower }
                .observe(viewLifecycleOwner) { programAvgPowerTv.text = it }
        }
    }

    private fun bindInteractions(binding: FragmentProgramInfoBinding) {
        with(binding) {
            backBtn.setOnClickListener { viewModel.onBackClick() }
            editBtn.setOnClickListener { viewModel.onEditClick() }
            runWorkoutBtn.setOnClickListener { viewModel.onRunWorkoutClick() }
            deleteBtn.setOnClickListener { viewModel.onDeleteClick() }
            barChartGestureListener.setBarChart(programChart)
        }
    }

    private fun FragmentProgramInfoBinding.setChart(programData: List<ProgramData>) {
        if (programData.isEmpty()) {
            return
        }
        val entries = programData.mapIndexed { index, _data ->
            BarEntry(index.toFloat(), _data.power.toFloat())
        }
        val labels = programData.map { it.duration.timeFormat() }
        barChartGestureListener.setLabels(labels)
        with(programChart) {
            setDefaultBaseSettings(entries.size)
            data = BarData(
                BarDataSet(entries, "")
                    .setDefaultSettings(this@with.getYAxisValueFormatter(entries.size))
            ).apply { barWidth = BAR_WIDTH_95 }
            xAxis.apply {
                isEnabled = entries.size <= 5
                setDefaultSettings(labels, this@with.getXAxisValueFormatter(labels))
            }
            onChartGestureListener = barChartGestureListener
            invalidate()
        }
    }
}
