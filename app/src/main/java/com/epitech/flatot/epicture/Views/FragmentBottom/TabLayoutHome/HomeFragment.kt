package com.epitech.flatot.epicture.Views.FragmentBottom.TabLayoutHome

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.asynclayoutinflater.R.id.title
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TabHost
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Adapter.TabLayoutAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_filters.*
import kotlinx.android.synthetic.main.dialog_template.*
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), Callback<ImgurInterface.Result> {

    var items: MutableList<ImgurInterface.ImgurItem>? = null

    var jpeg: Boolean = true
    var png: Boolean = true
    var gif: Boolean = true

    var last_week: Boolean = true
    var all_time: Boolean = true

    var sup_100: Boolean = true
    var inf_100: Boolean = true


    companion object {
        fun newInstance(access_token: String): HomeFragment {
            val args = Bundle()
            args.putString("access_token", access_token)
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun getAlbums()
    {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()

        val token = arguments?.getString("access_token")
        val call = imgurApi.getUser("Bearer " + token)
        call.enqueue(this)
    }

    override fun onFailure(call: Call<ImgurInterface.Result>, t: Throwable) {
        Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
    }

    fun ValidType(item: ImgurInterface.ImgurItem): Boolean {
        if (png && gif && jpeg)
            return (true)
        if (png && item.data.type == "image/png")
            return (true)
        if (jpeg && item.data.type == "image/jpeg")
            return (true)
        if (gif && item.data.type == "image/gif")
            return (true)
        return (false)
    }

    fun ValidPeriod(item: ImgurInterface.ImgurItem): Boolean {
        val myDate = Date()
        val sevenDay = Date(myDate.getTime() - 604800000L) // 7 * 24 * 60 * 60 * 1000
        val valueSeven = sevenDay.getTime() / 1000
        if (last_week && all_time)
            return (true)
        if (last_week && item.data.datetime >= valueSeven)
            return (true)
        if (all_time)
            return (true)
        return (false)
    }

    fun ValidViews(item: ImgurInterface.ImgurItem): Boolean {
        if (sup_100 && inf_100)
            return (true)
        if (inf_100 && item.data.views < 100)
            return (true)
        if (sup_100 && item.data.views > 100)
            return (true)
        return (false)
    }

    fun getValidItem(item: ImgurInterface.ImgurItem): Boolean {
        if (ValidViews(item) && ValidPeriod(item) && ValidType(item))
            return (true)
        else
            return (false)
    }

    override fun onResponse(call: Call<ImgurInterface.Result>, response: Response<ImgurInterface.Result>) {
        if (response.isSuccessful) {
            items = ArrayList()
            val picList = response.body()
            picList!!.data.forEach {
                pic ->
                val item = ImgurInterface.ImgurItem(pic)
                if (getValidItem(item))
                    items!!.add(item)
            }
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            HomeRecyclerView.layoutManager = layoutManager
            val adapter = LoadingAdapter(arguments?.getString("access_token")!!, context!!, items!!)
            HomeRecyclerView.adapter = adapter
        } else {
            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
            System.out.println(response.errorBody())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun checkTrue(customDialog: android.support.v7.app.AlertDialog) {
        if (inf_100) customDialog.view_100.setChecked(true) else customDialog.view_100.setChecked(false)
        if (sup_100) customDialog.view_500.setChecked(true) else customDialog.view_500.setChecked(false)
        if (png) customDialog.cb_png.setChecked(true) else customDialog.cb_png.setChecked(false)
        if (jpeg) customDialog.cb_jpeg.setChecked(true) else customDialog.cb_jpeg.setChecked(false)
        if (gif) customDialog.cb_gif.setChecked(true) else customDialog.cb_gif.setChecked(false)
        if (last_week) customDialog.cb_week.setChecked(true) else customDialog.cb_week.setChecked(false)
        if (all_time) customDialog.cb_single.setChecked(true) else customDialog.cb_single.setChecked(false)
    }

    fun getCheckbox(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.view_100.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) inf_100 = true else inf_100 = false
        }
        customDialog.view_500.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) sup_100 = true else sup_100 = false
        }
        customDialog.cb_single.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) all_time = true else all_time = false
        }
        customDialog.cb_week.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) last_week = true else last_week = false
        }
        customDialog.cb_gif.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) gif = true else gif = false
        }
        customDialog.cb_jpeg.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) jpeg = true else jpeg = false
        }
        customDialog.cb_png.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) png = true else png = false
        }
    }

    fun leaveFilters(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.back.setOnClickListener {
            customDialog.hide()
        }
        customDialog.save.setOnClickListener {
            customDialog.hide()
            getAlbums()
        }
    }

    fun openFilters(rootView: View, context: Context) { //jpeg, png, gif && all time, last week && views
        rootView.header_filters.setOnClickListener {
            val myDialog = android.support.v7.app.AlertDialog.Builder(context)
            val myDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_filters, null)
            myDialog.setView(myDialogView)
            //myDialog.setCancelable(false)
            val customDialog = myDialog.create()
            customDialog.show()
            checkTrue(customDialog)
            getCheckbox(customDialog)
            leaveFilters(customDialog)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        if (items != null && items!!.isNotEmpty())
        {
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            rootView.HomeRecyclerView.layoutManager = layoutManager
            val adapter = LoadingAdapter(arguments?.getString("access_token")!!, context!!, items!!)
            rootView.HomeRecyclerView.adapter = adapter
        }
        else
            getAlbums()
        /*val galFrag = GalleryFragment.newInstance(arguments?.getString("access_token")!!)
        AlbumsFragment.newInstance(arguments?.getString("access_token")!!)
        val fm = TabLayoutAdapter(arguments?.getString("access_token")!!, galFrag, activity!!.supportFragmentManager)
        rootView.viewpager_main.adapter = fm
        rootView.tabLayout.setupWithViewPager(viewpager_main)*/

        openFilters(rootView, context!!)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view!!, savedInstanceState)
    }
}
