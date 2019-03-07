package com.example.test2antplus.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.test2antplus.Program
import com.example.test2antplus.R

class ProgramAdapter(

): RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {
    private var programs: ArrayList<Program> = arrayListOf()
    private lateinit var selectedProgram: Program
    private lateinit var programsDiffUtil: ProgramCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramAdapter.ProgramViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_program_info, parent, false)
        return ProgramViewHolder(view)
    }

    fun addDevice(newProgram: Program) {
        val oldPrograms = this.getData()
        if (!programs.contains(newProgram)) {
            programs.add(newProgram)
        }
        programsDiffUtil = ProgramCallback(oldPrograms, programs)
        val productDiffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(programsDiffUtil, false)
        productDiffResult.dispatchUpdatesTo(this)
        this.notifyItemInserted(programs.size)
    }

    fun getSelectedPrograms(): Program = selectedProgram

    fun getAllData() = programs

    private fun getData(): ArrayList<Program> = programs

    override fun getItemCount(): Int = programs.size

    override fun onBindViewHolder(holder: ProgramAdapter.ProgramViewHolder, position: Int) {
        holder.bind(this.programs[position], position)
    }


    inner class ProgramViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(program: Program, position: Int) {

        }
    }


}