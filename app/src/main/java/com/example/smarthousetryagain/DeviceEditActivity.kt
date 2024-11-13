package com.example.smarthousetryagain

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception

class DeviceEditActivity : AppCompatActivity() {
    private lateinit var name: TextView
    private lateinit var status: SwitchCompat
    private lateinit var type: TextView
    private lateinit var powername: TextView
    private lateinit var power:SeekBar
    private lateinit var powername1: TextView
    private lateinit var power1:SeekBar
    private lateinit var image:ImageView
    private lateinit var underseek: TextView
    private lateinit var fieldEdit: LinearLayout
    var deviceid:String=""
    var roomid:String=""
    var powertext:String=""
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
        setContentView(R.layout.activity_device_edit)
        name = findViewById(R.id.name)
        status = findViewById(R.id.status)
        type = findViewById(R.id.type)
        powername = findViewById(R.id.powername)
        power = findViewById(R.id.power)
        powername1 = findViewById(R.id.powername1)
        power1 = findViewById(R.id.power1)
        image = findViewById(R.id.image)
        underseek = findViewById(R.id.underseek)
        fieldEdit = findViewById(R.id.fieldEdit)
        lifecycleScope.launch{
            deviceid = intent.getStringExtra("deviceid").toString()
            try {
                val device = client.postgrest["Device"].select(columns = Columns.list("id", "name","device_type_id","power","status","power1")){
                    eq("id",deviceid)
                }.body.toString()
                val array = JSONArray(device)
                val obj = array.getJSONObject(0)
                val namedevice = obj.getString("name")
                val devices_type_id = obj.getString("device_type_id")
                val powervalue = obj.getString("power")
                val powervalue1 = obj.getString("power1")
                val statusValue = obj.getString("status")

                val devicetype = client.postgrest["Device_type"].select(columns = Columns.list("id", "name")){
                    eq("id",devices_type_id)
                }.body.toString()

                val arraytype = JSONArray(devicetype)
                val objtype = arraytype.getJSONObject(0)
                val namedevicetype = objtype.getString("name")
                val img = "$devices_type_id.png"
                val bucket = client.storage["Devices"]
                val bytes = bucket.downloadPublic(img)
                val is1: InputStream = ByteArrayInputStream(bytes)
                val bmp: Bitmap = BitmapFactory.decodeStream(is1)
                val dr = BitmapDrawable(resources,bmp)



                when(devices_type_id){
                    "1"->powertext="$powervalue% яркость"
                    "2"->{
                        powertext="$powervalue℃ градусов"
                        power.max = 35
                        power1.max = 100
                        power1.visibility = View.VISIBLE
                        powername1.visibility = View.VISIBLE
                        underseek.visibility = View.VISIBLE
                        val layoutparams = fieldEdit.layoutParams as LinearLayout.LayoutParams
                        layoutparams.height=566
                        fieldEdit.layoutParams = layoutparams
                    }
                    "3"-> powertext="$powervalue% мощность"
                    "4"->{
                        powertext="$powervalue℃ градусов"
                        power.max = 35
                        underseek.visibility = View.VISIBLE
                    }
                    "5"->powertext="$powervalue% мощность"
                }


                image.setImageDrawable(dr)
                name.setText(namedevice)
                status.isChecked = statusValue.toBoolean()
                type.setText(namedevicetype)
                power.setProgress(powervalue.toInt())
                power1.setProgress(powervalue1.toInt())
                powername.setText(powertext)
                powername1.setText("$powervalue1% мощность")
            }catch (e: Exception){
                Log.e("Message",e.toString())
            }
        }
        power.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                lifecycleScope.launch {
                    deviceid = intent.getStringExtra("deviceid").toString()
                    try {
                        client.postgrest["Device"].update(
                            {
                                set("power", power.progress)
                            }
                        ) {
                            eq("id", deviceid)
                        }
                    }catch (e:Exception){
                        Log.e("Message",e.toString())
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
        power1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                lifecycleScope.launch {
                    deviceid = intent.getStringExtra("deviceid").toString()
                    try {
                        client.postgrest["Device"].update(
                            {
                                set("power1", power1.progress)
                            }
                        ) {
                            eq("id", deviceid)
                        }
                    }catch (e:Exception){
                        Log.e("Message",e.toString())
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        status.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                lifecycleScope.launch {
                    deviceid = intent.getStringExtra("deviceid").toString()
                    try {
                        client.postgrest["Device"].update(
                            {
                                set("status", "true")
                            }
                        ) {
                            eq("id", deviceid)
                        }
                    }catch (e:Exception){
                        Log.e("Message",e.toString())
                    }
                }
            }else{
                lifecycleScope.launch {
                    deviceid = intent.getStringExtra("deviceid").toString()
                    try {
                        client.postgrest["Device"].update(
                            {
                                set("status", "false")
                            }
                        ) {
                            eq("id", deviceid)
                        }
                    }catch (e:Exception){
                        Log.e("Message",e.toString())
                    }
                }

            }
        }

    }
    fun Back(view: View){
        roomid = intent.getStringExtra("roomid").toString()
        val intent = Intent(this, DeviceActivity::class.java)
        intent.putExtra("roomid",roomid)
        startActivity(intent)
    }
}