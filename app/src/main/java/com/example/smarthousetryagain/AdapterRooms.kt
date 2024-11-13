package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class AdapterRooms(private val roomsList: ArrayList<DataRooms>,private val context: Context
): RecyclerView.Adapter<AdapterRooms.MyViewHolder>()
{
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var id:TextView=itemView.findViewById(R.id.id)
        var rooms_type_id:TextView=itemView.findViewById(R.id.rooms_type_id)
        var name: TextView = itemView.findViewById(R.id.name)
        var image: ImageView = itemView.findViewById(R.id.image)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rooms_list, parent, false)
        return MyViewHolder(v)
    }
    override fun onBindViewHolder(holder: AdapterRooms.MyViewHolder, position: Int) {
        val rooms = roomsList[position]
        holder.id.text=rooms.id
        holder.rooms_type_id.text=rooms.rooms_type_id
        holder.name.text = rooms.name
        holder.image.setImageDrawable(rooms.image)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DeviceActivity::class.java).apply {
                putExtra("roomid", rooms.id)
            }
            context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int {
        return roomsList.size
    }
}