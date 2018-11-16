package com.epitech.flatot.epicture.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Model.GlideModel
import com.epitech.flatot.epicture.Model.ImgurModel
import com.epitech.flatot.epicture.Model.ZoomedActivityModel
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.item_favorite_cardview.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteAdapter(val context: Context, val items:MutableList<ImgurModel.ImgurFavoriteItem>) : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>()
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

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), Callback<ImgurModel.FavoriteResult> {

        fun setZoomedClick (item: ImgurModel.ImgurFavoriteItem?, pos: Int) {
            itemView.setOnClickListener {
                ZoomedActivityModel().setZoomed(context, item!!)
            }
        }

        override fun onFailure(call: Call<ImgurModel.FavoriteResult>, t: Throwable) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(call: Call<ImgurModel.FavoriteResult>, response: Response<ImgurModel.FavoriteResult>) {
            if (!response.isSuccessful)
                Toast.makeText(context, "Failed to favorite this picture !", Toast.LENGTH_SHORT).show()
        }

        fun setData(item: ImgurModel.ImgurFavoriteItem?, pos: Int) : Boolean
        {
            if (item!!.data.images != null && !item!!.data.images.isEmpty()) {
                GlideModel().displayGlide(item!!.data.images[0].type, context, item.data.images[0].link, itemView.favoriteImg)
            } else {
                GlideModel().displayGlide(item!!.data.type, context, item!!.data.link, itemView.favoriteImg)
            }
            return true
        }
    }
}