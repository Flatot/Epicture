package com.epitech.flatot.epicture

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_home.*
import okhttp3.Call
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import java.io.IOException
import retrofit2.Callback
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result.response
import retrofit2.adapter.rxjava2.Result
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


class HomeActivity : AppCompatActivity(), Callback<ImgurInterface.Result> {

    private var access_token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        access_token = intent.getStringExtra("access_token")
        textViewtest.text = access_token

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val imgurApi = retrofit.create(ImgurService::class.java)



        val token = access_token
        val call = imgurApi.getUser("Bearer " + token)
        call.enqueue(this)
    }

    override fun onFailure(call: retrofit2.Call<ImgurInterface.Result>, t: Throwable) {
        Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
    }

    override fun onResponse(call: retrofit2.Call<ImgurInterface.Result>, response: Response<ImgurInterface.Result>) {
        if (response.isSuccessful()) {
            val changesList = response.body()
            changesList!!.data.forEach {
                change -> System.out.println(change.link)
                Toast.makeText(this, change.link, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, response.message(), Toast.LENGTH_SHORT).show()
            System.out.println(response.errorBody())
        }
    }
}
