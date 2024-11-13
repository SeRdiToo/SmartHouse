package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.smarthousetryagain.ProfileActivity
import com.example.smarthousetryagain.RoomActivity
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception

class MainScreen : AppCompatActivity() {
    var array: JSONArray? = null
    var viewItems: ArrayList<DataRooms> = ArrayList()
    private val adapter = AdapterRooms(viewItems,this@MainScreen)

    private lateinit var addressText: TextView

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
        setContentView(R.layout.activity_main_screen)
        addressText = findViewById(R.id.addressText)
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager= LinearLayoutManager(this)


        lifecycleScope.launch{
            val userlg = client.gotrue.retrieveUserForCurrentSession(updateSession = true)
            try {
                val user = client.postgrest["User"].select() {
                    eq("id",userlg.id)
                }.decodeSingle<Users>()
                addressText.setText(user.address)
            }catch (e: Exception){
                Log.e("Message",e.toString())
            }
        }

        lifecycleScope.launch {
            val userlg = client.gotrue.retrieveUserForCurrentSession(updateSession = true)
            try {
                val client = client.postgrest["Room"].select(){
                    eq("user_id",userlg.id)
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
    fun Profile(view: View){
        val intent = Intent(this@MainScreen, ProfileActivity::class.java)
        startActivity(intent)
    }
    fun Add(view: View){
        val intent = Intent(this@MainScreen, RoomActivity::class.java)
        startActivity(intent)
    }
    private fun addItemsFromJSON() {
        try {
// Заполняем Модель спаршенными данными
            for (i in 0 until array!!.length()) {
                val itemObj: JSONObject = array!!.getJSONObject(i)
                val id = itemObj.getString("id")
                val name = itemObj.getString("name")
                val rooms_type_id = itemObj.getString("rooms_type_id")
                val img = "$rooms_type_id.png"
                lifecycleScope.launch {
                    try {
                        val bucket = client.storage["Rooms"]
                        val bytes = bucket.downloadPublic(img)
                        val is1: InputStream = ByteArrayInputStream(bytes)
                        val bmp: Bitmap = BitmapFactory.decodeStream(is1)
                        val dr = BitmapDrawable(resources,bmp)
                        val rooms = DataRooms(id,rooms_type_id, name,dr)
                        adapter.notifyDataSetChanged()
                        viewItems.add(rooms)
                    }catch (e:Exception){
                        Log.e("MESSAGE",e.toString())
                    }
                }
            }
        } catch (e: JSONException) {
        }
    }
}