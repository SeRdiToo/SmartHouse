package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.smarthousetryagain.DeviceAddActivity
import com.example.smarthousetryagain.MainScreen
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.util.Identity.encode
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception

class DeviceActivity : AppCompatActivity() {
    private lateinit var name:TextView
    var roomid:String=""
    var array: JSONArray? = null
    var viewItems: ArrayList<DataDevice> = ArrayList()
    private val adapter = AdapterDevice(viewItems,this@DeviceActivity,lifecycleScope)
    val client = createSupabaseClient(
        supabaseUrl = "https://fogygiutqidwswxezefb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZvZ3lnaXV0cWlkd3N3eGV6ZWZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzE0MzkyODksImV4cCI6MjA0NzAxNTI4OX0.09u_eu_f2zBE9PGxQUJC6zWgJGOoL_5zSJw-JUWkqqY"
    ){
        install(GoTrue)
        install(Postgrest)
        install(Storage)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        name = findViewById(R.id.name)
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager= GridLayoutManager(this,2)
        lifecycleScope.launch{
            roomid = intent.getStringExtra("roomid").toString()
            try {
                val device = client.postgrest["Room"].select(columns = Columns.list("id", "name")){
                    eq("id",roomid)
                }.body.toString()
                val array = JSONArray(device)
                val obj = array.getJSONObject(0)
                val nameroom = obj.getString("name")
                // Toast.makeText(this, room.name, Toast.LENGTH_SHORT).show()
                name.setText("Устройства в $nameroom")
            }catch (e: Exception){
                Log.e("Message",e.toString())
            }
        }


        lifecycleScope.launch {
            roomid = intent.getStringExtra("roomid").toString()
            try {
                val client = client.postgrest["Device"].select(){
                    eq("room_id",roomid)
                }
                val buf_client = StringBuilder()
                buf_client.append(client.body.toString()).append("\n")
                array = JSONArray(buf_client.toString())
                addItemsFromJSON()
            }catch (e: Exception){
                Log.e("!!!!!!!", e.toString())
            }
        }
        recyclerView.adapter = adapter
    }
    private fun addItemsFromJSON() {
        try {
// Заполняем Модель спаршенными данными
            for (i in 0 until array!!.length()) {
                val itemObj: JSONObject = array!!.getJSONObject(i)
                val id = itemObj.getString("id")
                val name = itemObj.getString("name")
                val devices_type_id = itemObj.getString("device_type_id")
                val room_id = itemObj.getString("room_id")
                val status = itemObj.getString("status")
                val power = itemObj.getString("power")
                val img = "$devices_type_id.png"
                lifecycleScope.launch {
                    try {
                        val bucket = client.storage["Devices"]
                        val bytes = bucket.downloadPublic(img)
                        val is1: InputStream = ByteArrayInputStream(bytes)
                        val bmp: Bitmap = BitmapFactory.decodeStream(is1)
                        val dr = BitmapDrawable(resources,bmp)
                        val devices = DataDevice(id, name,devices_type_id, img,dr,room_id,status,power)
                        adapter.notifyDataSetChanged()
                        viewItems.add(devices)
                    }catch (e:Exception){
                        Log.e("MESSAGE",e.toString())
                    }
                }
            }
        } catch (e: JSONException) {
        }
    }
    fun Back(view: View){
        val intent = Intent(this, MainScreen::class.java)
        startActivity(intent)
    }
    fun Add(view: View){
        roomid = intent.getStringExtra("roomid").toString()
        val intent = Intent(this@DeviceActivity, DeviceAddActivity::class.java)
        intent.putExtra("roomid", roomid)
        startActivity(intent)
    }
}