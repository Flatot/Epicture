package com.epitech.flatot.epicture.Views.FragmentBottom


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.FavoriteAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.dialog_fav_filters.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*
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
        try {
            if (response.isSuccessful) {
                val picList = response.body()
                items = ArrayList()
                picList!!.data.forEach { pic ->
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
        catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun checkTrueFav(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.view_100.isChecked = inf_100_fav
        customDialog.view_500.isChecked = sup_100_fav
        customDialog.cb_png.isChecked = png_fav
        customDialog.cb_jpeg.isChecked = jpeg_fav
        customDialog.cb_gif.isChecked = gif_fav
        customDialog.cb_week.isChecked = last_week_fav
        customDialog.cb_single.isChecked = all_time_fav
    }

    fun getCheckboxFav(customDialog: android.support.v7.app.AlertDialog) {
        customDialog.view_100.setOnCheckedChangeListener { buttonView, isChecked ->
            inf_100_fav = isChecked
        }
        customDialog.view_500.setOnCheckedChangeListener { buttonView, isChecked ->
            sup_100_fav = isChecked
        }
        customDialog.cb_single.setOnCheckedChangeListener { buttonView, isChecked ->
            all_time_fav = isChecked
        }
        customDialog.cb_week.setOnCheckedChangeListener { buttonView, isChecked ->
            last_week_fav = isChecked
        }
        customDialog.cb_gif.setOnCheckedChangeListener { buttonView, isChecked ->
            gif_fav = isChecked
        }
        customDialog.cb_jpeg.setOnCheckedChangeListener { buttonView, isChecked ->
            jpeg_fav = isChecked
        }
        customDialog.cb_png.setOnCheckedChangeListener { buttonView, isChecked ->
            png_fav = isChecked
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
        try {
            setHasOptionsMenu(true)
            val toolbar = rootView.findViewById(R.id.fav_toolbar) as android.support.v7.widget.Toolbar
            toolbar.setOnMenuItemClickListener {
                openFiltersFav(rootView, context!!)
                true
            }
            toolbar.inflateMenu(R.menu.menu_filters)
            if (items != null && items!!.isNotEmpty()) {
                val layoutManager = LinearLayoutManager(context) //StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

                rootView.FavoriteRecyclerView?.layoutManager = layoutManager
                val adapter = FavoriteAdapter(context!!, items!!)
                rootView.FavoriteRecyclerView?.adapter = adapter
            } else
                getFavorites()
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
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
