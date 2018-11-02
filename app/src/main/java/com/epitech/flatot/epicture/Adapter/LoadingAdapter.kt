package com.epitech.flatot.epicture.Adapter

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.ImgurInterface.ImgurItem
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.Views.ZoomedActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_cardview.view.*
import kotlinx.android.synthetic.main.item_search_cardview.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadingAdapter(val access_token:String, val context: Context, val items:MutableList<ImgurItem>) : RecyclerView.Adapter<LoadingAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LoadingAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cardview, p0, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val item = items[p1]
        p0.setData(item, p1)
        p0.setZoomedClick(item, p1)
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), Callback<ImgurInterface.FavoriteResult> {

        fun setZoomedClick (item: ImgurItem?, pos: Int) {
            itemView.setOnClickListener {
                val intent = Intent(context, ZoomedActivity::class.java)
                intent.putExtra("title", item!!.data.title)
                intent.putExtra("img_imgur", item.data.link)
                intent.putExtra("description", item.data.description)
                context.startActivity(intent)
            }

            itemView.favorite.setOnClickListener {
                val imgurApi = RetrofitInterface().createRetrofitBuilder()
                val call = imgurApi.favoriteImage("Bearer " + access_token, item!!.data.id)
                InverseFavoriteDrawable(item)
                call.enqueue(this)
            }
        }

        override fun onFailure(call: Call<ImgurInterface.FavoriteResult>, t: Throwable) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(call: Call<ImgurInterface.FavoriteResult>, response: Response<ImgurInterface.FavoriteResult>) {
            if (!response.isSuccessful)
                Toast.makeText(context, "Failed to favorite this picture !", Toast.LENGTH_SHORT).show()
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun InverseFavoriteDrawable(item: ImgurInterface.ImgurItem?)
        {
            if (item!!.data.favorite)
                itemView.favorite.background = context.getDrawable(android.R.drawable.star_big_off)
            else
                itemView.favorite.background = context.getDrawable(android.R.drawable.star_big_on)
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun setData(item: ImgurItem?, pos: Int)
        {
            //itemView.title.text = item!!.title
            //itemView.description.text = item!!.description
            Picasso.with(context).load(item!!.data.link).into(itemView.img_imgur)
            if (item.data.favorite)
                itemView.favorite.background = context.getDrawable(android.R.drawable.star_big_on)
            else
                itemView.favorite.background = context.getDrawable(android.R.drawable.star_big_off)
        }
    }
}