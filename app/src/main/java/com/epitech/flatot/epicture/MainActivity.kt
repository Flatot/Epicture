package com.epitech.flatot.epicture

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {

    private var timeout = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({
            val main = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(main)
            finish()
        }, timeout.toLong())
    }
}
