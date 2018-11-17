package com.epitech.flatot.epicture.Views

import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.R.id.navigation
import com.epitech.flatot.epicture.Views.FragmentBottom.*
//import com.epitech.flatot.epicture.Views.FragmentBottom.TabLayoutHome.HomeFragment
import kotlinx.android.synthetic.main.activity_bottom_nav.*
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

// 3 tests

class BottomNavActivityTest {

    val clientId: String? = "18acc2840b12049"
    val responseType: String? = "token"
    val clientSecret: String? = "2889b294a2c0992132070fd5f359d33aa0dbffc6"
    val redirectUrl: String? = "epicture://callback"
    val baseUrl = "https://api.imgur.com/oauth2/authorize"
    var accessToken: String? = null

    var myHomeFragment: HomeFragment? = null


    @Test
    fun firstTest() {
        Assert.assertTrue(1 == 1)
    }

    @Test
    fun createFragment() {}

    @Test
    fun onCreate() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "?client_id=" + clientId + "&response_type=" + responseType))
        var uri = intent.data
        if (uri != null && uri.toString().startsWith(redirectUrl!!)) {
            if (uri.getQueryParameter("error") == null) {
                val newUri = Uri.parse(uri.toString().replace('#', '?'))
                accessToken = newUri.getQueryParameter("access_token")
                assertNotNull(accessToken) //Get the value of accessToken in order to request the API
            }
        }
    }
}