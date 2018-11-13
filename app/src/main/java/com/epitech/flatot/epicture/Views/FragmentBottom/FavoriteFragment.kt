package com.epitech.flatot.epicture.Views.FragmentBottom


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.*
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.FavoriteAdapter
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Adapter.SearchAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface

import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.dialog_filters.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FavoriteFragment : Fragment(), Callback<ImgurInterface.GetFavoriteResult> {

    var items: MutableList<ImgurInterface.ImgurFavoriteItem>? = null

    var jpeg_fav: Boolean = true
    var png_fav: Boolean = true
    var gif_fav: Boolean = true

    var last_week_fav: Boolean = true
    var all_time_fav: Boolean = true

    var sup_100_fav: Boolean = true
    var inf_100_fav: Boolean = true


    companion object {
        fun newInstance(access_token: String): FavoriteFragment {
            val args = Bundle()
            args.putString("access_token", access_token)
            val fragment = FavoriteFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun ValidTypeFav(item: ImgurInterface.ImgurFavoriteItem): Boolean {
        if (png_fav && gif_fav && jpeg_fav)
            return (true)
        if (png_fav && item.data.type == "image/png")
            return (true)
        if (jpeg_fav && item.data.type == "image/jpeg")
            return (true)
        if (gif_fav && item.data.type == "image/gif")
            return (true)
        return (false)
    }

    fun ValidPeriodFav(item: ImgurInterface.ImgurFavoriteItem): Boolean {
        val myDate = Date()
        val sevenDay = Date(myDate.getTime() - 604800000L) // 7 * 24 * 60 * 60 * 1000
        val valueSeven = sevenDay.getTime() / 1000
        if (last_week_fav && all_time_fav)
            return (true)
        if (last_week_fav && item.data.datetime >= valueSeven)
            return (true)
        if (all_time_fav)
            return (true)
        return (false)
    }

    fun ValidViewsFav(item: ImgurInterface.ImgurFavoriteItem): Boolean {
        if (sup_100_fav && inf_100_fav)
            return (true)
        if (inf_100_fav && item.data.views < 100)
            return (true)
        if (sup_100_fav && item.data.views > 100)
            return (true)
        return (false)
    }

    fun getValidItemFav(item: ImgurInterface.ImgurFavoriteItem): Boolean {
        if (ValidViewsFav(item) && ValidPeriodFav(item) && ValidTypeFav(item))
            return (true)
        else
            return (false)
    }

    fun getFavorites()
    {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()

        val token = arguments?.getString("access_token")
        val call = imgurApi.getFavorite("Bearer " + token)
        call.enqueue(this)
    }

    override fun onFailure(call: Call<ImgurInterface.GetFavoriteResult>, t: Throwable) {
        println(t.message)
        Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
    }

    override fun onResponse(call: Call<ImgurInterface.GetFavoriteResult>, response: Response<ImgurInterface.GetFavoriteResult>) {
        if (response.isSuccessful) {
            val picList = response.body()
            items = ArrayList()
            picList!!.data.forEach {
                pic ->
                val item = ImgurInterface.ImgurFavoriteItem(pic)
                if (getValidItemFav(item))
                    items!!.add(item)
            }
            val layoutManager = LinearLayoutManager(context)
            FavoriteRecyclerView.layoutManager = layoutManager
            val adapter = FavoriteAdapter(context!!, items!!)
            FavoriteRecyclerView.adapter = adapter
        } else {
            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
            System.out.println(response.errorBody())
        }
    }

    fun checkTrueFav(customDialog: android.support.v7.app.AlertDialog) {
        if (inf_100_fav === true) customDialog.view_100.setChecked(true) else customDialog.view_100.setChecked(false)
        if (sup_100_fav === true) customDialog.view_500.setChecked(true) else customDialog.view_500.setChecked(false)
        if (png_fav === true) customDialog.cb_png.setChecked(true) else customDialog.cb_png.setChecked(false)
        if (jpeg_fav === true) customDialog.cb_jpeg.setChecked(true) else customDialog.cb_jpeg.setChecked(false)
        if (gif_fav === true) customDialog.cb_gif.setChecked(true) else customDialog.cb_gif.setChecked(false)
        if (last_week_fav === true) customDialog.cb_week.setChecked(true) else customDialog.cb_week.setChecked(false)
        if (all_time_fav === true) customDialog.cb_single.setChecked(true) else customDialog.cb_single.setChecked(false)
    }

    fun getCheckboxFav(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.view_100.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) inf_100_fav = true else inf_100_fav = false
        }
        customDialog.view_500.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) sup_100_fav = true else sup_100_fav = false
        }
        customDialog.cb_single.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) all_time_fav = true else all_time_fav = false
        }
        customDialog.cb_week.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) last_week_fav = true else last_week_fav = false
        }
        customDialog.cb_gif.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) gif_fav = true else gif_fav = false
        }
        customDialog.cb_jpeg.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) jpeg_fav = true else jpeg_fav = false
        }
        customDialog.cb_png.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) png_fav = true else png_fav = false
        }
    }

    fun leaveFiltersFav(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.back.setOnClickListener {
            customDialog.hide()
        }
        customDialog.save.setOnClickListener {
            customDialog.hide()
            getFavorites()
        }
    }

    fun openFiltersFav(rootView: View, context: Context) { //jpeg, png, gif && all time, last week && views
        val myDialog = android.support.v7.app.AlertDialog.Builder(context)
        val myDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_fav_filters, null)
        myDialog.setView(myDialogView)
        //myDialog.setCancelable(false)
        val customDialog = myDialog.create()
        customDialog.show()
        checkTrueFav(customDialog)
        getCheckboxFav(customDialog)
        leaveFiltersFav(customDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        setHasOptionsMenu(true)
        val toolbar = rootView.findViewById(R.id.fav_toolbar) as android.support.v7.widget.Toolbar
        toolbar.setOnMenuItemClickListener {
            openFiltersFav(rootView, context!!)
            true
        }
        toolbar.inflateMenu(R.menu.menu_filters)
        if (items != null && items!!.isNotEmpty())
        {
            val layoutManager = LinearLayoutManager(context) //StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            rootView.FavoriteRecyclerView?.layoutManager = layoutManager
            val adapter = FavoriteAdapter(context!!, items!!)
            rootView.FavoriteRecyclerView?.adapter = adapter
        }
        else
            getFavorites()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view!!, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        activity!!.menuInflater.inflate(R.menu.menu_filters, menu)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //super.onCreateOptionsMenu(menu, inflater)
        //menuInflater.inflate(R.menu.menu_filters, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.filters) {
            openFiltersFav(view!!, context!!)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
