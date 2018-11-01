package com.epitech.flatot.epicture.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epitech.flatot.epicture.Model.ImgurInterface.ImgurItem
import com.epitech.flatot.epicture.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_cardview.view.*

class LoadingAdapter(val context: Context, val items:MutableList<ImgurItem>) : RecyclerView.Adapter<LoadingAdapter.MyViewHolder>()
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
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun setData(item: ImgurItem?, pos: Int)
        {
            itemView.title.text = item!!.title
            itemView.description.text = item!!.description
            Picasso.with(context).load(item!!.imgImgur).into(itemView.img_imgur)
        }
    }
}