package com.example.smarthousetryagain

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.smarthousetryagain.MainScreen
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

class RoomActivity : AppCompatActivity() {
    var array: JSONArray? = null
    var viewItems: ArrayList<DataRoomsType> = ArrayList()
    private lateinit var nameEdit:EditText
    var roomtypeid:String=""
    private val adapter = AdapterRoomsType(viewItems, this@RoomActivity,object:AdapterRoomsType.ItemClickListener
    {
        override fun OnItemClick(typeId:String){
            roomtypeid= typeId
        }
    })
    val client = createSupabaseClient(
        supabaseUrl = "https://fogygiutqidwswxezefb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZvZ3lnaXV0cWlkd3N3eGV6ZWZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzE0MzkyODksImV4cCI6MjA0NzAxNTI4OX0.09u_eu_f2zBE9PGxQUJC6zWgJGOoL_5zSJw-JUWkqqY"
    ) {
        install(GoTrue)
        install(Postgrest)
        install(Storage)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        nameEdit=findViewById(R.id.nameEdit)
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager= GridLayoutManager(this,3)
        try {
            lifecycleScope.launch {
                val client = client.postgrest["Room_type"].select()
                val buf_client = StringBuilder()
                buf_client.append(client.body.toString()).append("\n")
                array = JSONArray(buf_client.toString())
                addItemsFromJSON()
            }
        }
        catch (e: Exception){
            Log.e("!!!!!!!", e.toString())
        }
        recyclerView.adapter = adapter
    }

    private fun addItemsFromJSON() {
        try {
            for (i in 0 until array!!.length()) {
                val itemObj: JSONObject = array!!.getJSONObject(i)
                val id = itemObj.getString("id")
                val name = itemObj.getString("name")
                val avatar = itemObj.getString("avatar")
                lifecycleScope.launch {
                    try {
                        val bucket = client.storage["Rooms_type"]
                        val bytes = bucket.downloadPublic(avatar)
                        val is1:InputStream = ByteArrayInputStream(bytes)
                        val bmp: Bitmap = BitmapFactory.decodeStream(is1)
                        val dr = BitmapDrawable(resources,bmp)
                        val room = DataRoomsType(id, name,avatar,dr)
                        adapter.notifyDataSetChanged()
                        viewItems.add(room)
                    }catch (e:Exception){
                        Log.e("MESSAGE",e.toString())
                    }
                }
            }
        } catch (e: JSONException) {
        }
    }

    fun Save(view: View){
        if(nameEdit.text.toString()!=""){
            lifecycleScope.launch{
                try {
                    val user = client.gotrue.retrieveUserForCurrentSession(updateSession = true)
                    val roomadd = DataRoomsAdd(user_id=user.id, name = nameEdit.text.toString(), rooms_type_id = roomtypeid)
                    client.postgrest["Room"].insert(roomadd)
                    val intent = Intent(this@RoomActivity, MainScreen::class.java)
                    startActivity(intent)
                }catch (e: Exception){
                    Log.e("Message",e.toString())
                }
            }
        }else Toast.makeText(this, "Введите имя комнаты!", Toast.LENGTH_SHORT).show()
    }

    fun Back (view: View){
        val intent = Intent(this@RoomActivity, MainScreen::class.java)
        startActivity(intent)
    }
}