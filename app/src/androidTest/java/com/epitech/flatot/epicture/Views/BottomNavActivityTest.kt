package com.epitech.flatot.epicture.Views

import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.R.id.navigation
import com.epitech.flatot.epicture.Views.FragmentBottom.FavoriteFragment
import com.epitech.flatot.epicture.Views.FragmentBottom.ProfilFragment
import com.epitech.flatot.epicture.Views.FragmentBottom.SearchFragment
import com.epitech.flatot.epicture.Views.FragmentBottom.TabLayoutHome.HomeFragment
import com.epitech.flatot.epicture.Views.FragmentBottom.UploadFragment
import kotlinx.android.synthetic.main.activity_bottom_nav.*
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

class BottomNavActivityTest: AppCompatActivity() {

    private val clientId: String? = "18acc2840b12049"
    private val responseType: String? = "token"
    private val clientSecret: String? = "2889b294a2c0992132070fd5f359d33aa0dbffc6"
    private val redirectUrl: String? = "epicture://callback"
    private val baseUrl = "https://api.imgur.com/oauth2/authorize"
    private var accessToken: String? = null
    private var accountUsername: String? = null
    private var refreshToken: String? = null

    private val manager = supportFragmentManager
    private var myHomeFragment: HomeFragment? = null


    @Test
    fun firstTest() {
        Assert.assertTrue(1 == 1)
    }

    @Test
    private fun createFragment(fragment: Fragment)
    {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.mainFrame, fragment)
        transaction.commit()
    }

    @Test
    fun onCreate() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "?client_id=" + clientId + "&response_type=" + responseType))
        var uri = intent.data
        if (uri != null && uri.toString().startsWith(redirectUrl!!)) {
            if (uri.getQueryParameter("error") == null) {
                val newUri = Uri.parse(uri.toString().replace('#', '?'))
                accessToken = newUri.getQueryParameter("access_token")
            }
        }
        myHomeFragment = HomeFragment.newInstance(accessToken!!)
        assertNull(myHomeFragment)
        assertNull(createFragment(myHomeFragment as Fragment))
    }
}