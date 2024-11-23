package com.example.smarthousetryagain

import android.graphics.drawable.Drawable
import kotlinx.serialization.Serializable

@Serializable
data class DataRoomsType(
    val room_id: Int,
    val type: String,
    val avatar: String
)

data class DataRoomsTypeWithImages(
    val room_id: Int,
    val type: String,
    val avatar: Drawable
)
