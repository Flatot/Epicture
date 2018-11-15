package com.epitech.flatot.epicture.Views.FragmentBottom

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.epitech.flatot.epicture.Interface.ILoadMore
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Adapter.GalleryAdapter
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.dialog_filters.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), Callback<ImgurInterface.SearchResult>, ILoadMore {

    var items: MutableList<ImgurInterface.ImgurSearchItem>? = null
    var new_items: MutableList<ImgurInterface.ImgurSearchItem>? = null
    lateinit var adapter: GalleryAdapter

    var bool_album: Boolean = false

    var jpeg: Boolean = true
    var png: Boolean = true
    var gif: Boolean = true

    var last_week: Boolean = true
    var all_time: Boolean = true

    var sup_100: Boolean = true
    var inf_100: Boolean = true

    var _pages = 0

    override fun OnLoadMore() {
        if (new_items!!.size == items!!.size) {
            addAnotherPage()
        }
        val data = ImgurInterface.Data_search("null", "null", "null", 0, "null", false, 0, 0, 0, 0, 0, 0, false, false, "null", "null", "null", false, false, false, emptyList(), emptyList(), "null")
        val item = ImgurInterface.ImgurSearchItem(data)
        new_items!!.add(item)
        adapter.notifyItemInserted(new_items!!.size-1)
        Handler().postDelayed({
            new_items!!.removeAt(new_items!!.size-1)
            adapter.notifyItemRemoved(new_items!!.size)

            var count = new_items!!.size
            val need = count + 9

            while (count != items!!.size && count < need)
            {
                new_items!!.add(items!![count])
                count++
            }
            adapter.notifyDataSetChanged()
            adapter.setLoaded()
        }, 3000)
    }

    private fun addAnotherPage() {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        _pages++
        val call = imgurApi.getGallery("Bearer " + token, _pages, true, true, true)
        call.enqueue(object: Callback<ImgurInterface.SearchResult> {
            override fun onFailure(call: Call<ImgurInterface.SearchResult>, t: Throwable) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurInterface.SearchResult>, response: Response<ImgurInterface.SearchResult>) {
                try {
                    if (response.isSuccessful) {
                        val picLists = response.body()
                        picLists!!.data.forEach { pic ->
                            val item = ImgurInterface.ImgurSearchItem(pic)
                            items!!.add(item)
                        }
                    } else {
                        System.out.println(response.errorBody())

                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    companion object {
        fun newInstance(access_token: String): HomeFragment {
            val args = Bundle()
            args.putString("access_token", access_token)
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun get_first_items(items: MutableList<ImgurInterface.ImgurSearchItem>) {
        var count = 0

        while (count != items.size && count < 8)
        {
            new_items!!.add(items[count])
            count++
        }
    }

    fun getGallery()
    {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()

        val token = arguments?.getString("access_token")
        val call = imgurApi.getGallery("Bearer " + token, _pages, true, true, true)
        call.enqueue(this)
    }

    override fun onFailure(call: Call<ImgurInterface.SearchResult>, t: Throwable) {
        Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
    }

    override fun onResponse(call: Call<ImgurInterface.SearchResult>, response: Response<ImgurInterface.SearchResult>) {
        try {
            if (response.isSuccessful) {
                items = ArrayList()
                new_items = ArrayList()
                val picList = response.body()
                picList!!.data.forEach { pic ->
                    val item = ImgurInterface.ImgurSearchItem(pic)
                    if (getValidItem(item))
                        items!!.add(item)
                }
                get_first_items(items!!)
                val layoutManager = LinearLayoutManager(context) //StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                HomeRecyclerView.layoutManager = layoutManager
                adapter = GalleryAdapter(HomeRecyclerView, arguments?.getString("access_token")!!, context!!, new_items!!)
                HomeRecyclerView.adapter = adapter
                adapter.setLoadMore(this)
            } else {
                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                System.out.println(response.errorBody())
            }
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun ValidType(item: ImgurInterface.ImgurSearchItem): Boolean {
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

    fun ValidPeriod(item: ImgurInterface.ImgurSearchItem): Boolean {
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

    fun ValidViews(item: ImgurInterface.ImgurSearchItem): Boolean {
        if (sup_100 && inf_100)
            return (true)
        if (inf_100 && item.data.views < 100)
            return (true)
        if (sup_100 && item.data.views > 100)
            return (true)
        return (false)
    }

    fun getValidItem(item: ImgurInterface.ImgurSearchItem): Boolean {
        if (ValidViews(item) && ValidPeriod(item) && ValidType(item))
            return (true)
        else
            return (false)
    }

    fun checkTrue(customDialog: android.support.v7.app.AlertDialog) {
        if (inf_100) customDialog.view_100.setChecked(true) else customDialog.view_100.setChecked(false)
        if (sup_100) customDialog.view_500.setChecked(true) else customDialog.view_500.setChecked(false)
        if (png) customDialog.cb_png.setChecked(true) else customDialog.cb_png.setChecked(false)
        if (jpeg) customDialog.cb_jpeg.setChecked(true) else customDialog.cb_jpeg.setChecked(false)
        if (gif) customDialog.cb_gif.setChecked(true) else customDialog.cb_gif.setChecked(false)
        if (last_week) customDialog.cb_week.setChecked(true) else customDialog.cb_week.setChecked(false)
        if (all_time) customDialog.cb_single.setChecked(true) else customDialog.cb_single.setChecked(false)
        if (bool_album) customDialog.radio_album.setChecked(true) else customDialog.radio_album.setChecked(false)
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
        customDialog.radio_album.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) bool_album = true else bool_album = false
        }
    }

    fun leaveFilters(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.back.setOnClickListener {
            customDialog.hide()
        }
        customDialog.save.setOnClickListener {
            customDialog.hide()
            getGallery()
        }
    }

    fun openFilters(rootView: View, context: Context) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        try {
            setHasOptionsMenu(true)
            val toolbar = rootView.findViewById(R.id.home_toolbar) as android.support.v7.widget.Toolbar
            toolbar.setOnMenuItemClickListener {
                openFilters(rootView, context!!)
                true
            }
            toolbar.inflateMenu(R.menu.menu_filters)
            getGallery()
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view!!, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        try {
            activity!!.menuInflater.inflate(R.menu.menu_filters, menu)
            (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.filters) {
            openFilters(view!!, context!!)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
