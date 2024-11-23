package com.example.smarthousetryagain

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception

class MainScreen : AppCompatActivity() {
    var array: JSONArray? = null
    var viewItems: List<DataRooms> = ArrayList<DataRooms>()
    var roomsListWithImages: ArrayList<DataRoomsWithImages> = ArrayList<DataRoomsWithImages>()
    //private val adapter = AdapterRooms(viewItems,this@MainScreen)

    private lateinit var addressText: TextView

    /*    val client = createSupabaseClient(
            supabaseUrl = "https://ihyknrqszskicibjrtiv.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImloeWtucnFzenNraWNpYmpydGl2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzIyMTMxNjMsImV4cCI6MjA0Nzc4OTE2M30.fTYsD-bhpuEDLCNwfynB6YpBHpY9G9E164UoBRWEdAw"
        ){
            install(GoTrue)
            install(Postgrest)
            install(Storage)
        }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        addressText = findViewById(R.id.addressText)
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this@MainScreen)
        /*        roomsListWithImages.add(
                    DataRoomsWithImages(
                        room_id = 1,
                        name = "TEST",
                        type_id = 1,
                        user_id = "97f26633-bf1b-4576-a124-cf0c5737a20c",
                        images = getDrawable(R.drawable.room_tv_off),
                    )
                )*/
        val adapter = AdapterRooms(roomsListWithImages, this@MainScreen)
        lifecycleScope.launch {
            //
            /*            sb.getSB().gotrue.loginWith(Email){
                            email = "te@example.com"
                            password = "1234567890"
                        }*/
            //

            val userlg = sb.getSB().gotrue.retrieveUserForCurrentSession(updateSession = true)
            Log.e("User", userlg.id.toString())
            try {
                val user = sb.getSB().postgrest["Profiles"].select() {
                    eq("id", userlg.id)
                }.decodeSingle<Profiles>()
                Log.e("!Adress", user.adress.toString())
                Log.e("!Adress", user.adress.toString())
                addressText.setText(user.adress)
            } catch (e: Exception) {
                Log.e("ErrorProfile", e.toString())
            }
            //val userlg = sb.getSB().gotrue.retrieveUserForCurrentSession(updateSession = true)
            try {
                val room_type = sb.getSB().postgrest["Room_type"].select() {
                }.decodeList<DataRoomsType>()

                Log.e("!RoomMainType!", room_type[0].room_id.toString() + "|" + room_type[0].type)

                val client = sb.getSB().postgrest["Room"].select() {
                    eq("user_id", userlg.id)
                }.decodeList<DataRooms>()

                Log.e("!RoomMainScree1!", client[0].room_id.toString() + "|" + client[0].name)

                for (c in client) {
                    Log.e("id: " + c.room_id, "|" + c.name + "|" + room_type[c.type_id])
                }

                /*              val buf_client = StringBuilder()
                              buf_client.append(client.body.toString()).append("\n")
                              array = JSONArray(buf_client.toString())
                              addItemsFromJSON()*/
                //viewItems = client
                addRoomListImages(client, room_type, adapter)

                //adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("!RoomMainScree2!", e.toString())
            }

        }
        // adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
        // val adapter = AdapterRooms(viewItems, this@MainScreen)


        /*        lifecycleScope.launch {

                }*/

        // recyclerView.adapter = adapter
    }

    fun Profile(view: View) {
        val intent = Intent(this@MainScreen, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun Add(view: View) {
        val intent = Intent(this@MainScreen, RoomActivity::class.java)
        startActivity(intent)
    }

    private fun addRoomListImages(
        roomsList: List<DataRooms>,
        roomsTypeList: List<DataRoomsType>,
        adapter: AdapterRooms
    ) {
        try {
            var i = 0
            for (c in roomsList) {
                Log.e("id: " + c.room_id, "|" + c.name + "|" + roomsTypeList[c.type_id])
                //val img = "$rooms_type_id.png"
                val img_ = roomsTypeList[c.type_id]
                Log.e("!img_!:" + i, img_.avatar)
                //  val img = "$img_.png"
                lifecycleScope.launch {
                    try {
                        val bucket = sb.getSB().storage["Rooms"]
                        val bytes = bucket.downloadPublic("${img_.avatar}.png")
                        val is1: InputStream = ByteArrayInputStream(bytes)
                        val bmp: Bitmap = BitmapFactory.decodeStream(is1)
                        val dr = BitmapDrawable(resources, bmp)
                        roomsListWithImages.add(
                            DataRoomsWithImages(
                                room_id = c.room_id,
                                name = c.name,
                                type_id = c.type_id,
                                user_id = c.user_id,
                                images = dr,
                            )
                        )
                        Log.e("!roomsListWithImages!:" + i, roomsListWithImages[i].name)
                        i++
                    } catch (e: Exception) {
                        Log.e("MESSAGE", e.toString())
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        } catch (e: Exception) {
            Log.e("MESSAGE", e.toString())
        }
    }

    /* private fun addItemsFromJSON() {
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
                         val bucket = sb.getSB().storage["Rooms"]
                         val bytes = bucket.downloadPublic(img)
                         val is1: InputStream = ByteArrayInputStream(bytes)
                         val bmp: Bitmap = BitmapFactory.decodeStream(is1)
                         val dr = BitmapDrawable(resources, bmp)
                         val rooms = DataRooms(id, rooms_type_id, name, dr)
                         adapter.notifyDataSetChanged()
                         viewItems.add(rooms)
                     } catch (e: Exception) {
                         Log.e("MESSAGE", e.toString())
                     }
                 }
             }
         } catch (e: JSONException) {
         }
     }*/
}