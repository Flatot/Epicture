package com.epitech.flatot.epicture.Views.FragmentBottom


import android.support.design.R.id.container
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.FavoriteAdapter
import com.epitech.flatot.epicture.Model.ImgurModel
import com.epitech.flatot.epicture.Model.RetrofitModel
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.R.id.toolbar
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

// 8 tests

class FavoriteFragmentTest: Fragment() {

    var items: MutableList<ImgurModel.ImgurFavoriteItem>? = null

    @Test
    fun onCreateView() {
        try {
            setHasOptionsMenu(true)
            if (items != null && items!!.isNotEmpty()) {
                val layoutManager = LinearLayoutManager(context)
                val adapter = FavoriteAdapter(context!!, items!!)
                assertNotNull(adapter) // Check the creation of the adapter
                assertNotNull(layoutManager) // Check the existence of the layout manager
            } else
                getFavorites()
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun getFavorites() { //, Callback<ImgurModel.GetFavoriteResult>
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi)
        val token = arguments?.getString("access_token")
        val call = imgurApi.getFavorite("Bearer " + token)
        assertNotNull(call) //The call is not null, we can go further and check get the response
        call.enqueue(object: Callback<ImgurModel.GetFavoriteResult> {
            override fun onFailure(call: Call<ImgurModel.GetFavoriteResult>, t: Throwable) {
            }

            override fun onResponse(call: Call<ImgurModel.GetFavoriteResult>, response: Response<ImgurModel.GetFavoriteResult>) {
                try {
                    if (response.isSuccessful) {
                        val picList = response.body()
                        items = ArrayList()
                        picList!!.data.forEach { pic ->
                            val item = ImgurModel.ImgurFavoriteItem(pic)
                            assertNotNull(item) //Picture + Infos are not null, we can add in order to print
                            items!!.add(item)
                        }
                        val layoutManager = LinearLayoutManager(context)
                        assertNotNull(layoutManager) // Not null ? We can create the recycler view
                        FavoriteRecyclerView.layoutManager = layoutManager
                        val adapter = FavoriteAdapter(context!!, items!!)
                        assertNotNull(adapter) // Not null so we can link the adapter to the recycler view
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
        })
    }
}