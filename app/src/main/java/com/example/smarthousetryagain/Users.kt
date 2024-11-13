package com.example.smarthousetryagain

import kotlinx.serialization.Serializable

@Serializable
data class Users(val id:String = "", val name: String = "",val address:String = "")
