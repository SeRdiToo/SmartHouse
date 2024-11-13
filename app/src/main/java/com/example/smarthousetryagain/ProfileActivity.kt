package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

import com.example.smarthousetryagain.RegistrationActivity
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    val client = createSupabaseClient(
        supabaseUrl = "https://fogygiutqidwswxezefb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZvZ3lnaXV0cWlkd3N3eGV6ZWZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzE0MzkyODksImV4cCI6MjA0NzAxNTI4OX0.09u_eu_f2zBE9PGxQUJC6zWgJGOoL_5zSJw-JUWkqqY"
    ){
        install(GoTrue)
        install(Postgrest)
    }

    private lateinit var nameEdit:EditText
    private lateinit var emailEdit:EditText
    private lateinit var addressEdit:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        nameEdit=findViewById(R.id.nameEdit)
        emailEdit=findViewById(R.id.emailEdit)
        addressEdit=findViewById(R.id.addressEdit)
        lifecycleScope.launch{
            val userlg = client.gotrue.retrieveUserForCurrentSession(updateSession = true)
            try {
                val user = client.postgrest["User"].select(){
                    eq("id",userlg.id)
                }.decodeSingle<Users>()
                nameEdit.setText(user.name)
                addressEdit.setText(user.address)
                emailEdit.setText(userlg.email)
            }catch (e: Exception){
                Log.e("Message",e.toString())
            }
        }
    }
    fun Back(view:View){
        val intent = Intent(this@ProfileActivity, MainScreen::class.java)
        startActivity(intent)
    }
    fun Save(view: View){


        lifecycleScope.launch {
            try {
                client.gotrue.modifyUser {
                    email = emailEdit.text.toString()
                }
                val user = client.gotrue.retrieveUserForCurrentSession(updateSession = true)
                client.postgrest["User"].update(
                    {
                        set("address", addressEdit.text.toString())
                        set("name", nameEdit.text.toString())
                    }
                ) {
                    eq("id", user.id)
                }
            }catch (e:Exception){
                Log.e("Message",e.toString())
            }
        }

    }
    fun Exit(view: View){
        val intent = Intent(this@ProfileActivity, RegistrationActivity::class.java)
        startActivity(intent)
    }
}