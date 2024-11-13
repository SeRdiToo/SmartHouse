package com.example.smarthousetryagain

import kotlinx.serialization.Serializable

@Serializable
data class DataRoomsAdd(val id:String="",val user_id:String, val rooms_type_id:String="",val name:String="")
