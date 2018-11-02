package com.epitech.flatot.epicture.Views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.epitech.flatot.epicture.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_zoomed.*

class ZoomedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoomed)

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val urlimg = intent.getStringExtra("img_imgur")

        val list_link = intent.getStringExtra("list_link")
        val list_description = intent.getStringExtra("list_description")

        title2.text = title
        description2.text = description
        Picasso.with(this).load(urlimg).into(img_imgur2)
    }
}
