package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

import com.example.smarthousetryagain.PinCodeActivity
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.lang.Exception

class RegistrationActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    val client = createSupabaseClient(
        supabaseUrl = "https://fogygiutqidwswxezefb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZvZ3lnaXV0cWlkd3N3eGV6ZWZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzE0MzkyODksImV4cCI6MjA0NzAxNTI4OX0.09u_eu_f2zBE9PGxQUJC6zWgJGOoL_5zSJw-JUWkqqY"
    ){
        install(GoTrue)
        install(Postgrest)
    }
    lateinit var nameEdit:EditText
    lateinit var emailEdit:EditText
    lateinit var passwordEdit:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        nameEdit=findViewById(R.id.nameEdit)
        emailEdit=findViewById(R.id.emailEdit)
        passwordEdit=findViewById(R.id.passwordEdit)
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
    }
    fun SignUp(view: View){
        val emailPattern = "[a-z0-9._]+@[a-z]+\\.+[a-z]+"
        if(emailEdit.text.toString().matches(emailPattern.toRegex())){
            if(emailEdit.text.toString()!=""&&passwordEdit.text.toString()!=""){
                lifecycleScope.launch{
                    try {
                        client.gotrue.signUpWith(io.github.jan.supabase.gotrue.providers.builtin.Email){
                            email = emailEdit.text.toString()
                            password = passwordEdit.text.toString()
                        }
                        val user = client.gotrue.retrieveUserForCurrentSession(updateSession = true)
                        val editor:SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putBoolean("ISREGISTRATED",true)
                        editor.putBoolean("ISPINCODED",false)
                        editor.putBoolean("ISADDREESSED",false)
                        editor.apply()
                        val useradd = Users(id = user.id, name = nameEdit.text.toString())
                        client.postgrest["User"].insert(useradd)
                        val intent = Intent(this@RegistrationActivity, PinCodeActivity::class.java)
                        startActivity(intent)
                    }catch (e: Exception){
                        Log.e("Message",e.toString())
                    }
                }
            }else Toast.makeText(this, "Не все поля заполнены!", Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this, "Проверьте правильность введенной почты!", Toast.LENGTH_SHORT).show()
    }
    fun  SignIn(view: View){
        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("ISEXITED",true)
        editor.putBoolean("ISREGISTRATED",true)
        editor.putBoolean("ISPINCODED",true)
        editor.putBoolean("ISADDRESSED",true)
        editor.apply()
        val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
        startActivity(intent)
    }
}