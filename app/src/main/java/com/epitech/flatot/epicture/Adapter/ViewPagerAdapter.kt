package com.epitech.flatot.epicture.Adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.epitech.flatot.epicture.R.id.img_imgur2
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_zoomed.*

class ViewPagerAdapter(val context: Context?, val arr: ArrayList<String>, val type: ArrayList<String>): PagerAdapter()
{
    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return arr.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var v = ImageView(context)
        if (type[position] == "image/gif")
            Glide.with(context).asGif()
                    .load(arr[position])
                    .apply(RequestOptions()
                            .fitCenter())
                    .into(v)
        else
            Glide.with(context)
                    .load(arr[position])
                    .apply(RequestOptions()
                            .fitCenter())
                    .into(v)
        container.addView(v)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}
