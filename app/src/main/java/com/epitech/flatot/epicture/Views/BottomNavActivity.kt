package com.epitech.flatot.epicture.Views

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.Views.FragmentBottom.*
import com.epitech.flatot.epicture.Views.FragmentBottom.TabLayoutHome.HomeFragment
import kotlinx.android.synthetic.main.activity_bottom_nav.*

class BottomNavActivity : AppCompatActivity() {

    private var access_token: String? = null
    private var refresh_token: String? = null
    private var username: String? = null
    private val manager = supportFragmentManager
    private var myUploadFragment: UploadFragment? = null
    private var mySearchFragment: SearchFragment? = null
    private var myProfilFragment: ProfilFragment? = null
    private var myFavoriteFragment: FavoriteFragment? = null


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                //message.setText(R.string.title_home)
                val fragment = HomeFragment.newInstance(access_token!!)
                createFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                //message.setText(R.string.title_dashboard)
                //val fragment = SearchFragment.newInstance(access_token!!)
                createFragment(mySearchFragment as Fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favoris -> {
                //message.setText(R.string.title_dashboard)
                val fragment = FavoriteFragment.newInstance(access_token!!)
                createFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profil -> {
                //message.setText(R.string.title_notifications)
                val fragment = ProfilFragment.newInstance(access_token!!, refresh_token!!, username!!)
                createFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_upload -> {
                //message.setText(R.string.title_dashboard)
                createFragment(myUploadFragment as Fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun createFragment(fragment: Fragment)
    {
        val transaction = manager.beginTransaction()

        transaction.replace(R.id.mainFrame, fragment)
        transaction.commit()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        access_token = intent.getStringExtra("access_token")
        refresh_token = intent.getStringExtra("refresh_token")
        username = intent.getStringExtra("account_username")

        myUploadFragment = UploadFragment.newInstance(access_token!!)
        mySearchFragment = SearchFragment.newInstance(access_token!!)
        myFavoriteFragment = FavoriteFragment.newInstance(access_token!!)
        myProfilFragment = ProfilFragment.newInstance(access_token!!, refresh_token!!, username!!)

        val fragment = HomeFragment.newInstance(access_token!!)
        createFragment(fragment)
    }
}
