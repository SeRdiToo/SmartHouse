package com.example.smarthousetryagain

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

class DeviceAddActivity : AppCompatActivity() {
    var array: JSONArray? = null
    var viewItems: ArrayList<DataDeviceType> = ArrayList()
    private lateinit var nameEdit: EditText
    private lateinit var identifierEdit: EditText
    var devicetypeid:String=""
    var roomid:String=""
    private val adapter = AdapterDeviceType(viewItems, this@DeviceAddActivity,object:AdapterDeviceType.ItemClickListener
    {
        override fun OnItemClick(typeId:String){
            devicetypeid= typeId
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
        setContentView(R.layout.activity_device_add)

        nameEdit=findViewById(R.id.nameEdit)
        identifierEdit=findViewById(R.id.identifierEdit)
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager= GridLayoutManager(this,3)
        try {
            lifecycleScope.launch {
                val client = client.postgrest["Device_type"].select()
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

    fun Back(view: View){
        roomid = intent.getStringExtra("roomid").toString()
        val intent = Intent(this@DeviceAddActivity, DeviceActivity::class.java)
        intent.putExtra("roomid", roomid)
        startActivity(intent)
    }

    fun Save(view: View){
        if(nameEdit.text.toString()!="" && identifierEdit.text.toString()!=""){
            lifecycleScope.launch{
                roomid = intent.getStringExtra("roomid").toString()
                try {
                    val deviceadd = DataDeviceAdd(name = nameEdit.text.toString(), identifier = identifierEdit.text.toString(), power = "0", power1 = "0", status = "false", device_type_id = devicetypeid, room_id = roomid)
                    client.postgrest["Device"].insert(deviceadd)
                    val intent = Intent(this@DeviceAddActivity, DeviceActivity::class.java)
                    intent.putExtra("roomid", roomid)
                    startActivity(intent)
                }catch (e: Exception){
                    Log.e("Message",e.toString())
                }
            }
        }else Toast.makeText(this, "Ошибка, не все поля заполнены!", Toast.LENGTH_SHORT).show()
    }
    private fun addItemsFromJSON() {
        try {
            for (i in 0 until array!!.length()) {
                val itemObj: JSONObject = array!!.getJSONObject(i)
                val id = itemObj.getString("id")
                val avatar = itemObj.getString("avatar")
                val name = itemObj.getString("name")
                lifecycleScope.launch {
                    try {
                        val bucket = client.storage["Devices_type"]
                        val bytes = bucket.downloadPublic(avatar)
                        val is1: InputStream = ByteArrayInputStream(bytes)
                        val bmp: Bitmap = BitmapFactory.decodeStream(is1)
                        val dr = BitmapDrawable(resources,bmp)
                        val device = DataDeviceType(id, avatar,dr,name)
                        adapter.notifyDataSetChanged()
                        viewItems.add(device)
                    }catch (e:Exception){
                        Log.e("MESSAGE",e.toString())
                    }
                }
            }
        } catch (e: JSONException) {
        }
    }
}