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

class HomeFragment : Fragment(), Callback<ImgurInterface.Result> {

    var items: MutableList<ImgurInterface.ImgurItem>? = ArrayList()

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

    override fun onResponse(call: Call<ImgurInterface.Result>, response: Response<ImgurInterface.Result>) {
        if (response.isSuccessful) {
            items = ArrayList()
            val picList = response.body()
            picList!!.data.forEach {
                pic ->
                val item = ImgurInterface.ImgurItem(pic)
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

    fun openFilters(rootView: View, context: Context) { //jpeg, png, gif && all time, last week && views
        rootView.header_filters.setOnClickListener {
            val myDialog = android.support.v7.app.AlertDialog.Builder(context)
            val myDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_filters, null)
            myDialog.setView(myDialogView)
            //myDialog.setCancelable(false)
            val customDialog = myDialog.create()
            customDialog.show()
            customDialog.back.setOnClickListener {
                customDialog.hide()
            }
            customDialog.save.setOnClickListener {
                customDialog.hide()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
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
