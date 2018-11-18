package com.epitech.flatot.epicture.Views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.epitech.flatot.epicture.Adapter.ViewPagerAdapter
import com.epitech.flatot.epicture.Model.GlideModel
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.activity_zoomed.*

class ZoomedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoomed)

        var title = intent.getStringExtra("title")
        var description = intent.getStringExtra("description")
        val urlimg = intent.getStringExtra("img_imgur")
        val type = intent.getStringExtra("type")

        val list_link = intent.getStringArrayListExtra("list_link")
        val list_type = intent.getStringArrayListExtra("list_type")

        if (list_link != null) {
            val adapter = ViewPagerAdapter(this, list_link, list_type)
            viewPager.adapter = adapter
        }
        else {
            GlideModel().displayGlide(type, this@ZoomedActivity, urlimg, img_imgur2)
        }
        if (title != null && title.length > 40)
            title = title.take(40) + "..."
        title2.text = title
        if (description != null && description.length > 120)
            description = description.take(120) + "..."
        description2.text = description
    }
}
