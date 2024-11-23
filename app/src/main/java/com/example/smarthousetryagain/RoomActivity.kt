package com.example.smarthousetryagain

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception

class RoomActivity : AppCompatActivity() {
    var array: JSONArray? = null
    var viewItems: ArrayList<DataRoomsType> = ArrayList<DataRoomsType>()
    private lateinit var nameEdit: EditText
    var roomtypeid: Int = 0
    var roomTypesListWithImages: ArrayList<DataRoomsTypeWithImages> =
        ArrayList<DataRoomsTypeWithImages>()
    private val adapter = AdapterRoomsType(roomTypesListWithImages,
        this@RoomActivity,
        object : AdapterRoomsType.ItemClickListener {
            override fun OnItemClick(typeId: Int) {
                roomtypeid = typeId
            }
        })

    /*    val client = createSupabaseClient(
            supabaseUrl = "https://ihyknrqszskicibjrtiv.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImloeWtucnFzenNraWNpYmpydGl2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzIyMTMxNjMsImV4cCI6MjA0Nzc4OTE2M30.fTYsD-bhpuEDLCNwfynB6YpBHpY9G9E164UoBRWEdAw"
        ) {
            install(GoTrue)
            install(Postgrest)
            install(Storage)
        }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        nameEdit = findViewById(R.id.nameEdit)
        val recyclerView: RecyclerView = findViewById(R.id.recycler)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        try {
            lifecycleScope.launch {
                val client = sb.getSB().postgrest["Room_type"].select().decodeList<DataRoomsType>()
                Log.e("!", client[0].type + "|" + client[0].avatar)
                addRoomTypeListImages(client, roomTypesListWithImages, adapter)

                /*                val client = sb.getSB().postgrest["Room"].select().decodeList<DataRoomsType>()
                                Log.e("!", client[0].type + "|" + client[0].avatar)*/

                /*                val buf_client = StringBuilder()
                                buf_client.append(client.body.toString()).append("\n")
                                array = JSONArray(buf_client.toString())*/


                // addItemsFromJSON()
            }
        } catch (e: Exception) {
            Log.e("!Room_type!!", e.toString())
        }
        recyclerView.adapter = adapter
    }

    private fun addRoomTypeListImages(
        roomsTypeList: List<DataRoomsType>,
        roomsTypeListWithImage: ArrayList<DataRoomsTypeWithImages>,
        adapter: AdapterRoomsType
    ) {
        try {
            var i = 0;
            for (c in roomsTypeList) {
                Log.e("id: " + c.room_id, "|" + c.type + "|" + c.avatar)
                lifecycleScope.launch {
                    try {
                        val bucket = sb.getSB().storage["Rooms"]
                        val bytes = bucket.downloadPublic("${c.avatar}.png")
                        val is1: InputStream = ByteArrayInputStream(bytes)
                        val bmp: Bitmap = BitmapFactory.decodeStream(is1)
                        val dr = BitmapDrawable(resources, bmp)
                        roomsTypeListWithImage.add(
                            DataRoomsTypeWithImages(
                                room_id = c.room_id,
                                type = c.type,
                                avatar = dr
                            )
                        )
                        Log.e("!addRoomTypeListImages!:" + i, roomsTypeListWithImage[i].type)
                        i++
                    } catch (e: Exception) {
                        Log.e("ERRORddRoomTypeListImages", e.toString())
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        } catch (e: Exception) {
            Log.e("MESSAGE", e.toString())
        }
    }
    /*  private fun addItemsFromJSON() {
          try {
              for (i in 0 until array!!.length()) {
                  val itemObj: JSONObject = array!!.getJSONObject(i)
                  val room_id = itemObj.getString("room_id")
                  val name = itemObj.getString("name")
                  val avatar = itemObj.getString("avatar")
                  val type_id = itemObj.getString("type_id")
                  val user_id = itemObj.getString("user_id")
                  lifecycleScope.launch {
                      try {
                          val bucket = client.storage["Rooms_type"]
                          val bytes = bucket.downloadPublic(avatar)
                          val is1:InputStream = ByteArrayInputStream(bytes)
                          val bmp: Bitmap = BitmapFactory.decodeStream(is1)
                          val dr = BitmapDrawable(resources,bmp)
                          val room = DataRoomsType(room_id,name,type_id,user_id,avatar)
                          adapter.notifyDataSetChanged()
                          viewItems.add(DataRoomsType(room_id,name,type_id,user_id,avatar))
                      }catch (e:Exception){
                          Log.e("MESSAGE",e.toString())
                      }
                  }
              }
          } catch (e: JSONException) {
          }
      }*/


    fun Save(view: View) {
        if (nameEdit.text.toString() != "") {
            lifecycleScope.launch {
                try {
                    Log.e("!Сохранение!Комнаты!", roomtypeid.toString())
                    val user = sb.getSB().gotrue.retrieveUserForCurrentSession(updateSession = true)
                    val roomadd = DataRoomsPOST(
                        user_id = user.id,
                        name = nameEdit.text.toString(),
                        type_id = roomtypeid,
                    )
                    sb.getSB().postgrest["Room"].insert(roomadd)
                    val intent = Intent(this@RoomActivity, MainScreen::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("Message", e.toString())
                }
            }
        } else Toast.makeText(this, "Введите имя комнаты!", Toast.LENGTH_SHORT).show()
    }

    fun Back(view: View) {
        val intent = Intent(this@RoomActivity, MainScreen::class.java)
        startActivity(intent)
    }
}