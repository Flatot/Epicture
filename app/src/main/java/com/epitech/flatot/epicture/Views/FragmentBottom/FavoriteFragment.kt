package com.epitech.flatot.epicture.Views.FragmentBottom


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.FavoriteAdapter
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Adapter.SearchAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface

import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.fragment_favorite.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteFragment : Fragment(), Callback<ImgurInterface.GetFavoriteResult> {

    var items: MutableList<ImgurInterface.ImgurFavoriteItem>? = ArrayList()

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
            picList!!.data.forEach {
                pic ->
                val item = ImgurInterface.ImgurFavoriteItem(pic)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        getFavorites()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view!!, savedInstanceState)
    }
}
