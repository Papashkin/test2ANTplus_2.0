package com.antsfamily.biketrainer.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsfamily.biketrainer.databinding.CardProgramAddNewBinding
import javax.inject.Inject

class CreateProgramAdapter @Inject constructor() :
    RecyclerView.Adapter<CreateProgramAdapter.CreateProgramViewHolder>() {

    private var onCreateProgramClickListener: (() -> Unit)? = null

    fun setOnCreateProgramClickListener(listener: () -> Unit) {
        onCreateProgramClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateProgramViewHolder =
        CreateProgramViewHolder(
            CardProgramAddNewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CreateProgramViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemViewType(position: Int): Int = 0

    override fun getItemCount(): Int = 1

    inner class CreateProgramViewHolder(private val binding: CardProgramAddNewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.createProgramBtn.setOnClickListener { onCreateProgramClickListener?.invoke() }
        }
    }
}
