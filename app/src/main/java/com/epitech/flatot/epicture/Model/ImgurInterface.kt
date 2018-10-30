package com.epitech.flatot.epicture.Model

import android.widget.ImageView

class ImgurInterface {

    data class Data(
            val id: String,
            val title: String,
            val description: String,
            val datetime: Long,
            val type: String,
            val animated: Boolean,
            val width: Int,
            val height: Int,
            val size: Long,
            val views: Int,
            val bandwidth: Int,
            val vote: String,
            val favorite: Boolean,
            val nsfw: String,
            val section: String,
            val account_url: String,
            val account_id: Long,
            val is_ad: Boolean,
            val has_sound: Boolean,
            val is_most_viral: Boolean,
            val tags: List<String>,
            val in_gallery: Boolean,
            val deletehash: String,
            val name: String,
            val link: String
    )

    data class Result (val data: List<Data>, val success: Boolean, val status: String)

    data class ImgurItem (val title: String, val imgImgur: String, val description: String)
}