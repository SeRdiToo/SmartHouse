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

import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


class RegistrationActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
/*    val client = createSupabaseClient(
        supabaseUrl = "https://ihyknrqszskicibjrtiv.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImloeWtucnFzenNraWNpYmpydGl2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzIyMTMxNjMsImV4cCI6MjA0Nzc4OTE2M30.fTYsD-bhpuEDLCNwfynB6YpBHpY9G9E164UoBRWEdAw"
    ){
        install(GoTrue)
        install(Postgrest)
    }*/
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
                        try {
                           sb.getSB().gotrue.signUpWith(io.github.jan.supabase.gotrue.providers.builtin.Email){
                                email = emailEdit.text.toString()
                                password = passwordEdit.text.toString()
                            }
                        }
                        catch (e: Exception){
                            Log.e("!gotrue", e.toString())
                        }


/*                        val user = sb.getSB().gotrue.retrieveUserForCurrentSession(updateSession = true)
                        Log.e("!REGISTRATION!NAME!", nameEdit.text.toString())
                        val useradd = Users(id = user.id, first_name = nameEdit.text.toString())
                        Log.e("!REGISTRATION!NAME!", useradd.first_name)
                        sb.getSB().postgrest["profiles"].insert(useradd)
                        */
                        val intent = Intent(this@RegistrationActivity, PinCodeActivity::class.java)
                        intent.putExtra("username", nameEdit.text.toString())
                        startActivity(intent)
                    }catch (e: Exception){
                        Log.e("gotrue",e.toString())
                    }
                    lifecycleScope.launch{
                        val user = sb.getSB().gotrue.retrieveUserForCurrentSession(updateSession = true)
                        val useradd = Profiles(
                            id = user.id,
                            first_name = nameEdit.text.toString(),
                            last_name = "-",
                            adress = "-")
                        Log.e("!TEST!NAME!", useradd.id + "!" + useradd.first_name)
                        sb.getSB().postgrest["Profiles"].insert(useradd)
                    }

                }
     /*           lifecycleScope.launch{
                    try {
                        val user = sb.getSB().gotrue.retrieveUserForCurrentSession(updateSession = true)
                        Log.e("!REGISTRATION!NAME!", nameEdit.text.toString())
                        val useradd = Users(id = user.id, first_name = nameEdit.text.toString())
                        Log.e("!REGISTRATION!NAME!", useradd.first_name)
                        sb.getSB().postgrest["profiles"].insert(useradd)
                        val intent = Intent(this@RegistrationActivity, PinCodeActivity::class.java)
                        startActivity(intent)
                    }catch (e: Exception){
                        Log.e("profile",e.toString())
                    }
                }*/
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