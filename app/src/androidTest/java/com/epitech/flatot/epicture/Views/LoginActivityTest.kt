package com.epitech.flatot.epicture.Views


import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.activity_login.*
import android.support.v4.content.ContextCompat.startActivity
import org.junit.Test

import org.junit.Assert.*

class LoginActivityTest {

    private val clientId: String? = "18acc2840b12049"
    private val responseType: String? = "token"
    private val clientSecret: String? = "2889b294a2c0992132070fd5f359d33aa0dbffc6"
    private val redirectUrl: String? = "epicture://callback"
    private val baseUrl = "https://api.imgur.com/oauth2/authorize"
    private var accessToken: String? = null
    private var accountUsername: String? = null
    private var refreshToken: String? = null

    @Test
    fun onCreate() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "?client_id=" + clientId + "&response_type=" + responseType))
        assertNotNull(intent)
    }

    @Test
    fun onResume() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "?client_id=" + clientId + "&response_type=" + responseType))
        var uri = intent.data
        if (uri != null && uri.toString().startsWith(redirectUrl!!)) {
            if (uri.getQueryParameter("error") == null) {
                val newUri = Uri.parse(uri.toString().replace('#', '?'))
                accessToken = newUri.getQueryParameter("access_token")
                refreshToken = newUri.getQueryParameter("refresh_token")
                accountUsername = newUri.getQueryParameter("account_username")
                assertNotNull(accessToken)
                assertNotNull(refreshToken)
                assertNotNull(accountUsername)
            }
        }
    }
}