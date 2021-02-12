package com.antsfamily.biketrainer.ui.programs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.data.models.program.ProgramData
import com.antsfamily.biketrainer.databinding.CardProgramInfoBinding
import com.antsfamily.biketrainer.util.fullTimeFormat
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
            val programSource = item.getData()
            with(binding) {
                programNameTv.text = item.getName()
                programDurationTv.text = getTotalTime(programSource)
                programMaxPowerTv.text = getMaxPower(programSource)
                programAvgPowerTv.text = getAveragePower(programSource)

//                Picasso.get()
//                    .load(File(item.getImagePath()))
//                    .fit()
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                    .into(programIv)

                root.setOnClickListener { onItemClickListener?.invoke(item) }
                root.setOnLongClickListener {
                    onLongItemClickListener?.invoke(item)
                    true
                }
            }
        }

        private fun getAveragePower(data: List<ProgramData>): String {
            val avgPower = data.map { it.power }.sum().div(data.size)
            return "$avgPower W avg"
        }

        private fun getMaxPower(data: List<ProgramData>): String {
            val maxPower = data.maxOf { it.power }
            return "$maxPower W max"
        }

        private fun getTotalTime(data: List<ProgramData>): String =
            data.map { it.duration }.sum().fullTimeFormat()
    }

}
