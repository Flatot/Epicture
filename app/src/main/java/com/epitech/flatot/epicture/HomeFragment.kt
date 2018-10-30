package com.epitech.flatot.epicture

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Interface.ILoadMore
import com.epitech.flatot.epicture.Interface.ImgurService
import com.epitech.flatot.epicture.Model.ImgurInterface
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_cardview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class HomeFragment : Fragment(), Callback<ImgurInterface.Result> {

    var items:MutableList<ImgurInterface.ImgurItem?> = ArrayList()
    lateinit var adapter: LoadingAdapter

    fun OnLoadMore()
    {
        if (items!!.size < 50)
        {
            items!!.add(null)
            adapter.notifyItemRemoved(items.size)
            val index = items.size
            val end = index + 10

            for (i in index until end)
            {
                val name = "test"
                val img_imgur = img_imgur;
                val item = ImgurInterface.ImgurItem(name, "test", name)
                items.add(item)
            }
            adapter.notifyDataSetChanged()
            adapter.setLoaded()
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

        //recyclerViewMyPic.layoutManager = LinearLayoutManager(context)
        //adapter = LoadingAdapter(recyclerViewMyPic, this, items)

        //adapter.setLoadMore()
    }

    fun getAlbums()
    {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val imgurApi = retrofit.create(ImgurService::class.java)

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
                val item = ImgurInterface.ImgurItem(change.title, "test", change.description)
                items.add(item)
            }
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
