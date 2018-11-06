package com.epitech.flatot.epicture.Adapter

import android.annotation.TargetApi
import android.app.Activity
import android.arch.lifecycle.AndroidViewModel
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.view.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.constraint.R.id.parent
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Interface.ILoadMore
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.Views.ZoomedActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.item_search_cardview.view.*
import kotlinx.android.synthetic.main.loading_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchAdapter(val myRecyclerView: RecyclerView, val access_token:String, val context: Context, val items:MutableList<ImgurInterface.ImgurSearchItem>) : RecyclerView.Adapter<SearchAdapter.MyViewHolder>()
{

    val VIEW_ITEMTYPE = 0
    val VIEW_LOADINGTYPE = 1
    internal var loadMore: ILoadMore? = null
    internal var isLoading: Boolean = false
    internal var visibleThreshold = 5
    internal var lastVisibleItem: Int = 0
    internal var totalItemCount: Int = 0

    init {
        val linearLayoutManager = myRecyclerView.layoutManager as LinearLayoutManager
        myRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold)
                    if (loadMore != null)
                        loadMore!!.OnLoadMore()
                isLoading = true
            }
        })
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SearchAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_search_cardview, p0, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setLoaded()
    {
        isLoading = false
    }

    fun setLoadMore(iLoadMore: ILoadMore)
    {
        this.loadMore = iLoadMore
    }

    internal class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var progressBar = itemView.progressBarSearch
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val item = items[p1]
        if (p0 is MyViewHolder) {
            p0.setData(item, p1)
            p0.setZoomedClick(item, p1)
        }
        else if (p0 is MyViewHolder) {
            p0.itemView.progressBarSearch.isIndeterminate = true
        }
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), Callback<ImgurInterface.FavoriteResult> {

        fun setZoomedClick (item: ImgurInterface.ImgurSearchItem?, pos: Int) {
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
            itemView.favorite2.setOnClickListener {
                val imgurApi = RetrofitInterface().createRetrofitBuilder()

                InverseFavoriteDrawable(item)
                val call = imgurApi.favoriteImage("Bearer " + access_token, item!!.data.images[0].id)
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
        fun InverseFavoriteDrawable(item: ImgurInterface.ImgurSearchItem?)
        {
            if (item!!.data.favorite)
                itemView.favorite2.background = context.getDrawable(android.R.drawable.star_big_off)
            else
                itemView.favorite2.background = context.getDrawable(android.R.drawable.star_big_on)
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun setData(item: ImgurInterface.ImgurSearchItem?, pos: Int)
        {
            if (item!!.data.images != null && !item!!.data.images.isEmpty())
                Picasso.with(context).load(item.data.images[0].link).into(itemView.img_imgur2)
            else
                Picasso.with(context).load(item!!.data.link).into(itemView.img_imgur2)

            if (item.data.favorite)
                itemView.favorite2.background = context.getDrawable(android.R.drawable.star_big_on)
            else
                itemView.favorite2.background = context.getDrawable(android.R.drawable.star_big_off)
        }
    }
}