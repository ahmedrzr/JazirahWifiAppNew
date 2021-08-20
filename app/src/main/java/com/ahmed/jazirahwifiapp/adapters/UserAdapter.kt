package com.ahmed.jazirahwifiapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.model.Wifi
import kotlinx.android.synthetic.main.user_item_view.view.*


class UserAdapter : RecyclerView.Adapter<UserAdapter.WifiViewHolder>() {
    inner class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Permission>() {
        override fun areItemsTheSame(oldItem: Permission, newItem: Permission): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Permission, newItem: Permission): Boolean {
            return oldItem.id == newItem.id
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {

        return WifiViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.user_item_view, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.itemView.apply {
            ivEdit.setOnClickListener {
                onItemSelectLister?.let { it1 -> it1(user) }
            }
            tvDisplayName.text = user?.displayName
            tvEmailAddress.text = user?.email
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemSelectLister: ((permission: Permission) -> Unit)? = null
    fun setOnItemSelectListener(listener: (permission: Permission) -> Unit) {
        onItemSelectLister = listener
    }

}