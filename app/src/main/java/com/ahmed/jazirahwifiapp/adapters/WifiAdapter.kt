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
import com.ahmed.jazirahwifiapp.model.Wifi
import kotlinx.android.synthetic.main.wifi_item_view.view.*


class WifiAdapter(val isFromAdminView: Boolean) :
    RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {
    inner class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Wifi>() {
        override fun areItemsTheSame(oldItem: Wifi, newItem: Wifi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Wifi, newItem: Wifi): Boolean {
            return oldItem.id == newItem.id
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        return WifiViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.wifi_item_view, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        val wifi = differ.currentList[position]
        val connectionTypes =
            holder.itemView.context.resources.getStringArray(R.array.wifi_connection_types)
        val securityTypes =
            holder.itemView.context.resources.getStringArray(R.array.wifi_security_modes)

        holder.itemView.apply {
            findViewById<TextView>(R.id.tvWifiName).text = wifi.wifiName
            findViewById<TextView>(R.id.tv_device_location).text = wifi.deviceLocation
            val sbConnectionType = StringBuilder()
            sbConnectionType.append("Connection Type : ")
            sbConnectionType.append(connectionTypes[wifi.connectionType])
            val sbSecurityType = StringBuilder()
            sbSecurityType.append("Security Type : ")
            sbSecurityType.append(securityTypes[wifi.securityType])
            findViewById<TextView>(R.id.tv_security_type).text = sbSecurityType
            findViewById<TextView>(R.id.tv_connection_type).text = sbConnectionType
            findViewById<RelativeLayout>(R.id.rel_ip_address).apply {
                if (wifi.connectionType == 0) {
                    this.visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tv_ip_address).text = wifi.ipAddress
                } else this.visibility = View.GONE
            }
            if (isFromAdminView) {
                cbToggle.visibility = View.VISIBLE
                cbToggle.setOnCheckedChangeListener { compoundButton, b ->
                    if (compoundButton.isChecked)
                        onSelectItemCheckedListener?.let { it(wifi, position) }
                    else
                        onSelectItemUnCheckedListener?.let { it(wifi, position) }
                }
                cbToggle.isChecked = wifi.isSelected
            }

            findViewById<ImageView>(R.id.iv_broadcast).apply {
                if (wifi.visibility) this.visibility = View.VISIBLE
                else this.visibility = View.GONE
            }
            findViewById<ImageView>(R.id.iv_delete).setOnClickListener {
                onDeleteItemListener?.let { it(wifi, position) }
            }
            findViewById<ImageView>(R.id.iv_share).setOnClickListener {
                onShareItemListener?.let { it(wifi) }
            }
            findViewById<ImageView>(R.id.iv_edit).setOnClickListener {
                onEditItemListener?.let { it(wifi, position) }
            }
            lyContents.setOnClickListener {
                onSelectItemListener?.let { it(wifi, position) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateChanges(map:MutableMap<Int,Wifi>){
        for ((k, v) in map) {
            notifyItemChanged(k)
        }
    }
    fun updateItemSelectedAll(list: MutableList<Wifi>){
        differ.submitList(list)
        notifyDataSetChanged()
    }

    private var onSelectItemListener: ((Wifi, Int) -> Unit)? = null
    private var onDeleteItemListener: ((Wifi, Int) -> Unit)? = null
    private var onEditItemListener: ((Wifi, Int) -> Unit)? = null
    private var onShareItemListener: ((Wifi) -> Unit)? = null
    private var onSelectItemCheckedListener: ((Wifi, Int) -> Unit)? = null
    private var onSelectItemUnCheckedListener: ((Wifi, Int) -> Unit)? = null
    fun setOnItemDeleteListener(listener: (Wifi, Int) -> Unit) {
        onDeleteItemListener = listener
    }

    fun setOnItemShareListener(listener: (Wifi) -> Unit) {
        onShareItemListener = listener
    }

    fun setOnItemClickListener(listener: (Wifi, Int) -> Unit) {
        onSelectItemListener = listener
    }

    fun setOnItemEditListener(listener: (Wifi, Int) -> Unit) {
        onEditItemListener = listener
    }

    fun setOnCheckBoxChecked(listener: (Wifi, Int) -> Unit) {
        onSelectItemCheckedListener = listener
    }

    fun setOnCheckBoxUnChecked(listener: (Wifi, Int) -> Unit) {
        onSelectItemUnCheckedListener = listener
    }

}