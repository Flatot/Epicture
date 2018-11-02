package com.epitech.flatot.epicture.Views.FragmentBottom

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Adapter.SearchAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.item_cardview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(), Callback<ImgurInterface.SearchResult> {

    var items:MutableList<ImgurInterface.ImgurSearchItem> = ArrayList()
    var searchQuery: String? = null

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
        rootView.searchView.queryHint = "Search Pictures in Imgur"
        rootView.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchQuery = query
                GetSearch()
                return true
            }

        })

        return rootView
    }

    fun GetSearch() {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val _window = "all"
        val _sort = "q_all"
        val _page = 1
        val _query = searchQuery!!
        val call = imgurApi.searchGallery("Bearer " + token, _sort, _window, _page, _query)
        call.enqueue(this)
    }

    override fun onFailure(call: Call<ImgurInterface.SearchResult>, t: Throwable) {
        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
    }

    override fun onResponse(call: Call<ImgurInterface.SearchResult>, response: Response<ImgurInterface.SearchResult>) {
        if (response.isSuccessful) {
            val picLists = response.body()
            picLists!!.data.forEach {
                pic ->
                val item = ImgurInterface.ImgurSearchItem(pic)
                items!!.add(item)
            }
            recyclerViewSearch.layoutManager = LinearLayoutManager(context)

            val adapter = SearchAdapter(arguments?.getString("access_token")!!, context!!, items!!)
            recyclerViewSearch.adapter = adapter
        }
        else {
            System.out.println(response.errorBody())
        }
    }
}
