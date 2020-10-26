package com.example.recordedweather.activity.activity.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.recordedweather.R
import kotlinx.android.synthetic.main.activity_splash.*
import java.text.SimpleDateFormat
import java.util.*


class Splash_Activity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_splash)







        gotoNextActivity()

    }


     fun gotoNextActivity() {

        Handler().postDelayed(Runnable {
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }, 3000)


    }


}
