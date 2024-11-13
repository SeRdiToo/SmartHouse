package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.http.HttpResponseCache.install
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import java.lang.Exception
import androidx.lifecycle.lifecycleScope

import com.example.smarthousetryagain.MainScreen
import com.example.smarthousetryagain.PinCodeActivity
import com.example.smarthousetryagain.RegistrationActivity
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var emailEdit:EditText
    lateinit var passwordEdit:EditText

    val client = createSupabaseClient(
        supabaseUrl = "https://fogygiutqidwswxezefb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZvZ3lnaXV0cWlkd3N3eGV6ZWZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzE0MzkyODksImV4cCI6MjA0NzAxNTI4OX0.09u_eu_f2zBE9PGxQUJC6zWgJGOoL_5zSJw-JUWkqqY"
    ){
        install(GoTrue)
        install(Postgrest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        if(sharedPreferences.getBoolean("ISREGISTRATED",false)==false){
            val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        else if(sharedPreferences.getBoolean("ISEXITED",false)==false){
            val intent = Intent(this@MainActivity, PinCodeActivity::class.java)
            startActivity(intent)
        }
        emailEdit = findViewById(R.id.emailEdit)
        passwordEdit = findViewById(R.id.passwordEdit)
        val email = sharedPreferences.getString("EMAIL","")
        emailEdit.setText(email)
    }
    fun SignIn(view:View){
        if(emailEdit.text.toString()!=""&&passwordEdit.text.toString()!=""){
            lifecycleScope.launch{
                try {
                    client.gotrue.loginWith(Email){
                        email = emailEdit.text.toString()
                        password = passwordEdit.text.toString()
                    }
                    val editor:SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("ISEXITED",false)
                    editor.apply()
                    if(sharedPreferences.getBoolean("ISPINCODED",false)==true){
                        val intent = Intent(this@MainActivity, MainScreen::class.java)
                        startActivity(intent)
                    }
                    else{
                        val intent = Intent(this@MainActivity, PinCodeActivity::class.java)
                        startActivity(intent)
                    }
                }catch (e:Exception){
                    Log.e("Message",e.toString())
                }
            }
        }
        else Toast.makeText(this, "Ошибка, не все поля заполнены!", Toast.LENGTH_SHORT).show()
    }
    fun SignUp(view:View){
        val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
        startActivity(intent)
    }
}