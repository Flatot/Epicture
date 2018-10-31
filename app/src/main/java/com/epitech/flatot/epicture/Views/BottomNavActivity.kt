package com.epitech.flatot.epicture.Views

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.Views.FragmentBottom.HomeFragment
import com.epitech.flatot.epicture.Views.FragmentBottom.ProfilFragment
import com.epitech.flatot.epicture.Views.FragmentBottom.SearchFragment
import com.epitech.flatot.epicture.Views.FragmentBottom.UploadFragment
import kotlinx.android.synthetic.main.activity_bottom_nav.*
import kotlinx.android.synthetic.main.fragment_upload.*

class BottomNavActivity : AppCompatActivity() {

    private var access_token: String? = null
    private val manager = supportFragmentManager
    private var myUploadFragment: UploadFragment? = null


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                //message.setText(R.string.title_home)
                val fragment = HomeFragment.newInstance(access_token!!)
                fragment.getAlbums()
                createFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                //message.setText(R.string.title_dashboard)
                createFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profil -> {
                //message.setText(R.string.title_notifications)
                createFragment(ProfilFragment())
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

        myUploadFragment = UploadFragment()

        val fragment = HomeFragment.newInstance(access_token!!)
        fragment.getAlbums()
        createFragment(fragment)
    }
}
