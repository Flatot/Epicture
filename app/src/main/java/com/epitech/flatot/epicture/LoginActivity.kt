package com.epitech.flatot.epicture

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.widget.Toast
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private val clientId: String? = "18acc2840b12049"
    private val responseType: String? = "token"
    private val clientSecret: String? = "2889b294a2c0992132070fd5f359d33aa0dbffc6"
    private val redirectUrl: String? = "epicture://callback"
    private val grantType: String? = "authorization_code"
    private val baseUrl = "https://api.imgur.com/oauth2/authorize"
    private val baseUrlToken = "https://api.imgur.com/oauth2/token"
    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imgurView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "?client_id=" + clientId + "&response_type=" + responseType))
            startActivity(intent)
        }
        //val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrlToken + "?client_id=" + clientId + "&response_type=" + responseType))
    }

    override fun onResume() {
        super.onResume()

        var uri = intent.data
        if (uri != null && uri.toString().startsWith(redirectUrl!!)) {
            if (uri.getQueryParameter("error") == null) {
                val newUri = Uri.parse(uri.toString().replace('#', '?'))
                accessToken = newUri.getQueryParameter("access_token")
                Toast.makeText(this, accessToken, Toast.LENGTH_LONG).show()
                val myIntent = Intent(this@LoginActivity, HomeActivity::class.java)
                myIntent.putExtra("access_token", accessToken)
                startActivity(myIntent)
            }
        }
    }
}
