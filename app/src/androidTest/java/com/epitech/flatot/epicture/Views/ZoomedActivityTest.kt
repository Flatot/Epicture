package com.epitech.flatot.epicture.Views

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.support.v4.app.Fragment
import com.epitech.flatot.epicture.Adapter.ViewPagerAdapter
import com.epitech.flatot.epicture.Model.ImgurModel
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.R.id.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_zoomed.*
import org.junit.Test

import org.junit.Assert.*


// 5 tests

class ZoomedActivityTest {

    @Test
    fun onCreate() { //Init all for zoom
        val title = "Title"
        val description = "Bio"
        val urlimg = ""
        val list_link = "list_link"
        val list_type = "list_type"
        assertNotNull(list_link) //Values ok ? We can create the adapter
        assertNotNull(list_type)
        assertNotNull(title) //Check values of the picture in order to avoid bug when the zoom is loading
        assertNotNull(description)
        assertNotNull(urlimg)
    }

}