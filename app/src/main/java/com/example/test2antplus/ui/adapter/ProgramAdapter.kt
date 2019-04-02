package com.example.test2antplus.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test2antplus.R
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.fullTimeFormat
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

class ProgramAdapter(
    private val onEditClick: (id: Int)  -> Unit,
    private val onDeleteClick: (id: Int) -> Unit,
    private val onItemClick: (id: Int) -> Unit
) : RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {
    private var programs: ArrayList<Program> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramAdapter.ProgramViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_program_info, parent, false)
        return ProgramViewHolder(view)
    }

    fun removeItem(id: Int) {
        val position = programs.indexOf(programs.first { it.getId() == id })
        programs.removeAt(position)
        notifyItemRemoved(position)
    }

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
        private val programImage = view.findViewById<ImageView>(R.id.imageProgram)
        private val maxPower = view.findViewById<TextView>(R.id.textMaxPower)
        private val btnDelete = view.findViewById<ImageView>(R.id.btnDeleteProgram)
        private val btnEdit = view.findViewById<ImageView>(R.id.btnEditProgram)
        private val programLayout = view.findViewById<View>(R.id.clProgram)

        fun bind(program: Program) {

            val programSource = program.getProgram()
            programName.text = program.getName()
            avgPower.text = getAveragePower(programSource)
            maxPower.text = getMaxPower(programSource)
            duration.text = getTotalTime(programSource)

            Picasso.get()
                .load(File(program.getImagePath()))
                .fit()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(programImage)

            btnDelete.setOnClickListener {
                onDeleteClick.invoke(program.getId())
            }

            btnEdit.setOnClickListener {
                onEditClick.invoke(program.getId())
            }

            programLayout.setOnClickListener {
                onItemClick.invoke(program.getId())
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


        private fun getTotalTime(program: String): CharSequence {
            var count = 0.0f
            program.split("|").forEach {
                if (it.isNotEmpty()) {
                    count += it.split("*").first().toFloat()
                }
            }
            return "Total time: ${count.toLong().fullTimeFormat()}"
        }
    }

}