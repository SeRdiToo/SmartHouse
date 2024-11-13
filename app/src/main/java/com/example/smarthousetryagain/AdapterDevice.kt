package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.lang.Exception
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

val client = createSupabaseClient(
    supabaseUrl = "https://fogygiutqidwswxezefb.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZvZ3lnaXV0cWlkd3N3eGV6ZWZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzE0MzkyODksImV4cCI6MjA0NzAxNTI4OX0.09u_eu_f2zBE9PGxQUJC6zWgJGOoL_5zSJw-JUWkqqY"
){
    install(GoTrue)
    install(Postgrest)
    install(Storage)
}

class AdapterDevice(private val devicesList: ArrayList<DataDevice>,private val context: Context,private val lifecycleCoroutineScope: LifecycleCoroutineScope
): RecyclerView.Adapter<AdapterDevice.MyViewHolder>() {
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var id: TextView =itemView.findViewById(R.id.id)
        var device_type_id: TextView =itemView.findViewById(R.id.device_type_id)
        var name: TextView = itemView.findViewById(R.id.name)
        var image: ImageView = itemView.findViewById(R.id.image)
        var status: SwitchCompat = itemView.findViewById(R.id.status)
        var power:TextView = itemView.findViewById(R.id.power)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDevice.MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.device_list, parent, false)
        return MyViewHolder(v)
    }
    override fun onBindViewHolder(holder: AdapterDevice.MyViewHolder, position: Int) {
        val devices = devicesList[position]
        holder.id.text=devices.id
        holder.device_type_id.text=devices.device_type_id
        holder.name.text = devices.name.toString()
        holder.image.setImageDrawable(devices.image)
        holder.status.isChecked= devices.status.toBoolean()
        if(devices.device_type_id=="1"){
            holder.power.visibility = View.VISIBLE
            holder.power.setText("${devices.power}% яркость")
        }
        else holder.power.visibility = View.INVISIBLE

        holder.status.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                lifecycleCoroutineScope.launch {
                    try {
                        client.postgrest["Device"].update(
                            {
                                set("status", "true")
                            }
                        ) {
                            eq("id", devices.id)
                        }
                    }catch (e:Exception){
                        Log.e("Message",e.toString())
                    }
                }
            }
            else{
                lifecycleCoroutineScope.launch {
                    try {
                        client.postgrest["Device"].update(
                            {
                                set("status", "false")
                            }
                        ) {
                            eq("id", devices.id)
                        }
                    }catch (e:Exception){
                        Log.e("Message",e.toString())
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DeviceEditActivity::class.java).apply {
                putExtra("deviceid", devices.id)
                putExtra("roomid", devices.room_id)
            }
            context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int {
        return devicesList.size
    }

}