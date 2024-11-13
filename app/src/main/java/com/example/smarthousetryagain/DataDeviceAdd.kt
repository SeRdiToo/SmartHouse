package com.example.smarthousetryagain

import kotlinx.serialization.Serializable

@Serializable
data class DataDeviceAdd(val id:String="",val name:String="", val device_type_id:String="", val status:String="", val identifier:String="", val power:String="", val power1:String="", val room_id:String)
