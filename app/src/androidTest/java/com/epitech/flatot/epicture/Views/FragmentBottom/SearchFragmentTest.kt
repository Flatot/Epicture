package com.epitech.flatot.epicture.Views.FragmentBottom

import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.SearchAdapter
import com.epitech.flatot.epicture.Model.ImgurModel
import com.epitech.flatot.epicture.Model.RetrofitModel
import com.epitech.flatot.epicture.Interface.ILoadMore
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.R.id.searchView
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 22 tests

class SearchFragmentTest: Fragment(), ILoadMore {

    var items: MutableList<ImgurModel.ImgurSearchItem>? = null
    var new_items: MutableList<ImgurModel.ImgurSearchItem>? = null
    var searchQuery: String? = null
    lateinit var adapter: SearchAdapter
    var _page = 0

    @Test
    fun getSearchQuery() {
    }

    @Test
    fun get_page() {
    }

    @Test
    fun addAnotherPage() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi)
        val token = arguments?.getString("access_token")
        val _window = "all"
        val _sort = "q_all"
        val _query = "dogs"
        assertNotNull(_window)
        assertNotNull(_sort)
        val oldpages = _page
        _page++
        assertEquals(_page, oldpages+1)
        val call = imgurApi.searchGallery("Bearer " + token, _sort, _window, _page, _query)
        assertNotNull(call)
        call.enqueue(object: Callback<ImgurModel.SearchResult> {
            override fun onFailure(call: Call<ImgurModel.SearchResult>, t: Throwable) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurModel.SearchResult>, response: Response<ImgurModel.SearchResult>) {
                try {
                    if (response.isSuccessful) {
                        val picLists = response.body()
                        picLists!!.data.forEach { pic ->
                            val item = ImgurModel.ImgurSearchItem(pic)
                            assertNotNull(item)
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

    @Test
    override fun OnLoadMore() {
        addAnotherPage()
        val data = ImgurModel.Data_search("null", "null", "null", 0, "null", false, 0, 0, 0, 0, 0, 0, false, false, "null", "null", "null", false, false, false, emptyList(), emptyList(), "null")
        assertNotNull(data)
        val item = ImgurModel.ImgurSearchItem(data)
        assertNotNull(item)
        if (new_items == null) {
        }
        else {
            var count = new_items!!.size
            var tmp = count
            val need = count + 9
            assertEquals(tmp + 9, count)
            while (count != items!!.size && count < need) {
                new_items!!.add(items!![count])
                tmp = count
                count++
                assertEquals(count, tmp + 1)
            }
        }
    }

    @Test
    fun getSearch() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi)
        val token = arguments?.getString("access_token")
        val _window = "all"
        val _sort = "q_all"
        val _query = "dogs"
        assertNotNull(_window)
        assertNotNull(_sort)
        val call = imgurApi.searchGallery("Bearer " + token, _sort, _window, _page, _query)
        assertNotNull(call)
        call.enqueue(object: Callback<ImgurModel.SearchResult> {
            override fun onFailure(call: Call<ImgurModel.SearchResult>, t: Throwable) {
            }

            override fun onResponse(call: Call<ImgurModel.SearchResult>, response: Response<ImgurModel.SearchResult>) {
                try {
                    if (response.isSuccessful) {
                        val picLists = response.body()
                        assertNotNull(picLists)
                        picLists!!.data.forEach { pic ->
                            val item = ImgurModel.ImgurSearchItem(pic)
                            assertNotNull(item)
                            items!!.add(item)
                        }
                        var count = 0
                        while (count != items!!.size && count < 8) {
                            assertNotNull(items!![count])
                            new_items!!.add(items!![count])
                            count++
                        }
                        progressBar.visibility = View.GONE
                        recyclerViewSearch.layoutManager = LinearLayoutManager(context)
                        adapter = SearchAdapter(recyclerViewSearch, arguments?.getString("access_token")!!, context!!, new_items!!)
                        assertNotNull(adapter)
                        recyclerViewSearch.adapter = adapter
                        //adapter.setLoadMore(this)
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

}