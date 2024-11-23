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
        lifecycleScope.launch{
            try {
                sb.getSB().gotrue.loginWith(io.github.jan.supabase.gotrue.providers.builtin.Email){
                    email = emailEdit.text.toString()
                    password = passwordEdit.text.toString()
                }
                val editor:SharedPreferences.Editor = sharedPreferences.edit()
                editor.putBoolean("ISEXITED",false)
                editor.apply()
                if(sharedPreferences.getBoolean("ISPINCODED",false)==true){
                    val intent = Intent(this@MainActivity, PinCodeActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this@MainActivity, MainScreen::class.java)
                    startActivity(intent)
                }
            }catch (e:Exception){
                Log.e("Message",e.toString())
            }
        }
    }
    fun SignUp(view:View){
        val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
        startActivity(intent)
    }
}