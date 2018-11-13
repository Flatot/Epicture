package com.epitech.flatot.epicture.Views

import android.support.v7.app.AppCompatActivity
import com.epitech.flatot.epicture.Adapter.ViewPagerAdapter
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.R.id.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_zoomed.*
import org.junit.Test

import org.junit.Assert.*

class ZoomedActivityTest: AppCompatActivity(){

    @Test
    fun onCreate() {
        setContentView(R.layout.activity_zoomed)
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val urlimg = intent.getStringExtra("img_imgur")
        val list_link = intent.getStringArrayListExtra("list_link")
        if (list_link != null) {
            val adapter = ViewPagerAdapter(this, list_link)
            viewPager.adapter = adapter
        }
        else
            assertNull(Picasso.with(this).load(urlimg).into(img_imgur2))
        assertNull(title)
        assertNull(description)
        assertNull(urlimg)
        assertNull(list_link)
    }
}