package com.epitech.flatot.epicture.Adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.epitech.flatot.epicture.Views.FragmentBottom.HomeFragment
import com.epitech.flatot.epicture.Interface.ILoadMore
import com.epitech.flatot.epicture.Model.ImgurInterface.ImgurItem
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.item_cardview.view.*
import kotlinx.android.synthetic.main.loading_layout.view.*

internal class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
{
    var progressBar = itemView.progressBar!!
}

internal class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
{
    var description = itemView.description!!
    var title = itemView.title!!
    var imageImgur = itemView.img_imgur!!
}

class LoadingAdapter(recyclerView: RecyclerView, internal var activity: HomeFragment, internal var items:MutableList<ImgurItem?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if (p1 == VIEW_ITEMTYPE)
        {
            val view = activity.layoutInflater
                    .inflate(R.layout.item_cardview, p0, false)
            return ItemViewHolder(view)
        }
        else if (p1 == VIEW_LOADINGTYPE)
        {
            val view = activity.layoutInflater
                    .inflate(R.layout.loading_layout, p0, false)
            return ItemViewHolder(view)
        }
        //TODO remove that return
        return ItemViewHolder(p0)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (p0 is ItemViewHolder)
        {
            val item = items[p1]
            p0.title.text = items[p1]!!.title
            p0.description.text = items[p1]!!.description
            p0.imageImgur.text = items[p1]!!.imgImgur
        }
        else if (p0 is LoadingViewHolder)
        {
            p0.progressBar.isIndeterminate = true
        }
    }

    fun setLoaded()
    {
        isLoading = false
    }

    fun setLoadMore()
    {
        val iLoadMore = this.loadMore
        loadMore = iLoadMore
    }

    val VIEW_ITEMTYPE=0
    val VIEW_LOADINGTYPE=1

    internal var loadMore: ILoadMore?=null
    internal var isLoading: Boolean?=false
    internal var visibleThreshold=5
    internal var lastVisibleItem:Int=0
    internal var totalItemCount:Int=0

    init {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading!! && totalItemCount <= lastVisibleItem+visibleThreshold)
                    if (loadMore != null)
                        loadMore!!.OnLoadMore()
                isLoading = true
            }
        })

    }
}
