package com.example.smarthousetryagain

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.example.smarthousetryagain.DataRoomsType

class AdapterRoomsType(private val typesList: ArrayList<DataRoomsTypeWithImages>, private val context: Context, private val itemClickListener: ItemClickListener
):RecyclerView.Adapter<AdapterRoomsType.MyViewHolder>()
{
    private var pos:Int = 0
    interface ItemClickListener{
        fun OnItemClick(typeId: Int){
        }
    }
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var id: TextView = itemView.findViewById(R.id.id)
        var name: TextView = itemView.findViewById(R.id.name)
        var avatar: TextView = itemView.findViewById(R.id.avatar)
        var image: ImageView = itemView.findViewById(R.id.photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rooms_type_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val on = ContextCompat.getColor(context,R.color.on)
        val off = ContextCompat.getColor(context,R.color.off)
        val types = typesList[position]
        holder.id.text = types.room_id.toString()
        holder.name.text = types.type.toString()
        holder.avatar.text = types.avatar.toString()
        holder.image.setImageDrawable(types.avatar)
        if(position==pos){
            holder.name.setTextColor(on)
            holder.image.setColorFilter(on)
        }
        else{
            holder.name.setTextColor(off)
            holder.image.setColorFilter(off)
        }
        holder.itemView.setOnClickListener {
            itemClickListener.OnItemClick(types.room_id)
            pos = position
            notifyDataSetChanged()
        }

    }
    override fun getItemCount(): Int {
        return typesList.size
    }
}

private fun View.setImageDrawable(image: Image) {
    TODO("Not yet implemented")
}
