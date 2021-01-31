package com.antsfamily.biketrainer.ui.createprogram.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.databinding.ViewDurationBinding
import com.antsfamily.biketrainer.ui.util.getStyledAttributes

class DurationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewDurationBinding =
        ViewDurationBinding.inflate(LayoutInflater.from(context), this, true)

    private var onDurationChangeListener: (() -> Unit)? = null

    var error: String? = null
        set(value) {
            field = value
            setupErrorView(value)
        }

    init {
        context.getStyledAttributes(attrs, R.styleable.DurationView) {
            binding.titleTv.text = getString(R.styleable.DurationView_title)
        }
        setDurationBasedValues()
        setupListeners()
    }

    fun setOnDurationChangeListener(listener: () -> Unit) {
        onDurationChangeListener = listener
    }

    fun getValue(): Long = with(binding) {
        secondsNp.value +
                minutesNp.value.times(MINUTES_TO_SECONDS_MULTIPLIER) +
                hoursNp.value.times(HOURS_TO_SECONDS_MULTIPLIER)
    }

    private fun setupListeners() {
        with(binding) {
            hoursNp.setOnValueChangedListener { _, _, _ -> onDurationChangeListener?.invoke() }
            minutesNp.setOnValueChangedListener { _, _, _ -> onDurationChangeListener?.invoke() }
            secondsNp.setOnValueChangedListener { _, _, _ -> onDurationChangeListener?.invoke() }
        }
    }

    private fun setDurationBasedValues() {
        with(binding.hoursNp) {
            minValue = ZERO
            maxValue = MAX_HOURS
        }
        with(binding.minutesNp) {
            minValue = ZERO
            maxValue = MAX_MINUTES
        }
        with(binding.secondsNp) {
            minValue = ZERO
            maxValue = MAX_SECONDS
        }
    }

    private fun setupErrorView(error: String?) {
        with(binding.errorTv) {
            isVisible = !error.isNullOrBlank()
            text = error
        }
    }

    companion object {
        private const val MINUTES_TO_SECONDS_MULTIPLIER = 60L
        private const val HOURS_TO_SECONDS_MULTIPLIER = 3600L

        private const val ZERO = 0
        private const val MAX_HOURS = 12
        private const val MAX_MINUTES = 59
        private const val MAX_SECONDS = 59
    }
}
