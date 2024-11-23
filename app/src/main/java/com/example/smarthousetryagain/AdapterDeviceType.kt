package com.example.smarthousetryagain

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class AdapterDeviceType(
    private val typesList: ArrayList<DataDeviceType>,
    private val context: Context,
    private val itemClickListener: AdapterDeviceType.ItemClickListener
) : RecyclerView.Adapter<AdapterDeviceType.MyViewHolder>() {
    private var pos: Int = 0

    interface ItemClickListener {
        fun OnItemClick(typeId: String) {
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id: TextView = itemView.findViewById(R.id.id)
        var name: TextView = itemView.findViewById(R.id.name)
        var avatar: TextView = itemView.findViewById(R.id.avatar)
        var image: ImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.device_type_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val on = ContextCompat.getColor(context, R.color.on)
        val off = ContextCompat.getColor(context, R.color.off)
        val types = typesList[position]
        holder.id.text = types.id.toString()
        holder.name.text = types.name.toString()
        holder.avatar.text = types.avatar.toString()
        holder.image.setImageDrawable(types.image)
        if (position == pos) {
            holder.name.setTextColor(on)
            holder.image.setColorFilter(on)
        } else {
            holder.name.setTextColor(off)
            holder.image.setColorFilter(off)
        }
        holder.itemView.setOnClickListener {
            itemClickListener.OnItemClick(types.id)
            pos = position
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return typesList.size
    }
}