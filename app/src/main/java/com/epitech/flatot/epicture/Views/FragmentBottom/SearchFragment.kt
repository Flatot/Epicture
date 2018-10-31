package com.epitech.flatot.epicture.Views.FragmentBottom

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_cardview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(), Callback<ImgurInterface.SearchResult> {

    var items:MutableList<ImgurInterface.SearchResult?> = ArrayList()
    lateinit var adapter: LoadingAdapter


    companion object {
        fun newInstance(access_token: String): SearchFragment {
            val args = Bundle()
            args.putString("access_token", access_token)
            val fragment = SearchFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_search, container, false)
        return rootView
    }

    fun GetSearch() {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        var _window = "all"
        var _sort = "q_all"
        var _page = ""
        var _query = id_input_search.text.toString()
        val call = imgurApi.searchGallery("Bearer " + token, _sort, _window, _page, _query)
        call.enqueue(this)
    }

    override fun onFailure(call: Call<ImgurInterface.SearchResult>, t: Throwable) {
    }

    override fun onResponse(call: Call<ImgurInterface.SearchResult>, response: Response<ImgurInterface.SearchResult>) {
        if (response.isSuccessful) {
            val picLists = response.body()
            picLists!!.data.forEach {
                pic -> println(pic.id)
                println(pic.title)
                println(pic.views)
                println(pic.favorite)
            }
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
        else {
            System.out.println(response.errorBody())
        }
    }
}
