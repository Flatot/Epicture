package com.epitech.flatot.epicture.Views.FragmentBottom

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
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

    var bool_viral: Boolean = true
    var bool_mature: Boolean = false

    var _section: String = "hot"
    var _sort: String = "viral"

    var cb_hot: Boolean = true
    var cb_top: Boolean = false
    var cb_user: Boolean = false

    var cb_viral: Boolean = true
    var cb_top_sort: Boolean = false
    var cb_time: Boolean = false

    var cb_show_mature: Boolean = false
    var cb_show_viral: Boolean = true

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
        val call = imgurApi.getGallery("Bearer " + token, _section, _sort, _pages, bool_viral, bool_mature, true)
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
        val call = imgurApi.getGallery("Bearer " + token, _section, _sort, _pages, bool_viral, bool_mature, true)
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

    fun checkTrue(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.cb_hot.isChecked = cb_hot
        customDialog.cb_top.isChecked = cb_top
        customDialog.cb_user.isChecked = cb_user
        customDialog.cb_viral.isChecked = cb_viral
        customDialog.cb_top_sort.isChecked = cb_top_sort
        customDialog.cb_time.isChecked = cb_time
        customDialog.cb_show_mature.isChecked = cb_show_mature
        customDialog.cb_show_viral.isChecked = cb_show_viral
    }

    fun getCheckbox(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.cb_hot.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cb_hot = true
                customDialog.cb_user.isChecked = false
                customDialog.cb_top.isChecked = false
                cb_user = false
                cb_top = false
            } else cb_hot = false
        }
        customDialog.cb_top.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cb_top = true
                customDialog.cb_user.isChecked = false
                customDialog.cb_hot.isChecked = false
                cb_hot = false
                cb_user = false
            } else cb_top = false
        }
        customDialog.cb_user.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cb_user = true
                customDialog.cb_top.isChecked = false
                customDialog.cb_hot.isChecked = false
                cb_hot = false
                cb_top = false
            } else cb_user = false
        }
        customDialog.cb_viral.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cb_viral = true
                customDialog.cb_time.isChecked = false
                customDialog.cb_top_sort.isChecked = false
                cb_time = false
                cb_top_sort = false
            } else cb_viral = false
        }
        customDialog.cb_top_sort.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cb_top_sort = true
                customDialog.cb_time.isChecked = false
                customDialog.cb_viral.isChecked = false
                cb_time = false
                cb_viral = false
            } else cb_top_sort = false
        }
        customDialog.cb_time.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cb_time = true
                customDialog.cb_top_sort.isChecked = false
                customDialog.cb_viral.isChecked = false
                cb_top_sort = false
                cb_viral = false
            } else cb_time = false
        }
        customDialog.cb_show_viral.setOnCheckedChangeListener { buttonView, isChecked ->
            cb_show_viral = isChecked
        }
        customDialog.cb_show_mature.setOnCheckedChangeListener { buttonView, isChecked ->
            cb_show_mature = isChecked
        }
    }

    fun changeGlobImgur()
    {
        if (cb_user)
            _section = "user"
        else if (cb_top)
            _section = "top"
        else
            _section = "hot"

        if (cb_time)
            _sort = "time"
        else if (cb_top_sort)
            _sort = "top"
        else
            _sort = "viral"
        bool_viral = cb_show_viral
        bool_mature = cb_show_mature
    }

    fun leaveFilters(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.backHome.setOnClickListener {
            customDialog.hide()
        }
        customDialog.saveHome.setOnClickListener {
            customDialog.hide()
            changeGlobImgur()
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
