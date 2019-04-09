package com.example.test2antplus.ui.adapter.program

import androidx.recyclerview.widget.DiffUtil
import com.example.test2antplus.data.programs.Program

class ProgramCallback(
    private val oldList: ArrayList<Program>,
    private val newList: ArrayList<Program>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldListItemId: Int, newListItemId: Int): Boolean {
        val oldProgram = oldList[oldListItemId]
        val newProgram = newList[newListItemId]
        return oldProgram == newProgram
    }

    override fun areContentsTheSame(oldListItemId: Int, newListItemId: Int): Boolean {
        val oldProgram = oldList[oldListItemId]
        val newProgram = newList[newListItemId]
        return (oldProgram.getName() == newProgram.getName() && oldProgram.getProgram() == newProgram.getProgram())
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size
}