package com.epitech.flatot.epicture.Model

import android.content.Context
import android.content.Intent
import com.epitech.flatot.epicture.Views.ZoomedActivity
import kotlinx.android.synthetic.main.item_cardview.view.*

class ZoomedActivityInterface {

    fun setZoomed(context: Context, item: ImgurInterface.ImgurItem)
    {
        val intent = Intent(context, ZoomedActivity::class.java)
        intent.putExtra("title", item!!.data.title)
        intent.putExtra("img_imgur", item.data.link)
        intent.putExtra("description", item.data.description)
        intent.putExtra("type", item.data.type)
        context.startActivity(intent)
    }

    fun setZoomed(context: Context, item: ImgurInterface.ImgurFavoriteItem)
    {
        val intent = Intent(context, ZoomedActivity::class.java)
        intent.putExtra("title", item!!.data.title)
        intent.putExtra("img_imgur", item.data.link)
        intent.putExtra("description", item.data.description)
        intent.putExtra("type", item.data.type)
        if (item.data.images != null) {
            var list_link: MutableList<String> = ArrayList()
            item.data.images.forEach { img ->
                list_link.add(img.link)
            }
            var list_description: MutableList<String> = ArrayList()
            item.data.images.forEach { img ->
                list_description.add(img.description)
            }
            var list_type: MutableList<String> = ArrayList()
            item.data.images.forEach { img ->
                list_type.add(img.type)
            }
            intent.putStringArrayListExtra("list_type", list_type as ArrayList<String>)
            intent.putStringArrayListExtra("list_link", list_link as ArrayList<String>)
            intent.putStringArrayListExtra("list_description", list_description as ArrayList<String>)
        }
        context.startActivity(intent)
    }

    fun setZoomed(context: Context, item: ImgurInterface.ImgurSearchItem)
    {

        val intent = Intent(context, ZoomedActivity::class.java)
        intent.putExtra("title", item!!.data.title)
        intent.putExtra("img_imgur", item.data.link)
        intent.putExtra("description", item.data.description)
        intent.putExtra("type", item.data.type)
        if (item.data.images != null) {
            var list_link: MutableList<String> = ArrayList()
            item.data.images.forEach { img ->
                list_link.add(img.link)
            }
            var list_description: MutableList<String> = ArrayList()
            item.data.images.forEach { img ->
                list_description.add(img.description)
            }
            var list_type: MutableList<String> = ArrayList()
            item.data.images.forEach { img ->
                list_type.add(img.type)
            }
            intent.putStringArrayListExtra("list_type", list_type as ArrayList<String>)
            intent.putStringArrayListExtra("list_link", list_link as ArrayList<String>)
            intent.putStringArrayListExtra("list_description", list_description as ArrayList<String>)
        }
        context.startActivity(intent)
    }
}