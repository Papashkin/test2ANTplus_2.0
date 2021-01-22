package com.antsfamily.biketrainer.ui.profiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.Profile
import kotlinx.android.synthetic.main.card_profile_info.view.*
import java.util.*

class ProfilesAdapter(
    private val onEditClick: (id: Int) -> Unit,
    private val onDeleteClick: (position: Int) -> Unit,
    private val onItemClick: (id: Int) -> Unit
) : RecyclerView.Adapter<ProfilesAdapter.ProfileViewHolder>() {

    private var profiles: ArrayList<Profile> = arrayListOf()
    private var deletedPosition: Int = -1

    private lateinit var deletedItem: Profile

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
        val selectedId = profiles[position].getId()
        notifyDataSetChanged()
        onEditClick.invoke(selectedId)
    }

    override fun getItemCount(): Int = profiles.size

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(this.profiles[position])
    }

    fun setProfileList(newProfiles: List<Profile>) {
        profiles.clear()
        profiles.addAll(newProfiles)
        notifyDataSetChanged()
    }

    inner class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(profile: Profile) {
            itemView.textProfileName.text = profile.getName()
            itemView.setOnClickListener {
                onItemClick.invoke(profile.getId())
            }
        }
    }

}
