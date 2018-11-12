package com.epitech.flatot.epicture.Adapter

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_template.*
import kotlinx.android.synthetic.main.item_avatar.view.*
import kotlinx.android.synthetic.main.item_linear.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AvatarsDialogAdapter(val access_token: String, val context: Context,
                           val items: MutableList<ImgurInterface.Available_avatar>,
                           val myDialog: AlertDialog)://, var c_avatar: ImgurInterface.Available_avatar):
        RecyclerView.Adapter<AvatarsDialogAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AvatarsDialogAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_avatar, p0, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: AvatarsDialogAdapter.MyViewHolder, p1: Int) {
        val data = items[p1]
        p0.setPic(data, p1)
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun setPic(data: ImgurInterface.Available_avatar, pos: Int)
        {
            Picasso.with(context).load(data.location).into(itemView.imgAvatar)
            itemView.setOnClickListener {
                //c_avatar = data
                myDialog.cancel()
            }
        }
    }
}