package com.epitech.flatot.epicture.Views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.epitech.flatot.epicture.Adapter.ViewPagerAdapter
import com.epitech.flatot.epicture.Model.GlideInterface
import com.epitech.flatot.epicture.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_zoomed.*
import kotlinx.android.synthetic.main.item_cardview.view.*

class ZoomedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoomed)

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val urlimg = intent.getStringExtra("img_imgur")
        val type = intent.getStringExtra("type")

        val list_link = intent.getStringArrayListExtra("list_link")
        val list_type = intent.getStringArrayListExtra("list_type")

        if (list_link != null) {
            val adapter = ViewPagerAdapter(this, list_link, list_type)
            viewPager.adapter = adapter
        }
        else {
            GlideInterface().displayGlide(type, this@ZoomedActivity, urlimg, img_imgur2)
        }
        title2.text = title
        description2.text = description
    }
}
