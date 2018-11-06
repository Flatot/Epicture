package com.epitech.flatot.epicture.Adapter

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
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
import kotlinx.android.synthetic.main.dialog_add_to_album.*
import kotlinx.android.synthetic.main.dialog_template.*
import kotlinx.android.synthetic.main.item_cardview.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
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

            itemView.add.setOnClickListener {
                showPictureDialog(item)
            }
        }

        private fun choosePrivacyAlbum(item: ImgurItem?) {
            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Select album's privacy")
            val pictureDialogItems = arrayOf("Hidden", "Public")
            pictureDialog.setItems(pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    0 -> create_album(item, "hidden")
                    1 -> create_album(item, "public")
                }
            }
            pictureDialog.show()
        }

        private fun create_album(item: ImgurItem?, privacy: String) {

            val myDialog = AlertDialog.Builder(context)
            val myDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_template, null)
            myDialog.setView(myDialogView)
            //myDialog.setCancelable(false)
            val customDialog = myDialog.create()
            customDialog.show()
            customDialog.dialogSave.setOnClickListener {
                callApiAlbum(item, privacy, customDialog)
            }
        }

        fun callApiAlbum(item: ImgurItem?, privacy: String, myDialog: AlertDialog)
        {
            val titleBody = RequestBody.create(okhttp3.MultipartBody.FORM, myDialog.dialogTitle.text.toString())
            val privacyBody = RequestBody.create(MediaType.parse("text/plain"), privacy)
            val idsBody = RequestBody.create(MediaType.parse("text/plain"), item!!.data.id)
            val descriptionBody = RequestBody.create(okhttp3.MultipartBody.FORM, myDialog.dialogDescription.text.toString())
            val optinalBodyMap = mapOf("ids" to idsBody, "title" to titleBody, "description" to descriptionBody, "privacy" to privacyBody)

            val retrofit = RetrofitInterface().createRetrofitBuilder()
            val call = retrofit.createAlbum("Bearer " + access_token, optinalBodyMap)

            call.enqueue(object : Callback<ImgurInterface.UploadResult> {
                override fun onFailure(call: Call<ImgurInterface.UploadResult>, t: Throwable) {
                    Toast.makeText(context, "Fail to create album", Toast.LENGTH_SHORT).show()
                    myDialog.cancel()
                }

                override fun onResponse(call: Call<ImgurInterface.UploadResult>, response: Response<ImgurInterface.UploadResult>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Created Successfully", Toast.LENGTH_SHORT).show()
                    }
                    myDialog.cancel()
                }
            })
        }

        fun showPictureDialog(item: ImgurItem?) {
            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Create Album", "Add to album")
            pictureDialog.setItems(pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    0 -> choosePrivacyAlbum(item)
                    1 -> addToAlbum(item)
                }
            }
            pictureDialog.show()
        }

        fun addToAlbum(item: ImgurItem?)
        {
            val retrofit = RetrofitInterface().createRetrofitBuilder()
            val call = retrofit.getAlbums("Bearer " + access_token)

            call.enqueue(object : Callback<ImgurInterface.Result> {
                override fun onFailure(call: Call<ImgurInterface.Result>, t: Throwable) {
                    println(t.message)
                }

                override fun onResponse(call: Call<ImgurInterface.Result>, response: Response<ImgurInterface.Result>) {
                    if (response.isSuccessful) {
                        val dialogItems = ArrayList<String>()
                        val dialogIds = ArrayList<String>()
                        val body = response.body()
                        body!!.data.forEach {
                            item ->
                            dialogIds.add(item.id)
                            dialogItems.add(item.title)
                        }
                        val myDialog = AlertDialog.Builder(context)
                        val myDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_album, null)
                        myDialog.setView(myDialogView)
                        //myDialog.setCancelable(false)
                        val customDialog = myDialog.create()
                        customDialog.show()
                        customDialog.dialogRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        val adapter = DialogAdapter(access_token, context, dialogItems, dialogIds, item, customDialog)
                        customDialog.dialogRecyclerView.adapter = adapter
                    }
                }
            })
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
            Picasso.with(context).load(item!!.data.link).into(itemView.img_imgur)
            if (item.data.favorite)
                itemView.favorite.background = context.getDrawable(android.R.drawable.star_big_on)
            else
                itemView.favorite.background = context.getDrawable(android.R.drawable.star_big_off)
        }
    }
}