package com.example.smarthousetryagain

import android.graphics.drawable.Drawable
import kotlinx.serialization.Serializable

@Serializable
data class DataRooms(
    val room_id: Int,
    val name: String,
    val type_id: Int,
    val user_id: String,
    //val avatar: String
)
@Serializable
data class DataRoomsPOST(
    val name: String,
    val type_id: Int,
    val user_id: String,
    //val avatar: String
)

data class DataRoomsWithImages(
    val room_id: Int,
    val name: String,
    val type_id: Int,
    val user_id: String,
    val images: Drawable?
)