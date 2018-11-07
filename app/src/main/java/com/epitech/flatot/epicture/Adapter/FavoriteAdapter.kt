package com.epitech.flatot.epicture.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.Views.ZoomedActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_cardview.view.*
import kotlinx.android.synthetic.main.item_favorite_cardview.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteAdapter(val context: Context, val items:MutableList<ImgurInterface.ImgurFavoriteItem>) : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FavoriteAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_favorite_cardview, p0, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val item = items[p1]
        if (p0.setData(item, p1))
            p0.setZoomedClick(item, p1)
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), Callback<ImgurInterface.FavoriteResult> {

        fun setZoomedClick (item: ImgurInterface.ImgurFavoriteItem?, pos: Int) {
            itemView.setOnClickListener {
                val intent = Intent(context, ZoomedActivity::class.java)
                intent.putExtra("title", item!!.data.title)
                intent.putExtra("img_imgur", item.data.link)
                intent.putExtra("description", item.data.description)
                if (item.data.images != null) {
                    var list_link: MutableList<String> = ArrayList()
                    item.data.images.forEach { img ->
                        list_link.add(img.link)
                    }
                    var list_description: MutableList<String> = ArrayList()
                    item.data.images.forEach { img ->
                        list_description.add(img.description)
                    }
                    intent.putStringArrayListExtra("list_link", list_link as ArrayList<String>)
                    intent.putStringArrayListExtra("list_description", list_description as ArrayList<String>)
                }
                context.startActivity(intent)
            }
        }

        override fun onFailure(call: Call<ImgurInterface.FavoriteResult>, t: Throwable) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(call: Call<ImgurInterface.FavoriteResult>, response: Response<ImgurInterface.FavoriteResult>) {
            if (!response.isSuccessful)
                Toast.makeText(context, "Failed to favorite this picture !", Toast.LENGTH_SHORT).show()
        }

        fun setData(item: ImgurInterface.ImgurFavoriteItem?, pos: Int) : Boolean
        {
            if (itemView.img_imgur.drawable == null) {
                if (item!!.data.images != null && !item!!.data.images.isEmpty()) {
                    if (item!!.data.images[0].type == "image/gif")
                        Glide.with(context).asGif()
                                .load(item.data.images[0].link)
                                .apply(RequestOptions()
                                        .fitCenter())
                                .into(itemView.favoriteImg)
                    else
                        Picasso.with(context).load(item.data.images[0].link)
                                .into(itemView.favoriteImg)
                } else {
                    if (item!!.data.type == "image/gif")
                        Glide.with(context).asGif()
                                .load(item!!.data.link)
                                .apply(RequestOptions()
                                        .fitCenter())
                                .into(itemView.favoriteImg)
                    else
                        Picasso.with(context).load(item!!.data.link)
                                .into(itemView.favoriteImg)
                }
                return true
            }
            return false
        }
    }
}