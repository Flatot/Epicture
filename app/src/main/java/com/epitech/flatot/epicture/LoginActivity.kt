package com.epitech.flatot.epicture

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val clientId: String? = "18acc2840b12049"
    private val responseType: String? = "token"
    private val clientSecret: String? = "2889b294a2c0992132070fd5f359d33aa0dbffc6"
    private val redirectUrl: String? = "epicture://callback"
    private val baseUrl = "https://api.imgur.com/oauth2/authorize"
    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imgurView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "?client_id=" + clientId + "&response_type=" + responseType))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        var uri = intent.data
        if (uri != null && uri.toString().startsWith(redirectUrl!!)) {
            if (uri.getQueryParameter("error") == null) {
                val newUri = Uri.parse(uri.toString().replace('#', '?'))
                accessToken = newUri.getQueryParameter("access_token")
                val myIntent = Intent(this@LoginActivity, BottomNavActivity::class.java)
                myIntent.putExtra("access_token", accessToken)
                startActivity(myIntent)
            }
        }
    }
}
