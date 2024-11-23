package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.lifecycle.lifecycleScope

import com.example.smarthousetryagain.MainScreen
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.lang.Exception

class AddressActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var addressEdit: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        addressEdit = findViewById(R.id.addressEdit)
    }

    fun Save(view: View) {

        lifecycleScope.launch {
            try {

                val user = sb.getSB().gotrue.retrieveUserForCurrentSession(updateSession = true)
                Log.e("UserInAdress", user.id.toString())
                sb.getSB().postgrest["Profiles"].update({
                    set("adress", addressEdit.text.toString())
                }) {
                    eq("id", user.id)
                }
                val isAdd = true
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putBoolean("ISADDRESSED", isAdd)
                editor.apply()
            } catch (e: Exception) {
                Log.e("ErrorProfileAdress", e.toString())
            }
        }
        val intent = Intent(this@AddressActivity, MainScreen::class.java)
        startActivity(intent)
    }
}