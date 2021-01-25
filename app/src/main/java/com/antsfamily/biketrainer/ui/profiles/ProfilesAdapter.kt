package com.antsfamily.biketrainer.ui.profiles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsfamily.biketrainer.data.models.Profile
import com.antsfamily.biketrainer.databinding.CardProfileInfoBinding
import java.util.*
import javax.inject.Inject

class ProfilesAdapter @Inject constructor() :
    RecyclerView.Adapter<ProfilesAdapter.ProfileViewHolder>() {

    private var profiles: ArrayList<Profile> = arrayListOf()
    private var deletedPosition: Int = -1

    private var onEditClickListener: ((id: Int) -> Unit)? = null
    private var onDeleteClickListener: ((position: Int) -> Unit)? = null
    private var onItemClickListener: ((id: Int) -> Unit)? = null

    private lateinit var deletedItem: Profile

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder =
        ProfileViewHolder(
            CardProfileInfoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = profiles.size

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(this.profiles[position])
    }

    fun setOnEditClickListener(listener: ((id: Int) -> Unit)) {
        onEditClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (position: Int) -> Unit) {
        onDeleteClickListener = listener
    }

    fun setOnItemClickListener(listener: ((id: Int) -> Unit)) {
        onItemClickListener = listener
    }

    fun removeItem(position: Int) {
        deletedItem = profiles[position]
        deletedPosition = position
        profiles.removeAt(position)
        notifyItemRemoved(position)
        onDeleteClickListener?.invoke(position)
    }

    fun editItem(position: Int) {
        val selectedId = profiles[position].getId()
        notifyDataSetChanged()
        onEditClickListener?.invoke(selectedId)
    }

    fun setProfileList(newProfiles: List<Profile>) {
        profiles.clear()
        profiles.addAll(newProfiles)
        notifyDataSetChanged()
    }

    inner class ProfileViewHolder(private val binding: CardProfileInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: Profile) {
            with(binding) {
                nameTv.text = profile.getName()
                this.root.setOnClickListener { onItemClickListener?.invoke(profile.getId()) }
            }
        }
    }
}
