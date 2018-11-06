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
import kotlinx.android.synthetic.main.dialog_template.*
import kotlinx.android.synthetic.main.item_linear.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DialogAdapter(val access_token: String, val context: Context, val items: ArrayList<String>,
                    val ids: ArrayList<String>, val item: ImgurInterface.ImgurItem?,
                    val myDialog: AlertDialog):
        RecyclerView.Adapter<DialogAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DialogAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_linear, p0, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: DialogAdapter.MyViewHolder, p1: Int) {
        val data = items[p1]
        p0.setText(data, p1)
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun setText(data: String, pos: Int)
        {
            itemView.itemDialogAddTxt.text = data
            itemView.setOnClickListener {
                val idsBody = RequestBody.create(okhttp3.MultipartBody.FORM, item!!.data.id)
                val optinalBodyMap = mapOf("ids" to idsBody)

                val retrofit = RetrofitInterface().createRetrofitBuilder()
                val call = retrofit.addToAlbum("Bearer " + access_token, ids[pos], optinalBodyMap)

                call.enqueue(object : Callback<ImgurInterface.AddAlbumResult> {
                    override fun onFailure(call: Call<ImgurInterface.AddAlbumResult>, t: Throwable) {
                        Toast.makeText(context, "Fail to add to the album", Toast.LENGTH_SHORT).show()
                        myDialog.cancel()
                    }

                    override fun onResponse(call: Call<ImgurInterface.AddAlbumResult>, response: Response<ImgurInterface.AddAlbumResult>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()
                        }
                        myDialog.cancel()
                    }
                })
            }
        }
    }
}