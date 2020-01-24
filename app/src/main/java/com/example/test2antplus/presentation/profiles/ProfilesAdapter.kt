package com.example.test2antplus.presentation.profiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test2antplus.R
import java.util.*

class ProfilesAdapter(
    private val onEditClick: (id: Int) -> Unit,
    private val onDeleteClick: (position: Int) -> Unit,
    private val onItemClick: (id: Int) -> Unit
) : RecyclerView.Adapter<ProfilesAdapter.ProfileViewHolder>() {

    private var profiles: ArrayList<Pair<String, Int>> = arrayListOf()
    private var deletedPosition: Int = -1

    private lateinit var deletedItem: Pair<String, Int>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_profile_info, parent, false)
        return ProfileViewHolder(view)
    }

    fun removeItem(position: Int) {
        deletedItem = profiles[position]
        deletedPosition = position
        profiles.removeAt(position)
        notifyItemRemoved(position)
        onDeleteClick.invoke(position)
    }

    fun editItem(position: Int) {
        val selectedId = profiles[position].second
        notifyDataSetChanged()
        onEditClick.invoke(selectedId)
    }

    override fun getItemCount(): Int = profiles.size

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(this.profiles[position])
    }

    fun setProfileList(newProfiles: ArrayList<Pair<String, Int>>) {
        profiles.clear()
        profiles.addAll(newProfiles)
        notifyDataSetChanged()
    }


    inner class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val programName = view.findViewById<TextView>(R.id.textProfileName)

        fun bind(profile: Pair<String, Int>) {
            programName.text = profile.first
            programName.setOnClickListener {
                onItemClick.invoke(profile.second)
            }
        }
    }

}