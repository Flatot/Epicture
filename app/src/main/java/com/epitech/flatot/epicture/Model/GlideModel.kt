package com.epitech.flatot.epicture.Model

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class GlideModel {

    fun displayGlide(type: String, context: Context, url: String, img: ImageView)
    {
        if (type == "image/gif")
            Glide.with(context).asGif()
                    .load(url)
                    .apply(RequestOptions()
                            .fitCenter())
                    .into(img)
        else
            Glide.with(context)
                    .load(url)
                    .apply(RequestOptions()
                            .fitCenter())
                    .into(img)
    }
}