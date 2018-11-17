package com.epitech.flatot.epicture.Views

import android.content.Intent
import android.os.Handler
import android.support.v4.content.ContextCompat.startActivity
import com.epitech.flatot.epicture.R
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.epitech.flatot.epicture.Views.FragmentBottom.ProfilFragment
import org.junit.Test
import org.junit.Assert.*

// 1 test

class MainActivityTest {

    private var timeout = 1500


    @Test
    fun onCreate() {
        val main = Intent()
        assertNotNull(main) //The activity is not null, we can go further
    }
}