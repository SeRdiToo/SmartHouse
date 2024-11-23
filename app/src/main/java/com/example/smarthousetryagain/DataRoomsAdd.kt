package com.example.smarthousetryagain

import kotlinx.serialization.Serializable

@Serializable
data class DataRoomsAdd(
    val id:String="",
    val user_id:String,
    val rooms_type_id:Int=0,
    val name:String="")
