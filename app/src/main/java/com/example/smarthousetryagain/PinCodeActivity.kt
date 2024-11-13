package com.example.smarthousetryagain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity



class PinCodeActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var btnExit:Button
    private lateinit var title:TextView

    private lateinit var ellipse1:ImageView
    private lateinit var ellipse2:ImageView
    private lateinit var ellipse3:ImageView
    private lateinit var ellipse4:ImageView
    var number1 = false
    var number2 = false
    var number3 = false
    var number4 = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code)

        ellipse1=findViewById(R.id.ellipse1)
        ellipse2=findViewById(R.id.ellipse2)
        ellipse3=findViewById(R.id.ellipse3)
        ellipse4=findViewById(R.id.ellipse4)

        btnExit = findViewById(R.id.btnExit)
        title = findViewById(R.id.title)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        if(sharedPreferences.getBoolean("ISPINCODED",false)){
            btnExit.visibility = View.VISIBLE
            title.setText("Умный дом")
        }
        else{
            btnExit.visibility = View.INVISIBLE
            title.setText("Создайте пинкод")
        }
    }

    var pincode =""
    fun Pin(view:View){
        if (!number1) {
            ellipse1.setImageResource(R.drawable.ellipseon)
            number1 = true
        } else if (number1 && !number2) {
            ellipse2.setImageResource(R.drawable.ellipseon)
            number2 = true
        } else if (number2 && !number3) {
            ellipse3.setImageResource(R.drawable.ellipseon)
            number3 = true
        } else if (number3 && !number4) {
            ellipse4.setImageResource(R.drawable.ellipseon)
            number4 = true
        }
        when (view.id) {
            R.id.num1 -> pincode+= "1"
            R.id.num2 -> pincode+= "2"
            R.id.num3 -> pincode+= "3"
            R.id.num4 -> pincode+= "4"
            R.id.num5 -> pincode+= "5"
            R.id.num6 -> pincode+= "6"
            R.id.num7 -> pincode+= "7"
            R.id.num8 -> pincode+= "8"

            R.id.num9 -> pincode+= "9"
        }
        if (pincode.length == 4) {
            if(!sharedPreferences.getBoolean("ISPINCODED",false)){
                val editor:SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("PINCODE",pincode)
                editor.putBoolean("ISPINCODED",true)
                editor.apply()
                val intent = Intent(this@PinCodeActivity, AddressActivity::class.java)
                startActivity(intent)
            }
            else{
                if(pincode==sharedPreferences.getString("PINCODE","")){
                    if(sharedPreferences.getBoolean("ISADDRESSED",false)){
                        val intent = Intent(this@PinCodeActivity, MainScreen::class.java)
                        startActivity(intent)
                    }else{
                        val intent = Intent(this@PinCodeActivity, AddressActivity::class.java)
                        startActivity(intent)
                    }
                }
                else{
                    pincode=""
                    ellipse1.setImageResource(R.drawable.ellipseoff)
                    ellipse2.setImageResource(R.drawable.ellipseoff)
                    ellipse3.setImageResource(R.drawable.ellipseoff)
                    ellipse4.setImageResource(R.drawable.ellipseoff)
                    number1 = false
                    number2 = false
                    number3 = false
                    number4 = false
                    val incorrect: Animation = AnimationUtils.loadAnimation(this, R.anim.pincode)
                    ellipse1.startAnimation(incorrect)
                    ellipse2.startAnimation(incorrect)
                    ellipse3.startAnimation(incorrect)
                    ellipse4.startAnimation(incorrect)
                }
            }
        }

    }
    fun Exit(view: View){
        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("ISEXITED",true)
        editor.apply()
        val intent = Intent(this@PinCodeActivity, MainActivity::class.java)
        startActivity(intent)
    }
}