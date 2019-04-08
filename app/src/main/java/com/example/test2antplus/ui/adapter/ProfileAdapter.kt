package com.example.test2antplus.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test2antplus.R
import java.util.*

class ProfileAdapter(
    private val onEditClick: (id: Int) -> Unit,
    private val onDeleteClick: (position: Int) -> Unit,
    private val onItemClick: (id: Int) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    private var profiles: ArrayList<Pair<String, Int>> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_profile_info, parent, false)
        return ProfileViewHolder(view)
    }
//
//    fun removeItem(id: Int) {
//        val position = profiles.indexOf(profiles.first { it.second == id })
//        profiles.removeAt(position)
//        notifyItemRemoved(position)
//    }

    fun removeItem(position: Int) {
        profiles.removeAt(position)
        notifyItemRemoved(position)
        onDeleteClick.invoke(position)
    }

    override fun getItemCount(): Int = profiles.size

    override fun onBindViewHolder(holder: ProfileAdapter.ProfileViewHolder, position: Int) {
        holder.bind(this.profiles[position])
    }

    fun setProfileList(newProfiles: ArrayList<Pair<String, Int>>) {
        profiles.clear()
        profiles.addAll(newProfiles)
        notifyDataSetChanged()
    }


    inner class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val programName = view.findViewById<TextView>(R.id.textProfileName)
//        private val btnEdit = view.findViewById<ImageView>(R.id.btnEditProfile)
//        private val btnDelete = view.findViewById<ImageView>(R.id.btnDeleteProfile)

        fun bind(profile: Pair<String, Int>) {

            programName.text = profile.first

//            btnDelete.setOnClickListener {
//                onDeleteClick.invoke(profile.second)
//            }

//            btnEdit.setOnClickListener {
//                onEditClick.invoke(profile.second)
//            }

            programName.setOnClickListener {
                onItemClick.invoke(profile.second)
            }
        }
    }

}