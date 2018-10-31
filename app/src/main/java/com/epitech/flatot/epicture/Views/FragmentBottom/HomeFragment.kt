package com.epitech.flatot.epicture.Views.FragmentBottom

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.R.id.img_imgur
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), Callback<ImgurInterface.Result> {

    var items:MutableList<ImgurInterface.ImgurItem?> = ArrayList()
    lateinit var adapter: LoadingAdapter

    fun OnLoadMore()
    {
        if (items!!.size < 50)
        {
            items!!.add(null)
            adapter.notifyItemRemoved(items.size)

            Handler().postDelayed({
                val index = items.size
                val end = index + 10

                for (i in index until end)
                {
                    val name = "test"
                    val img_imgur = img_imgur
                    val item = ImgurInterface.ImgurItem(name, "test", name)
                    items.add(item)
                }
                adapter.notifyDataSetChanged()
                adapter.setLoaded()
            },3000)
        }
        else
        {
            Toast.makeText(context, "Max data is 50", Toast.LENGTH_SHORT).show()
        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        if (response.isSuccessful()) {
            val changesList = response.body()
            changesList!!.data.forEach {
                change -> System.out.println(change.link)
                //Toast.makeText(context, change.link, Toast.LENGTH_SHORT).show()

                //description.text = change.description
                //title.text = change.title
                //Picasso.with(context).load(change.link).into(img_imgur)
                val item = ImgurInterface.ImgurItem("test", change.link, "test")
                items.add(item)
            }
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = LoadingAdapter(recyclerView, this, items)

            adapter.setLoadMore()
        } else {
            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
            System.out.println(response.errorBody())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view!!, savedInstanceState)
    }
}
