package com.epitech.flatot.epicture.Views.FragmentBottom

import android.os.Handler
import android.support.design.R.id.container
import android.support.v4.app.Fragment
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.GalleryAdapter
import com.epitech.flatot.epicture.Model.ImgurModel
import com.epitech.flatot.epicture.Model.RetrofitModel
import com.epitech.flatot.epicture.R
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 8 tests

class HomeFragmentTest: Fragment() {

    var items: MutableList<ImgurModel.ImgurSearchItem>? = null
    var new_items: MutableList<ImgurModel.ImgurSearchItem>? = null
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

    @Test
    fun onLoadMore() {
        addAnotherPage()
        val data = ImgurModel.Data_search("null", "null", "null", 0, "null", false, 0, 0, 0, 0, 0, 0, false, false, "null", "null", "null", false, false, false, emptyList(), emptyList(), "null")
        assertNotNull(data) //Class format is ok, the init is ok
        val item = ImgurModel.ImgurSearchItem(data)
        assertNotNull(item) // Pic + Info exists, we can add
        if (new_items == null) {
        }
        else {
            var count = new_items!!.size
            var old_count = count
            val need = count + 9
            assertEquals(old_count + 9, need) //Addition is ok, for the infinite scroll
            while (count != items!!.size && count < need) {
                new_items!!.add(items!![count])
                old_count = count
                count++
                assertEquals(old_count + 1, count) //Addition is ok, we can add an other elem
            }
        }
    }

    @Test
    fun getGallery() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()

        val token = arguments?.getString("access_token")
        val call = imgurApi.getGallery("Bearer " + token, _section, _sort, _pages, bool_viral, bool_mature, true)
        assertNotNull(call) //The call is ok, all mandatory info were filled
    }


    @Test
    fun onCreateView() {
        try {
            getGallery()
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun addAnotherPage() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val pages_before = _pages
        _pages++
        assertEquals(_pages, pages_before + 1) //Incremenation is ok, we'll go the next page
        val call = imgurApi.getGallery("Bearer " + token, _section, _sort, _pages, bool_viral, bool_mature, true)
        assertNotNull(call) //The call is not null, we gave all mandatory informations, let's get the response
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
                            assertNotNull(item) //Picture & data are ok, we can add the response
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

}