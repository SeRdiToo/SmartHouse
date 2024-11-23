package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.lifecycle.lifecycleScope

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
/*
    val client = createSupabaseClient(
        supabaseUrl = "https://ihyknrqszskicibjrtiv.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImloeWtucnFzenNraWNpYmpydGl2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzIyMTMxNjMsImV4cCI6MjA0Nzc4OTE2M30.fTYsD-bhpuEDLCNwfynB6YpBHpY9G9E164UoBRWEdAw"
    ){
        install(GoTrue)
        install(Postgrest)
    }*/

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
            val userlg = sb.getSB().gotrue.retrieveUserForCurrentSession(updateSession = true)
            try {
                val user = sb.getSB().postgrest["profile"].select(){
                    eq("id",userlg.id)
                }.decodeSingle<Profiles>()
                nameEdit.setText(user.first_name)
                addressEdit.setText(user.adress)
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
                sb.getSB().gotrue.modifyUser {
                    email = emailEdit.text.toString()
                }
                val user = sb.getSB().gotrue.retrieveUserForCurrentSession(updateSession = true)
                sb.getSB().postgrest["profile"].update(
                    {
                        set("adress", addressEdit.text.toString())
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