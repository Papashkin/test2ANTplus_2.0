package com.antsfamily.biketrainer.ui.programs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsfamily.biketrainer.data.models.Program
import com.antsfamily.biketrainer.databinding.CardProgramInfoBinding
import com.antsfamily.biketrainer.util.fullTimeFormat
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.File
import javax.inject.Inject

class ProgramsAdapter @Inject constructor() :
    RecyclerView.Adapter<ProgramsAdapter.ProgramViewHolder>() {

    var items: List<Program> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onItemClickListener: ((item: Program) -> Unit)? = null
    private var onLongItemClickListener: ((item: Program) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val binding =
            CardProgramInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProgramViewHolder(binding)
    }

    fun setOnItemClickListener(listener: (item: Program) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnLongItemClickListener(listener: (item: Program) -> Unit) {
        onLongItemClickListener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ProgramViewHolder(private val binding: CardProgramInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Program) {
            val programSource = item.getProgram()
            with(binding) {
                programNameTv.text = item.getName()
                programDurationTv.text = getTotalTime(programSource)
                programMaxPowerTv.text = getMaxPower(programSource)
                programAvgPowerTv.text = getAveragePower(programSource)

                Picasso.get()
                    .load(File(item.getImagePath()))
                    .fit()
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(programIv)

                root.setOnClickListener { onItemClickListener?.invoke(item) }
                root.setOnLongClickListener {
                    onLongItemClickListener?.invoke(item)
                    true
                }
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
            return "${avgPower/count} W avg"
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
            return "$maxPower W max"
        }

        private fun getTotalTime(program: String): CharSequence {
            var count = 0.0f
            program.split("|").forEach {
                if (it.isNotEmpty()) {
                    count += it.split("*").first().toFloat()
                }
            }
            return count.toLong().fullTimeFormat()
        }
    }

}
