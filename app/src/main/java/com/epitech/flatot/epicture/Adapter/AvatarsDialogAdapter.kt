package com.epitech.flatot.epicture.Adapter


import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Model.GlideModel
import com.epitech.flatot.epicture.Model.ImgurModel
import com.epitech.flatot.epicture.Model.RetrofitModel
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.item_avatar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AvatarsDialogAdapter(val access_token: String, val context: Context,
                           val items: MutableList<ImgurModel.Available_avatar>,
                           val c_username: String,
                           val myDialog: AlertDialog)://, var c_avatar: ImgurModel.Available_avatar):
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
        fun setPic(data: ImgurModel.Available_avatar, pos: Int)
        {
            GlideModel().displayGlide("image/jpeg", context, data.location, itemView.imgAvatar)
            itemView.setOnClickListener {
                var c_avatar = data.name
                val imgurApi = RetrofitModel().createRetrofitBuilder()
                val call = imgurApi.mySetAvatar("Bearer " + access_token, c_username, c_avatar)
                call.enqueue(object: Callback<ImgurModel.SetResult> {
                    override fun onFailure(call: Call<ImgurModel.SetResult>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                        myDialog.cancel()
                    }

                    override fun onResponse(call: Call<ImgurModel.SetResult>, response: Response<ImgurModel.SetResult>) {
                        if (response.isSuccessful)
                        {
                            Toast.makeText(context, "Avatar changed", Toast.LENGTH_SHORT).show()
                        }
                        myDialog.cancel()
                    }
                })
            }
        }
    }
}